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

import com.discordsrv.core.api.dsrv.DiscordSRVContext;
import com.discordsrv.core.api.dsrv.plugin.DSRVPlugin;
import com.discordsrv.core.api.dsrv.plugin.extension.DSRVExtension;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * ExtensionsLoader utility, for fetching the extensions for a given DSRVPlugin.
 */
@ParametersAreNonnullByDefault
public final class ExtensionsLoader {

    private ExtensionsLoader() {
        throw new UnsupportedOperationException();
    }

    /**
     * Fetches all extensions from the provided extensions folder. This also creates the folder if it does not exist.
     *
     * @param extensionsFolder
     *         The folder to load from.
     * @param <T>
     *         The type of context to load with reference to.
     * @param <V>
     *         The type of plugin to load with reference to.
     *
     * @return extensions The extensions in the given path, non-recursive.
     */
    @SuppressWarnings("unchecked")
    public static <T extends DiscordSRVContext, V extends DSRVPlugin<T>> List<DSRVExtension<T, V>> getExtensions(
        final File extensionsFolder) {
        if (!extensionsFolder.exists()) {
            //noinspection ResultOfMethodCallIgnored
            extensionsFolder.mkdirs();
        }
        if (!extensionsFolder.isDirectory()) {
            throw new IllegalArgumentException("Extensions folder provided must be a folder.");
        }
        List<DSRVExtension<T, V>> extensions = new LinkedList<>();
        ExtensionClassLoader loader = new ExtensionClassLoader(ExtensionsLoader.class.getClassLoader());
        List<ExtensionJarFile> jars = new LinkedList<>();
        Arrays.stream(Objects.requireNonNull(extensionsFolder.listFiles())).map(file -> {
            try {
                return new ExtensionJarFile(file);
            } catch (IOException ignored) {
                return null;
            }
        }).filter(Objects::nonNull).forEach(jars::add);
        jars.stream().map(jarFile -> {
            try {
                String extensionClassName = jarFile.getManifest().getMainAttributes().getValue("Extension-Class");
                loader.addURL(jarFile.getFile().toURI().toURL());
                return (DSRVExtension<T, V>) loader.loadClass(extensionClassName).getDeclaredConstructor()
                    .newInstance();
            } catch (Throwable ignored) {
                return null;
            }
        }).forEach(extensions::add);
        return extensions;
    }

}
