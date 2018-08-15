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
package com.discordsrv.core.test.channel;

import com.discordsrv.core.api.channel.Chat;
import com.discordsrv.core.api.channel.ChatChannelLookup;
import com.discordsrv.core.test.mocker.Mocker;
import com.google.common.util.concurrent.FutureCallback;
import net.dv8tion.jda.core.entities.TextChannel;

import javax.annotation.Nonnull;

/**
 * A simple Test implementation of the ChatChannelLookup interface.
 */
public class TestChatChannelLookup implements ChatChannelLookup {

    private final Mocker mocker = new Mocker();

    @Override
    public void lookup(final long id, final @Nonnull FutureCallback<TextChannel> callback) {
        callback.onSuccess(mocker.getSimpleMockedTextChannel(id));
    }

    @Override
    public void lookup(final @Nonnull String id, final @Nonnull FutureCallback<Chat> callback) {
        callback.onSuccess(new TestChat("Test", id));
    }
}
