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

/**
 * An example configured type.
 */
public class ConfiguredType {

    private final String name;
    private final String value;

    /**
     * A configured constructor.
     *
     * @param name
     *         The name to be tested.
     * @param value
     */
    @Configured
    public ConfiguredType(final @Val("name") String name, final @Val("value") String value) {
        this.name = name;
        this.value = value;
    }

    String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
