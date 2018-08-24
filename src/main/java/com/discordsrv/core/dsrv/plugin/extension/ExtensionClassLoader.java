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
package com.discordsrv.core.dsrv.plugin.extension;

import com.discordsrv.core.api.dsrv.Context;
import com.discordsrv.core.api.dsrv.platform.Platform;
import com.discordsrv.core.api.dsrv.platform.extension.Extension;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

/**
 * ExtensionClassLoader type, for loading extensions from jar files.
 *
 * @param <T>
 *         The type of Context to be used by extensions loaded by this extension class loader.
 * @param <V>
 *         The type of DSRVPlugins to be used by extensions loaded by this extension class loader.
 */
@ParametersAreNonnullByDefault
public class ExtensionClassLoader<T extends Context, V extends Platform<T>> extends URLClassLoader {

    private final List<Extension<T, V>> extensions;

    /**
     * Main constructor of the ExtensionClassLoader type.
     *
     * @param parent
     *         The parent class loader.
     */
    public ExtensionClassLoader(final ClassLoader parent) {
        super(new URL[0], parent);
        extensions = new LinkedList<>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addURL(final URL url) {
        try {
            ExtensionJarFile jarFile = new ExtensionJarFile(new File(url.toURI()));
            String classname = jarFile.getManifest().getMainAttributes().getValue("Extension-Class");
            if (classname == null) {
                throw new IllegalArgumentException("Extension class attribute was not found.");
            }
            super.addURL(url);
            extensions.add((Extension<T, V>) this.loadClass(classname).getDeclaredConstructor().newInstance());
        } catch (Throwable t) {
            throw new IllegalArgumentException("URL provided does not lead to a valid platform extension jar.", t);
        }
    }

    public Stream<Extension<T, V>> getExtensions() {
        return extensions.stream();
    }

}
