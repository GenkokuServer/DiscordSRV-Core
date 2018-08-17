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
package com.discordsrv.core.conf.collect;

import com.discordsrv.core.conf.Configurator;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link ParentAwareHashMap}.
 */
public class ParentAwareHashMapTest {

    /**
     * Tests {@link ParentAwareHashMap#clone()}.
     *
     * @throws IOException
     *         If the compared yaml cannot load.
     */
    @Test
    public void cloneTest() throws IOException {
        final ParentAwareHashMap config = Configurator.createConfig(new Yaml(),
            this.getClass().getClassLoader().getResourceAsStream("locales/en/us/default.yaml"));
        assertEquals(config, config.clone());
    }
}
