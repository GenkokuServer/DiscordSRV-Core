/*
 * DiscordSRV2-Core: A library for generic Minecraft plugin development for all DiscordSRV2 projects
 * Copyright (C) 2018 DiscordSRV
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.discordsrv.core.debug.proxy;

import com.discordsrv.core.debug.Debugger;
import com.discordsrv.core.debug.annotation.DisableDebug;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * DebugMethodInterceptor type, for intercepting the methods of {@link net.sf.cglib.proxy.Enhancer} based proxies.
 */
public class DebugMethodInterceptor implements MethodInterceptor {

    private final Logger logger = LoggerFactory.getLogger("DSRVMethodInterceptor");
    private final Debugger debugger;

    /**
     * Main constructor for the DebugMethodInterceptor type.
     *
     * @param debugger
     *         The debugger which created this method interceptor.
     */
    public DebugMethodInterceptor(final Debugger debugger) {
        this.debugger = debugger;
    }

    @Override
    public Object intercept(final Object proxy, final Method method, final Object[] args, MethodProxy methodProxy)
        throws Throwable {
        if (method.getAnnotation(DisableDebug.class) == null) {
            Debugger.logInvocation(method, args, logger);
        }
        if (method.getReturnType().isInterface()) {
            return debugger.getProxy(methodProxy.invokeSuper(proxy, args), method.getReturnType());
        } else {
            return methodProxy.invokeSuper(proxy, args);
        }
    }
}
