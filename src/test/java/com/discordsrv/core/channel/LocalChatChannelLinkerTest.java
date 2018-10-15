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
package com.discordsrv.core.channel;

import com.discordsrv.core.api.channel.Chat;
import com.discordsrv.core.test.channel.TestChat;
import com.discordsrv.core.test.channel.TestChatChannelLookup;
import com.discordsrv.core.test.minecraft.TestConsole;
import com.discordsrv.core.test.mocker.Mocker;
import com.google.common.util.concurrent.FutureCallback;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.collections4.bidimap.DualTreeBidiMap;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static org.junit.Assert.*;

/**
 * Tests {@link LocalChatChannelLinker}.
 */
@SuppressWarnings("CanBeFinal")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ParametersAreNonnullByDefault
public class LocalChatChannelLinkerTest {

    private static LocalChatChannelLinker linker;
    private static String testMCId = "1234";
    private static String testDiscordId = "1234";
    private static Mocker mocker;

    /**
     * Creates the linker.
     */
    @BeforeClass
    public static void setup() {
        mocker = new Mocker();
        DualTreeBidiMap<String, String> bidiMap = new DualTreeBidiMap<>();
        bidiMap.put(testMCId, testDiscordId);
        linker = new LocalChatChannelLinker(bidiMap, new TestChatChannelLookup(), new TestConsole(), "1234");
    }

    /**
     * Destroys the linker.
     */
    @AfterClass
    public static void tearDown() {
        mocker = null;
        linker = null;
    }

    /**
     * Tests {@link LocalChatChannelLinker#translate(Chat, FutureCallback)}.
     */
    @Test
    public void stage1Translate() {
        linker.translate(new TestChat("Test", testMCId), new FutureCallback<TextChannel>() {
            @Override
            public void onSuccess(@Nullable final TextChannel result) {
                assertNotNull(result);
                assertEquals(testDiscordId, result.getId());
            }

            @Override
            public void onFailure(final Throwable t) {
                fail();
            }
        });
    }

    /**
     * Tests {@link LocalChatChannelLinker#translate(TextChannel, FutureCallback)}.
     */
    @Test
    public void stage2Translate() {
        linker.translate(mocker.getSimpleMockedTextChannel(testDiscordId), new FutureCallback<Chat>() {
            @Override
            public void onSuccess(@Nullable final Chat result) {
                assertNotNull(result);
                result.getUniqueIdentifier(ident -> assertEquals(testMCId, ident));
            }

            @Override
            public void onFailure(final Throwable t) {
                fail();
            }
        });
    }

}
