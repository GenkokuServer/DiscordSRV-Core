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
package com.discordsrv.core.discord.listeners;

import com.discordsrv.core.api.channel.ChatMessage;
import com.discordsrv.core.channel.LocalChatChannelLinker;
import com.discordsrv.core.conf.Configuration;
import com.discordsrv.core.role.LocalTeamRoleLinker;
import com.discordsrv.core.test.Values;
import com.discordsrv.core.test.channel.SettableTestChatChannelLookup;
import com.discordsrv.core.test.channel.TestChat;
import com.discordsrv.core.test.channel.TestChatChannelLookup;
import com.discordsrv.core.test.minecraft.TestConsole;
import com.discordsrv.core.test.mocker.Mocker;
import com.discordsrv.core.test.role.TestTeamRoleLookup;
import com.discordsrv.core.test.user.TestPlayerUserLookup;
import com.discordsrv.core.user.LocalPlayerUserLinker;
import com.google.common.util.concurrent.FutureCallback;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.collections4.bidimap.DualTreeBidiMap;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Nonnull;
import javax.naming.ConfigurationException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;

import static org.junit.Assert.fail;

/**
 * Tests for the {@link SynchronizerListener} type.
 */
public class SynchronizerListenerTest {

    private static Configuration configuration;
    private static Mocker mocker;
    private static TestConsole console;

    /**
     * Sets up before all tests are performed.
     *
     * @throws IOException
     *         If the conversion process from yaml to map fails.
     */
    @BeforeClass
    public static void setup() throws IOException {
        Yaml yaml = new Yaml();
        configuration = Configuration
            .getStandardConfiguration(yaml, SynchronizerListenerTest.class.getClassLoader().getResource("conf.yaml"));
        mocker = new Mocker();
        console = new TestConsole();
    }

    /**
     * Tears down after all tests have completed.
     */
    @AfterClass
    public static void tearDown() {
        configuration = null;
        mocker = null;
        console = null;
    }

    /**
     * Tests {@link SynchronizerListener#onGuildMessageReceived(GuildMessageReceivedEvent)} (console channel).
     *
     * @throws InvocationTargetException
     *         As inherited.
     * @throws InstantiationException
     *         As inherited.
     * @throws ConfigurationException
     *         As inherited.
     * @throws IllegalAccessException
     *         As inherited.
     * @throws InterruptedException
     *         If the expected received behaviour does not occur.
     */
    @Test
    public void onGuildMessageReceivedConsole()
        throws InstantiationException, IllegalAccessException, ConfigurationException, InvocationTargetException,
               InterruptedException {
        DualTreeBidiMap<UUID, String> playerStorage = new DualTreeBidiMap<>();
        DualTreeBidiMap<String, String> chatStorage = new DualTreeBidiMap<>(), teamStorage = new DualTreeBidiMap<>();
        SynchronizerListener listener = configuration
            .create(SynchronizerListener.class, new LocalPlayerUserLinker(playerStorage, new TestPlayerUserLookup()),
                new LocalChatChannelLinker(chatStorage, new TestChatChannelLookup(), console, "console-channel"),
                new LocalTeamRoleLinker(teamStorage, new TestTeamRoleLookup()),
                Collections.<UnaryOperator<String>>emptyList(), "%name%:%message%");
        playerStorage.put(UUID.nameUUIDFromBytes("1234".getBytes()), "1234");
        teamStorage.put("1234", "1234");
        CountDownLatch latch = new CountDownLatch(1);
        console.setOnInvocation(s -> latch.countDown());
        listener.onGuildMessageReceived(new GuildMessageReceivedEvent(mocker
            .getMockedJDA(mocker::getSimpleMockedTextChannel, mocker::getMockedUser, mocker.getMockedUser("self")), 0,
            mocker.getMockedMessage("", 0,
                mocker.getMockedTextChannel("console-channel", mocker.getMockedGuild(), (arg -> null)),
                mocker.getMockedUser("1234"))));
        if (!latch.await(Values.TIMEOUT, TimeUnit.SECONDS)) {
            fail();
        }
    }

    /**
     * Tests {@link SynchronizerListener#onGuildMessageReceived(GuildMessageReceivedEvent)} (not a console channel).
     *
     * @throws InvocationTargetException
     *         As inherited.
     * @throws InstantiationException
     *         As inherited.
     * @throws ConfigurationException
     *         As inherited.
     * @throws IllegalAccessException
     *         As inherited.
     * @throws InterruptedException
     *         If the expected received behaviour does not occur.
     */
    @Test
    public void onGuildMessageReceived()
        throws InstantiationException, IllegalAccessException, ConfigurationException, InvocationTargetException,
               InterruptedException {
        DualTreeBidiMap<UUID, String> playerStorage = new DualTreeBidiMap<>();
        DualTreeBidiMap<String, String> chatStorage = new DualTreeBidiMap<>(), teamStorage = new DualTreeBidiMap<>();
        SettableTestChatChannelLookup lookup = new SettableTestChatChannelLookup();
        SynchronizerListener listener = configuration
            .create(SynchronizerListener.class, new LocalPlayerUserLinker(playerStorage, new TestPlayerUserLookup()),
                new LocalChatChannelLinker(chatStorage, lookup, console, "console-channel"),
                new LocalTeamRoleLinker(teamStorage, new TestTeamRoleLookup()),
                Collections.<UnaryOperator<String>>emptyList(), "%name%:%message%");
        playerStorage.put(UUID.nameUUIDFromBytes("1234".getBytes()), "1234");
        chatStorage.put("1234", "1234");
        teamStorage.put("1234", "1234");
        console.setOnInvocation(s -> {
            throw new UnsupportedOperationException();
        });
        CountDownLatch latch = new CountDownLatch(1);
        lookup.put("1234", new TestChat("Test", "1234") {
            @Override
            public void sendMessage(@Nonnull final ChatMessage<Long> message,
                                    @Nonnull final FutureCallback<Void> resultCallback) {
                if (message.getMessage().equals("Test:1234")) {
                    latch.countDown();
                }
            }
        });
        listener.onGuildMessageReceived(new GuildMessageReceivedEvent(mocker
            .getMockedJDA(mocker::getSimpleMockedTextChannel, mocker::getMockedUser, mocker.getMockedUser("self")), 0,
            mocker.getMockedMessage("1234", 0,
                mocker.getMockedTextChannel("1234", mocker.getMockedGuild(), (arg -> null)),
                mocker.getMockedUser("1234"))));
        if (!latch.await(Values.TIMEOUT, TimeUnit.SECONDS)) {
            fail();
        }
    }

    /* Untested, as these require an interactive environment. Will be instead tested with DiscordSRV-Core-Tester.
    @Test
    public void onGuildMemberRoleAdd() {
    }

    @Test
    public void onGuildMemberRoleRemove() {
    }

    @Test
    public void onRoleDelete() {
    }
    */

}
