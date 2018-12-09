/*
 * DiscordSRV-Core: A library for generic Minecraft plugin development for all DiscordSRV projects
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
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualTreeBidiMap;

import javax.annotation.Nonnull;

/**
 * An example configured type with a map.
 */
public class MapConfiguredType {

    private final BidiMap<String, String> map;

    /**
     * A configured constructor.
     *
     * @param map
     *         The map to be tested.
     */
    @Configured
    public MapConfiguredType(final @Nonnull @Val("map") DualTreeBidiMap<String, String> map) {
        this.map = map;
    }

    public BidiMap<String, String> getMap() {
        return map;
    }
}
