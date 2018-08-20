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
import com.google.common.util.concurrent.FutureCallback;

import javax.annotation.Nonnull;
import java.util.HashMap;

/**
 * A simple Test implementation of the ChatChannelLookup interface.
 */
public class SettableTestChatChannelLookup extends TestChatChannelLookup {

    private final HashMap<String, Chat> chatHashMap = new HashMap<>();

    @Override
    public void lookupChat(final @Nonnull String id, final @Nonnull FutureCallback<Chat> callback) {
        callback.onSuccess(chatHashMap.get(id));
    }

    /**
     * Puts a chat in this lookup with a specific id.
     *
     * @param id
     *         The id to put.
     * @param chat
     *         The chat to put.
     */
    public void put(String id, Chat chat) {
        this.chatHashMap.put(id, chat);
    }
}
