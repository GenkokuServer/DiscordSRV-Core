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
package com.discordsrv.core.api.dsrv.platform.extension;

import com.discordsrv.core.api.dsrv.Context;
import com.discordsrv.core.api.dsrv.platform.Platform;

/**
 * Extension type, for extending DSRV plugins.
 *
 * @param <T>
 *         The context type that this Extension will use.
 * @param <V>
 *         The plugin type that this Extension will use.
 */
public interface Extension<T extends Context, V extends Platform<T>> {

    /**
     * Sets the plugin that this extension should use.
     *
     * @param plugin
     *         The plugin described.
     */
    void setPlugin(V plugin);

    /**
     * Fetches the name of this extension.
     *
     * @return name The name of this extension.
     */
    String getName();

}
