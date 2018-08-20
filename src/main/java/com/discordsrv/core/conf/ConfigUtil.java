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

import com.discordsrv.core.conf.collect.ParentAwareHashMap;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.stream.Stream;

/**
 * ConfigUtil, for injecting information from configs.
 */
@SuppressWarnings("WeakerAccess")
@CheckReturnValue
@ParametersAreNonnullByDefault
public final class ConfigUtil {

    private ConfigUtil() {
        throw new UnsupportedOperationException();
    }

    /**
     * Traverse into a target with a given path.
     *
     * @param target
     *         The target to traverse into.
     * @param path
     *         The path to traverse to.
     *
     * @return result The result of the traversal.
     */
    @SuppressWarnings("unchecked")
    public static Object traverseInto(@Nullable Object target, LinkedList<String> path) {
        if (path.isEmpty() || target == null) {
            return target;
        } else {
            return traverseInto(((Map<String, Object>) target).get(path.removeFirst()), path);
        }
    }

    /**
     * Remaps a config to a different path.
     * <p>
     * Replaces in the format {@code /^<original>/<resultant>/}, where the bracketed items are the passed strings (regex
     * escaped). If the regex shown is not matched, it will be left untouched and placed in the remapped config.
     *
     * @param config
     *         The original config.
     * @param remap
     *         The map of strings to replace (key = original, value = resultant).
     *
     * @return remapped The remapped config.
     */
    public static ParentAwareHashMap remapConfig(ParentAwareHashMap config, Map<String, String> remap) {
        Map<String, Object> flattened = flatten(Collections.singleton(config)); // required for path replacement
        Map<String, Object> remapped = new HashMap<>(flattened.size());
        remap.forEach((original, resultant) -> config.forEach((key, value) -> {
            if (key.startsWith(original)) {
                remapped.put(resultant + key.substring(original.length()), value);
            } else {
                remapped.put(key, value);
            }
        }));
        return unflatten(remapped);
    }

    /**
     * Unreduces a config such that the type name provided is prepended to each member of the resulting config.
     * <p>
     * This modifies the passed config!
     *
     * @param reduced
     *         The reduced config to unreduce.
     * @param type
     *         The type to unreduce from.
     *
     * @return unreduced The unreduced config.
     */
    public static ParentAwareHashMap unreduceConfig(ParentAwareHashMap reduced, Class<?> type) {
        ParentAwareHashMap root = new ParentAwareHashMap(null, null);
        ParentAwareHashMap result = createRecursive(root, new LinkedList<>(Arrays.asList(type.getName().split("\\."))));
        result.put(type.getSimpleName(), reduced);
        reduced.setParent(result);
        reduced.setName(type.getSimpleName());
        return root;
    }

    /**
     * Converts a recursive tree map to a map with a period separator. Pass the result of yaml#loadAll
     *
     * @param config
     *         The tree map config which this is loaded from.
     *
     * @return map The converted map.
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> flatten(Iterable<Object> config) {
        HashMap<String, Object> map = new HashMap<>();
        config.forEach(configEntry -> ((Map) configEntry).forEach((key, value) -> {
            if (value instanceof Map) {
                toMapRecurse((String) key, (Map<String, Object>) value, map);
            } else {
                map.put((String) key, value);
            }
        }));
        return map;
    }

    /**
     * Unflattens a path-based map into a ParentAwareHashMap (the kind used to construct from configs).
     *
     * @param flattened
     *         A flattened config.
     *
     * @return unflattened An unflattened config.
     */
    public static ParentAwareHashMap unflatten(Map<String, Object> flattened) {
        ParentAwareHashMap result = new ParentAwareHashMap(null, null);
        flattened.forEach((key, value) -> {
            LinkedList<String> path = splitPath(key);
            if (path.size() == 1) {
                result.put(path.removeFirst(), value);
            } else {
                createRecursive(result, path).put(path.removeFirst(), value);
            }
        });
        return result;
    }

    /**
     * Recursively creates ParentAwareHashMap instances within parents.
     *
     * @param parent
     *         The parent map.
     * @param path
     *         The path to traverse.
     *
     * @return traversed The traversed and created map.
     */
    public static ParentAwareHashMap createRecursive(ParentAwareHashMap parent, LinkedList<String> path) {
        if (path.size() == 1) {
            return parent;
        } else {
            ParentAwareHashMap self =
                (ParentAwareHashMap) parent.computeIfAbsent(path.removeFirst(), s -> new ParentAwareHashMap(parent, s));
            return createRecursive(self, path);
        }
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
    public static ParentAwareHashMap createConfig(Yaml yaml, InputStream... streams) throws IOException {
        Stream<InputStream> streamList = Arrays.stream(streams);
        IOException exception = new IOException();
        Stream<ParentAwareHashMap> mapStream = streamList.map(stream -> {
            try (Reader reader = new InputStreamReader(stream)) {
                return unflatten(flatten(yaml.loadAll(reader))); // to resolve names like "game.name"
            } catch (IOException | YAMLException e) {
                exception.addSuppressed(e);
                return new ParentAwareHashMap(null, null);
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
    public static ParentAwareHashMap mergeConfigs(Stream<ParentAwareHashMap> configs) {
        ParentAwareHashMap res = new ParentAwareHashMap(null, null);
        configs.forEach(config -> mergeConfigRecurse(config, res));
        return res;
    }

    private static void mergeConfigRecurse(ParentAwareHashMap config, ParentAwareHashMap merger) {
        config.forEach((key, value) -> {
            if (value instanceof ParentAwareHashMap) {
                ParentAwareHashMap insert =
                    (ParentAwareHashMap) merger.computeIfAbsent(key, s -> new ParentAwareHashMap(merger, s));
                mergeConfigRecurse((ParentAwareHashMap) value, insert);
            } else {
                merger.put(key, value);
            }
        });
    }

    /**
     * Splits the path with the . delimeter.
     *
     * @param path
     *         The path to split
     *
     * @return splitPath The path broken into a linked list for use with ConfigUtil.
     */
    public static LinkedList<String> splitPath(final String path) {
        return new LinkedList<>(Arrays.asList(path.split("\\.")));
    }

}
