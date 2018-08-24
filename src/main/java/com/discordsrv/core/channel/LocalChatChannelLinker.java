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
import com.discordsrv.core.api.channel.ChatChannelLinker;
import com.discordsrv.core.api.channel.ChatChannelLookup;
import com.discordsrv.core.api.minecraft.Console;
import com.discordsrv.core.conf.annotation.Configured;
import com.discordsrv.core.conf.annotation.Val;
import com.google.common.util.concurrent.FutureCallback;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.collections4.BidiMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Leverages a local storage for chat/channel linking.
 */
public class LocalChatChannelLinker implements ChatChannelLinker {

    private final BidiMap<String, String> channelStorage;
    private final ChatChannelLookup lookup;
    private final Console console;
    private final String consoleChannelId;

    /**
     * Main constructor for the LocalChatChannelLinker type.
     *
     * @param channelStorage
     *         The map to store the links in. This should update files when updated.
     * @param lookup
     *         The lookup service.
     */
    @Configured
    public LocalChatChannelLinker(final @Val("channels") BidiMap<String, String> channelStorage,
                                  final @Val("lookup") ChatChannelLookup lookup, final @Val("console") Console console,
                                  final @Val("console_channel") String id) {
        this.channelStorage = channelStorage;
        this.lookup = lookup;
        this.console = console;
        consoleChannelId = id;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void translate(final @Nonnull Chat chat, final @Nonnull FutureCallback<TextChannel> callback) {
        chat.getUniqueIdentifier(ident -> {
            @Nullable String result = channelStorage.get(ident);
            if (result == null) {
                callback.onSuccess(null);
            } else {
                lookup.lookupChannel(result, callback);
            }
        });
    }

    @Override
    public void translate(final @Nonnull TextChannel channel, final @Nonnull FutureCallback<Chat> callback) {
        @Nullable String result = channelStorage.getKey(channel.getId());
        if (result == null) {
            callback.onSuccess(null);
        } else {
            lookup.lookupChat(result, callback);
        }
    }

    @Override
    public void getConsoleChannel(final @Nonnull FutureCallback<TextChannel> callback) {
        lookup.lookupChannel(consoleChannelId, callback);
    }

    @Override
    public void getConsole(final @Nonnull FutureCallback<Console> callback) {
        callback.onSuccess(console);
    }

    @Override
    public String getConsoleChannelId() {
        return consoleChannelId;
    }
}
