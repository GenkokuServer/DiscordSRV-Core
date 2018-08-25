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
package com.discordsrv.core.test.mocker;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.discordsrv.core.test.Values.TIMEOUT;
import static org.junit.Assert.*;

/**
 * Tests for ensuring that the Mocker is successfully able to mock different types.
 */
public class MockerTest {

    private final String id = "1234";
    private final Mocker mocker = new Mocker();

    /**
     * Ensure that we can successfully mock JDA types.
     */
    @Test
    public void getMockedJDA() {
        //noinspection ConstantConditions
        JDA jda = mocker.getMockedJDA(mocker::getSimpleMockedTextChannel, mocker::getMockedUser, null);
        TextChannel channel = jda.getTextChannelById(id);
        assertNotNull(channel);
        assertEquals(id, channel.getId());
        User user = jda.getUserById(id);
        assertNotNull(user);
        assertEquals(id, user.getId());
    }

    /**
     * Ensure that we can successfully mock User types.
     */
    @Test
    public void getMockedUser() {
        User user = mocker.getMockedUser(id);
        assertEquals(id, user.getId());
    }

    /**
     * Ensure that we can successfully mock TextChannel types.
     */
    @Test
    public void getMockedTextChannel() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        TextChannel textChannel = mocker.getMockedTextChannel(id, mocker.getMockedGuild(), (arg -> {
            latch.countDown();
            return null;
        }));
        assertEquals(id, textChannel.getId());
        //noinspection ConstantConditions,ResultOfMethodCallIgnored
        textChannel.sendMessage((CharSequence) null);
        if (!latch.await(TIMEOUT, TimeUnit.SECONDS)) {
            fail();
        }
    }

    /**
     * Ensure that we can successfully mock GuildMessageReceived types.
     */
    @Test
    public void getMockedGuildMessageReceivedEvent() {
        new GuildMessageReceivedEvent(mocker
            .getMockedJDA(mocker::getSimpleMockedTextChannel, mocker::getMockedUser, mocker.getMockedUser("self")), 0,
            mocker.getMockedMessage(id, 0, mocker.getMockedTextChannel(id, mocker.getMockedGuild(), (arg -> null)),
                mocker.getMockedUser(id)));
    }

}
