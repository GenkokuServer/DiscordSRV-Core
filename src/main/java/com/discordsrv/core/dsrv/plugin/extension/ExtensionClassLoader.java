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
package com.discordsrv.core.dsrv.plugin.extension;

import javax.annotation.ParametersAreNonnullByDefault;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * ExtensionClassLoader type, for loading extensions from jar files.
 */
@ParametersAreNonnullByDefault
public class ExtensionClassLoader extends URLClassLoader {

    /**
     * Main constructor of the ExtensionClassLoader type.
     *
     * @param parent
     *         The parent class loader.
     */
    public ExtensionClassLoader(final ClassLoader parent) {
        super(new URL[0], parent);
    }

    @Override
    public void addURL(final URL url) {
        super.addURL(url);
    }

}
