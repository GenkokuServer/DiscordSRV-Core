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

import java.util.HashMap;

/**
 * ParentAwareHashMap, for maps that know their roots (in trees of maps).
 */
public class ParentAwareHashMap extends HashMap<String, Object> {

    private final ParentAwareHashMap parent;
    private final String name;

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

}
