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
import org.yaml.snakeyaml.Yaml;

import javax.naming.ConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.stream.Stream;

import static com.discordsrv.core.conf.ConfigUtil.*;
import static org.junit.Assert.assertEquals;

/**
 * Tests {@link ConfigUtil}.
 */
public class ConfigUtilTest {

    /**
     * Tests {@link Configuration#constructFromConfig(Class, Object...)}, {@link ConfigUtil#flatten(Iterable)}.
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
    public void constructFromConfigTest()
        throws InvocationTargetException, InstantiationException, ConfigurationException, IllegalAccessException,
               IOException {
        String name = "Test";
        String value = "overwritten";
        Configuration configuration =
            new Configuration(Objects.requireNonNull(this.getClass().getClassLoader().getResource("conf.yaml")));
        ConfiguredType type = configuration.constructFromConfig(ConfiguredType.class);
        assertEquals(name, type.getName());
        assertEquals(value, type.getValue());
    }

    /**
     * Tests {@link Configuration#constructFromConfig(Class, Object...)}, {@link ConfigUtil#flatten(Iterable)}.
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
    public void constructFromConfigExtrasTest()
        throws InvocationTargetException, InstantiationException, ConfigurationException, IllegalAccessException,
               IOException {
        String name = "Test";
        String value = "actual";
        Configuration configuration = new Configuration();
        configuration.addConfig(unreduceConfig(
            createConfig(configuration.getYaml(), this.getClass().getClassLoader().getResourceAsStream("conf2.yaml")),
            ConfiguredType.class));
        ConfiguredType type = configuration.constructFromConfig(ConfiguredType.class, name);
        assertEquals(name, type.getName());
        assertEquals(value, type.getValue());
    }

    /**
     * Tests {@link ConfigUtil#createConfig(Yaml, InputStream...)}, {@link Configuration#constructFromConfig(Class,
     * Object...)}, {@link ConfigUtil#mergeConfigs(Stream)}.
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
    public void createConfigTest()
        throws IOException, InvocationTargetException, InstantiationException, ConfigurationException,
               IllegalAccessException {
        String name = "Test";
        String value = "actual";
        Configuration configuration = new Configuration();
        configuration.addConfig(mergeConfigs(Stream.of(createConfig(configuration.getYaml(),
            this.getClass().getClassLoader().getResourceAsStream("conf.yaml")), unreduceConfig(
            createConfig(configuration.getYaml(), this.getClass().getClassLoader().getResourceAsStream("conf2.yaml")),
            ConfiguredType.class))));
        ConfiguredType type = configuration.constructFromConfig(ConfiguredType.class);
        assertEquals(name, type.getName());
        assertEquals(value, type.getValue());
    }

}
