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

import org.junit.Test;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import javax.naming.ConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link Configurator}.
 */
public class ConfiguratorTest {

    /**
     * Tests {@link Configurator#constructFromConfig(Map, Class)}.
     *
     * @throws InvocationTargetException
     *         As inherited.
     * @throws InstantiationException
     *         As inherited.
     * @throws ConfigurationException
     *         As inherited.
     * @throws IllegalAccessException
     *         As inherited.
     * @throws IOException
     *         If the conversion process from yaml to map fails.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void constructFromConfig()
        throws InvocationTargetException, InstantiationException, ConfigurationException, IllegalAccessException,
               IOException {
        String name = "Test";
        String value = "overwritten";
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml();
        try (
            InputStreamReader reader = new InputStreamReader(
                this.getClass().getClassLoader().getResourceAsStream("conf.yaml"))
        ) {
            LinkedHashMap<String, Map<String, Object>> tree = yaml.load(reader);
            ConfiguredType type = Configurator.constructFromConfig(Configurator.flatten(tree), ConfiguredType.class);
            assertEquals(name, type.getName());
            assertEquals(value, type.getValue());
        }
    }

    /**
     * Tests {@link Configurator#createConfig(Yaml, InputStream...)}.
     *
     * @throws InvocationTargetException
     *         As inherited.
     * @throws InstantiationException
     *         As inherited.
     * @throws ConfigurationException
     *         As inherited.
     * @throws IllegalAccessException
     *         As inherited.
     * @throws IOException
     *         If the conversion process from yaml to map fails.
     */
    @Test
    public void createConfig()
        throws IOException, InvocationTargetException, InstantiationException, ConfigurationException,
               IllegalAccessException {
        String name = "Test";
        String value = "actual";
        Map<String, Object> source = Configurator
            .createConfig(new Yaml(), this.getClass().getClassLoader().getResourceAsStream("conf.yaml"),
                this.getClass().getClassLoader().getResourceAsStream("conf2.yaml"));
        ConfiguredType type = Configurator.constructFromConfig(source, ConfiguredType.class);
        assertEquals(name, type.getName());
        assertEquals(value, type.getValue());
    }

}
