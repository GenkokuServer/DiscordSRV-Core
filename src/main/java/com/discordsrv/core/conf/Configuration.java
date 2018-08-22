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
package com.discordsrv.core.conf;

import com.discordsrv.core.conf.annotation.Configured;
import com.discordsrv.core.conf.annotation.Val;
import com.discordsrv.core.conf.collect.ParentAwareHashMap;
import com.discordsrv.core.debug.Debugger;
import com.discordsrv.core.discord.DSRVJDABuilder;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.naming.ConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.*;
import java.util.stream.Stream;

import static com.discordsrv.core.conf.ConfigUtil.*;

/**
 * Configuration type, for creating configurations.
 */
@ParametersAreNonnullByDefault
public class Configuration {

    private final Yaml yaml;
    private final Debugger debugger;
    private ParentAwareHashMap source;

    /**
     * Main constructor for the Configuration type.
     */
    public Configuration(final Yaml yaml, final ParentAwareHashMap source, final boolean debug) {
        this.yaml = yaml;
        this.source = source;
        this.debugger = new Debugger(debug);
    }

    private static ParentAwareHashMap applyDefaultRemappings(final ParentAwareHashMap map) {
        Map<String, String> defaultRemappings = new HashMap<>();
        defaultRemappings.put("bot", DSRVJDABuilder.class.getName());
        defaultRemappings.put("debug", Debugger.class.getName() + ".debug");
        return remapConfig(map, defaultRemappings);
    }

    /**
     * Creates a configuration using the default yamls.
     *
     * @param yaml
     *         The yaml parser to use.
     * @param userConfigURLs
     *         The URLs to load as user configs.
     *
     * @return config The configuration constructed.
     *
     * @throws IOException
     *         If a resource fails to load.
     */
    public static Configuration getStandardConfiguration(final Yaml yaml, final URL... userConfigURLs)
        throws IOException {
        List<InputStream> streams = new LinkedList<>();
        for (URL userConfigURL : userConfigURLs) {
            InputStream openStream = userConfigURL.openStream();
            streams.add(openStream);
        }
        ParentAwareHashMap universal = applyDefaultRemappings(createConfig(yaml,
            Configuration.class.getClassLoader().getResourceAsStream("dsrv/universal/default.yaml")));
        ParentAwareHashMap userConfig = applyDefaultRemappings(createConfig(yaml, streams.toArray(new InputStream[0])));
        ParentAwareHashMap langFetcher = mergeConfigs(Stream.of(universal, userConfig));
        String language = (String) traverseInto(langFetcher, splitPath("lang"));
        ParentAwareHashMap merged = mergeConfigs(Stream.of(universal, createConfig(yaml,
            Configuration.class.getClassLoader().getResourceAsStream("dsrv/locales/" + language + "/default.yaml")),
            userConfig));
        Boolean debug = (Boolean) ConfigUtil.traverseInto(merged, splitPath(Debugger.class.getName() + ".debug"));
        if (debug == null) {
            debug = false;
        }
        return new Configuration(yaml, merged, debug);
    }

    /**
     * Apply a remapping to this config.
     *
     * @param remapping
     *         The remapping to apply.
     */
    public void applyRemapping(Map<String, String> remapping) {
        this.source = remapConfig(this.source, remapping);
    }

    /**
     * Instantiates a type using a {@link Configured} constructor and injecting values from a config.
     *
     * @param type
     *         The type to be instantiated.
     * @param extras
     *         Extra values to inject.
     * @param <T>
     *         The type argument of the instantiated type.
     *
     * @return instance A new instance constructed via the config.
     *
     * @throws ConfigurationException
     *         If there are no constructors with the {@link Configured} annotation.
     * @throws IllegalAccessException
     *         If the constructor attempted to be used is not accessible.
     * @throws InvocationTargetException
     *         Shouldn't happen, but inherited from {@link Constructor#newInstance(Object...)}.
     * @throws InstantiationException
     *         If instantiation of the type fails.
     */
    public <T> T create(Class<T> type, Object... extras)
        throws ConfigurationException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return createReduced((ParentAwareHashMap) traverseInto(source, splitPath(type.getName())), type, extras);
    }

    /**
     * Instantiates a type using a {@link Configured} constructor and injecting values from a config.
     *
     * @param reduced
     *         The source of the injected values, without the class name prefixed.
     * @param extras
     *         Extra values to inject.
     * @param type
     *         The type to be instantiated.
     * @param <T>
     *         The type argument of the instantiated type.
     *
     * @return instance A new instance constructed via the config.
     *
     * @throws ConfigurationException
     *         If there are no constructors with the {@link Configured} annotation.
     * @throws IllegalAccessException
     *         If the constructor attempted to be used is not accessible.
     * @throws InvocationTargetException
     *         Shouldn't happen, but inherited from {@link Constructor#newInstance(Object...)}.
     * @throws InstantiationException
     *         If instantiation of the type fails.
     */
    public <T> T createReduced(ParentAwareHashMap reduced, Class<T> type, Object... extras)
        throws ConfigurationException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<?> constructor = null;
        for (Constructor<?> declared : type.getDeclaredConstructors()) {
            if (declared.isAnnotationPresent(Configured.class)) {
                constructor = declared;
                break;
            }
        }
        if (constructor == null) {
            throw new ConfigurationException("No @Configured annotation present on any declared constructors.");
        }
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Object[] parameters = new Object[constructor.getParameterCount()];
        LinkedList<String> parameterValues = new LinkedList<>();
        List<Object> extraList = new LinkedList<>(Arrays.asList(extras));
        for (Parameter parameter : constructor.getParameters()) {
            parameterValues.add(parameter.getAnnotation(Val.class).value());
        }
        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = traverseInto(reduced, splitPath(parameterValues.removeFirst()));
            if (parameters[i] == null) {
                for (Object obj : extraList) {
                    if (parameterTypes[i].isAssignableFrom(obj.getClass())) {
                        parameters[i] = obj;
                        break;
                    }
                }
            }
        }
        T returned;
        if (debugger != null && debugger.isDebugging()) {
            returned = debugger.getProxy(type, constructor.getParameterTypes(), parameters);
        } else {
            returned = type.cast(constructor.newInstance(parameters));
        }
        if (returned instanceof ConfigAware) {
            ((ConfigAware) returned).setConfig(this);
        }
        return returned;
    }

    /**
     * Dumps the current config to a print stream.
     *
     * @param stream
     *         The print stream to dump the config to.
     */
    public void dumpConfig(PrintStream stream) {
        stream.println(yaml.dump(source)
            .replace((String) traverseInto(source, splitPath(DSRVJDABuilder.class.getName() + ".token")),
                "[redacted]"));
    }

    /**
     * Add a config to the current config.
     *
     * @param source
     *         The source of the config.
     */
    public void addConfig(ParentAwareHashMap source) {
        this.source = mergeConfigs(Stream.of(this.source, source));
    }

    public Yaml getYaml() {
        return yaml;
    }

    public Debugger getDebugger() {
        return debugger;
    }

}
