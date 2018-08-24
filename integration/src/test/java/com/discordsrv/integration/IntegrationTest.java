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
package com.discordsrv.integration;

import com.discordsrv.integration.platform.MinecraftPlugin;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;
import java.util.jar.JarFile;

public class IntegrationTest {

    @Test
    public void integrationTest()
        throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
               InvocationTargetException, InstantiationException {
        Minecraft minecraft = new Minecraft(new LinkedList<>(), new LinkedList<>(), new LinkedList<>());
        File pluginFile = new File("platform/build/libs/platform-TEST-all.jar");
        JarFile jarFile = new JarFile(pluginFile);
        URLClassLoader loader =
            new URLClassLoader(new URL[]{pluginFile.toURI().toURL()}, this.getClass().getClassLoader());
        MinecraftPlugin plugin =
            (MinecraftPlugin) loader.loadClass(jarFile.getManifest().getMainAttributes().getValue("Plugin-Class"))
                .getDeclaredConstructor().newInstance();
        plugin.setMinecraft(minecraft);
        plugin.onEnable();
    }

}
