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

import com.discordsrv.core.conf.collect.ParentAwareHashMap;
import org.junit.Test;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import javax.naming.ConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.stream.Stream;

import static com.discordsrv.core.conf.Configurator.*;
import static org.junit.Assert.assertEquals;

/**
 * Tests {@link Configurator}.
 */
public class ConfiguratorTest {

    /**
     * Tests {@link Configurator#constructFromConfig(ParentAwareHashMap, Class)}, {@link
     * Configurator#flatten(Iterable)}.
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
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml();
        ConfiguredType type =
            constructFromConfig(createConfig(yaml, this.getClass().getClassLoader().getResourceAsStream("conf.yaml")),
                ConfiguredType.class);
        assertEquals(name, type.getName());
        assertEquals(value, type.getValue());
    }

    /**
     * Tests {@link Configurator#createConfig(Yaml, InputStream...)}, {@link Configurator#constructFromConfig(ParentAwareHashMap,
     * Class)}, {@link Configurator#mergeConfigs(Stream)}, {@link Configurator#unreduceConfig(ParentAwareHashMap,
     * Class)}.
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
        Yaml yaml = new Yaml();
        ParentAwareHashMap source = mergeConfigs(Stream
            .of(createConfig(yaml, this.getClass().getClassLoader().getResourceAsStream("conf.yaml")),
                unreduceConfig(createConfig(yaml, this.getClass().getClassLoader().getResourceAsStream("conf2.yaml")),
                    ConfiguredType.class)));
        ConfiguredType type = constructFromConfig(source, ConfiguredType.class);
        assertEquals(name, type.getName());
        assertEquals(value, type.getValue());
    }

}
