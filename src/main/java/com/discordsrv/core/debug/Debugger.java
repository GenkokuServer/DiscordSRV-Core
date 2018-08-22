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

import com.discordsrv.core.conf.annotation.Configured;
import com.discordsrv.core.conf.annotation.Val;
import com.discordsrv.core.debug.proxy.DebugInvocationHandler;
import com.discordsrv.core.debug.proxy.DebugMethodInterceptor;
import com.discordsrv.core.debug.proxy.DebugProxy;
import net.sf.cglib.proxy.Enhancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Debugger type, for debugging with the use of proxies.
 */
public final class Debugger {

    private final Logger logger;
    private final boolean debug;
    private @Nonnull final List<DebugProxy> proxies;
    private @Nonnull final Map<Class<?>, Enhancer> enhancerMap;
    private final DebugMethodInterceptor interceptor;

    /**
     * Main configured constructor for the Debugger type.
     *
     * @param debug
     *         Whether or not we're debugging.
     */
    @Configured
    public Debugger(final @Val("debug") boolean debug) {
        this.logger = LoggerFactory.getLogger("DSRVDebugger");
        this.debug = debug;
        this.proxies = new LinkedList<>();
        this.enhancerMap = new HashMap<>();
        this.interceptor = new DebugMethodInterceptor(this);
    }

    /**
     * Log the invocation of a method.
     *
     * @param method
     *         The method invoked.
     * @param args
     *         The arguments passed to the method.
     * @param logger
     *         The logger used for logging.
     */
    public static void logInvocation(Method method, Object[] args, Logger logger) {
        StringBuilder builder = new StringBuilder(method.getDeclaringClass().getSimpleName());
        builder.append('#').append(method.getName()).append('(');
        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length - 1; i++) {
                builder.append(args[i]).append(", ");
            }
            builder.append(args[args.length - 1]);
        }
        builder.append(')');
        builder.append(" invoked.");
        logger.debug(builder.toString());
    }

    /**
     * Creates a {@link Enhancer}-based proxy.
     *
     * @param supertype
     *         The supertype to use for this proxy.
     * @param parameters
     *         The parameters of construction.
     * @param arguments
     *         The arguments for construction.
     * @param <T>
     *         The type of proxy to create.
     *
     * @return proxy The enhanced proxy.
     */
    @SuppressWarnings("unchecked")
    public synchronized <T> T getProxy(Class<T> supertype, Class<?>[] parameters, Object[] arguments) {
        if (Enhancer.isEnhanced(supertype)) {
            return (T) enhancerMap.get(supertype.getSuperclass()).create(parameters, arguments);
        }
        Enhancer enhancer = enhancerMap.computeIfAbsent(supertype, compute -> {
            Enhancer created = new Enhancer();
            created.setSuperclass(supertype);
            created.setCallback(interceptor);
            logProxyTypeCreation(supertype);
            return created;
        });
        Object proxy = enhancer.create(parameters, arguments);
        logProxyCreation(supertype);
        return (T) proxy;
    }

    /**
     * Fetches an interface {@link Proxy} for reflective invocation wrapping.
     *
     * @param obj
     *         The object to proxy calls for.
     * @param types
     *         The target types of this proxy.
     *
     * @return proxy The proxy instance.
     */
    @SuppressWarnings("unchecked")
    public synchronized Object getProxy(Object obj, Class<?>... types) {
        if (Proxy.isProxyClass(obj.getClass())) {
            return obj;
        }
        List<Class<?>> typeList = new ArrayList<>(Arrays.asList(types));
        typeList.add(DebugProxy.class);
        for (DebugProxy proxy : proxies) {
            if (proxy.getProxied() == obj && typeList.stream()
                .allMatch(type -> type.isAssignableFrom(proxy.getClass()))) {
                return proxy;
            }
        }
        DebugProxy proxy = (DebugProxy) Proxy
            .newProxyInstance(obj.getClass().getClassLoader(), typeList.toArray(new Class[0]),
                new DebugInvocationHandler(this, obj));
        proxies.add(proxy);
        logProxyCreation(types);
        return proxy;
    }

    public boolean isDebugging() {
        return debug;
    }

    /**
     * Log the creation of a proxy.
     *
     * @param type
     *         The type which is being proxied.
     */
    public void logProxyCreation(Class<?>... type) {
        logger.debug(Arrays.stream(type).map(Class::getSimpleName).collect(Collectors.toList()) + " proxy created.");
    }

    /**
     * Log the creation of a proxy type.
     *
     * @param type
     *         The type which is being proxied.
     */
    public void logProxyTypeCreation(Class<?>... type) {
        logger
            .debug(Arrays.stream(type).map(Class::getSimpleName).collect(Collectors.toList()) + " proxy type created.");
    }
}
