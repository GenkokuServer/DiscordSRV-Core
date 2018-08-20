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
package com.discordsrv.core.conf.collect;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Objects;

/**
 * ParentAwareHashMap, for maps that know their roots (in trees of maps).
 */
public class ParentAwareHashMap extends HashMap<String, Object> {

    private ParentAwareHashMap parent;
    private String name;

    /**
     * Child constructor of {@link HashMap#HashMap(int, float)}.
     *
     * @param initialCapacity
     *         See {@link HashMap#HashMap(int, float)}
     * @param loadFactor
     *         See {@link HashMap#HashMap(int, float)}
     * @param parent
     *         The parent of this map.
     * @param name
     *         The name of this map.
     */
    public ParentAwareHashMap(final int initialCapacity, final float loadFactor, final ParentAwareHashMap parent,
                              final String name) {
        super(initialCapacity, loadFactor);
        this.parent = parent;
        this.name = name;
    }

    /**
     * Child constructor of {@link HashMap#HashMap(int)}.
     *
     * @param initialCapacity
     *         See {@link HashMap#HashMap(int)}
     * @param parent
     *         The parent of this map.
     * @param name
     *         The name of this map.
     */
    public ParentAwareHashMap(final int initialCapacity, final ParentAwareHashMap parent, final String name) {
        super(initialCapacity);
        this.parent = parent;
        this.name = name;
    }

    /**
     * Child constructor of {@link HashMap#HashMap)}.
     *
     * @param parent
     *         The parent of this map.
     * @param name
     *         The name of this map.
     */
    public ParentAwareHashMap(final ParentAwareHashMap parent, final String name) {
        super();
        this.parent = parent;
        this.name = name;
    }

    public ParentAwareHashMap getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    public void setParent(final ParentAwareHashMap parent) {
        this.parent = parent;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public ParentAwareHashMap clone() {
        final ParentAwareHashMap map = (ParentAwareHashMap) super.clone();
        this.forEach((key, value) -> {
            if (value instanceof ParentAwareHashMap) {
                map.compute(key, (s, existing) -> {
                    ParentAwareHashMap val = ((ParentAwareHashMap) value).clone();
                    val.setName(((ParentAwareHashMap) value).name);
                    val.setParent(map);
                    return val;
                });
            } else {
                map.put(key, value);
            }
        });
        return map;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(@Nullable Object o) {
        return o instanceof ParentAwareHashMap && (
            ((ParentAwareHashMap) o).parent == null && parent == null && o.toString().equals(this.toString()) || Objects
                .equals(((ParentAwareHashMap) o).parent, parent)); // We only really want this true for clones.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        throw new UnsupportedOperationException();
    }

    /**
     * Traverses into a map, creating if necessary.
     *
     * @param name
     *         The name of the child map.
     *
     * @return map The child map.
     *
     * @throws IllegalArgumentException
     *         If the entry exists, but not as a map.
     */
    public ParentAwareHashMap in(String name) {
        Object value = this.computeIfAbsent(name, s -> new ParentAwareHashMap(this, s));
        if (value instanceof ParentAwareHashMap) {
            return (ParentAwareHashMap) value;
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Traversal helper for effectively .. indexing.
     *
     * @return The parent map.
     */
    public ParentAwareHashMap up() {
        return parent;
    }

}
