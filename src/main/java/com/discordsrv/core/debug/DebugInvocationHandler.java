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
package com.discordsrv.core.debug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * DebugInvocationHandler, for debugging invocations.
 */
public class DebugInvocationHandler implements InvocationHandler, DebugProxy {

    private final Debugger debugger;
    private final Object proxied;
    private final Logger logger;

    /**
     * Main constructor for the DebugInvocationHandler type.
     *
     * @param debugger
     *         The debugger which created this proxy.
     * @param proxied
     *         The object being proxied.
     */
    public DebugInvocationHandler(final Debugger debugger, final Object proxied) {
        this.debugger = debugger;
        this.proxied = proxied;
        this.logger = LoggerFactory.getLogger(String.format("DSRVMethodProxy: %s", proxied.getClass().getName()));
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) {
        Debugger.logInvocation(method, args, logger);
        try {
            if (method.getReturnType().isInterface()) {
                return debugger.getProxy(method.invoke(proxy, args), method.getReturnType());
            } else {
                return method.invoke(proxy, args);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object getProxied() {
        return proxied;
    }
}
