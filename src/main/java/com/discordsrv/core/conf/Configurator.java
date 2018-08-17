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
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import javax.naming.ConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Stream;

/**
 * Configurator, for injecting information from configs.
 */
@SuppressWarnings("WeakerAccess")
public final class Configurator {

    private Configurator() {
        throw new UnsupportedOperationException();
    }

    /**
     * Instantiates a type using a {@link Configured} constructor and injecting values from a config.
     *
     * @param source
     *         The source of the injected values.
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
    public static <T> T constructFromConfig(Map<String, Object> source, Class<T> type)
        throws ConfigurationException, IllegalAccessException, InvocationTargetException, InstantiationException {
        String prefix = type.getName();
        Map<String, Object> reduced = new HashMap<>();
        source.entrySet().stream().filter(entry -> entry.getKey().startsWith(prefix))
            .forEach(entry -> reduced.put(entry.getKey().substring(prefix.length() + 1), entry.getValue()));
        return constructFromReducedConfig(reduced, type);
    }

    /**
     * Instantiates a type using a {@link Configured} constructor and injecting values from a config.
     *
     * @param reduced
     *         The source of the injected values, without the class name prefixed.
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
    public static <T> T constructFromReducedConfig(Map<String, Object> reduced, Class<T> type)
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
        Object[] parameters = new Object[constructor.getParameterCount()];
        List<String> parameterValues = new ArrayList<>(constructor.getParameterCount());
        for (Parameter parameter : constructor.getParameters()) {
            parameterValues.add(parameter.getAnnotation(Val.class).value());
        }
        reduced.forEach((key, value) -> {
            for (int i = 0; i < parameterValues.size(); i++) {
                if (parameterValues.get(i).equals(key)) {
                    parameters[i] = value;
                }
            }
        });
        return type.cast(constructor.newInstance(parameters));
    }

    /**
     * Remaps a config to a different path.
     * <p>
     * Replaces in the format {@code /^<original>/<resultant>/}, where the bracketed items are the passed strings (regex
     * escaped). If the regex shown is not matched, it will be left untouched and placed in the remapped config.
     *
     * @param config
     *         The original config.
     * @param original
     *         The path to replace.
     * @param resultant
     *         The path to replace with.
     *
     * @return remapped The remapped config.
     */
    public static Map<String, Object> remapConfig(Map<String, Object> config, String original, String resultant) {
        Map<String, Object> remapped = new HashMap<>(config.size());
        config.forEach((key, value) -> {
            if (key.startsWith(original)) {
                remapped.put(resultant + key.substring(original.length()), value);
            } else {
                remapped.put(key, value);
            }
        });
        return remapped;
    }

    /**
     * Unreduces a config such that the type name provided is prepended to each member of the resulting config.
     *
     * @param reduced
     *         The reduced config to unreduce.
     * @param type
     *         The type to unreduce from.
     *
     * @return unreduced The unreduced config.
     */
    public static Map<String, Object> unreduceConfig(Map<String, Object> reduced, Class<?> type) {
        return remapConfig(reduced, "", type.getName() + ".");
    }

    /**
     * Converts a recursive tree map to a map with a period separator.
     *
     * @param config
     *         The tree map config which this is loaded from.
     *
     * @return map The converted map.
     */
    public static Map<String, Object> flatten(LinkedHashMap<String, Map<String, Object>> config) {
        HashMap<String, Object> map = new HashMap<>();
        config.forEach((key, value) -> {
            toMapRecurse(key, value, map);
        });
        return map;
    }

    @SuppressWarnings("unchecked")
    private static void toMapRecurse(String key, Map<String, Object> map, Map<String, Object> target) {
        map.forEach((mapKey, value) -> {
            if (value instanceof Map) {
                toMapRecurse(String.format("%s.%s", key, mapKey), (Map<String, Object>) value, target);
            } else {
                target.put(String.format("%s.%s", key, mapKey), value);
            }
        });
    }

    /**
     * Creates a configuration by merging the passed yamls.
     *
     * @param yaml
     *         The yaml parser to use.
     * @param streams
     *         The input streams to load the yamls from, in ascending order of priority (first will be overwritten by
     *         last).
     *
     * @return config The constructed and merged config.
     *
     * @throws IOException
     *         If an exception occurs while attempting to load/parse the yaml documents.
     */
    public static Map<String, Object> createConfig(Yaml yaml, InputStream... streams) throws IOException {
        Stream<InputStream> streamList = Arrays.stream(streams);
        IOException exception = new IOException();
        Stream<Map<String, Object>> mapStream = streamList.map(stream -> {
            try (Reader reader = new InputStreamReader(stream)) {
                return flatten(yaml.load(reader));
            } catch (IOException | YAMLException e) {
                exception.addSuppressed(e);
                return Collections.emptyMap();
            }
        });
        if (exception.getSuppressed().length > 0) {
            throw exception;
        }
        return mergeConfigs(mapStream);
    }

    /**
     * Merges 0..many configs together.
     *
     * @param configs
     *         The configs to merge.
     *
     * @return merged The merged configs.
     */
    public static Map<String, Object> mergeConfigs(Stream<Map<String, Object>> configs) {
        Map<String, Object> res = new HashMap<>();
        configs.forEach(res::putAll);
        return res;
    }

}
