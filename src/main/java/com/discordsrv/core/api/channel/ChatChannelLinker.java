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
package com.discordsrv.core.api.channel;

import com.discordsrv.core.api.common.functional.Translator;
import com.google.common.util.concurrent.FutureCallback;
import net.dv8tion.jda.core.entities.TextChannel;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.ThreadSafe;

/**
 * ChatChannelLinker type, for linking {@link Chat} instances and {@link TextChannel} instances and performing lookups
 * between them.
 */
@ThreadSafe
@ParametersAreNonnullByDefault
public interface ChatChannelLinker {

    /**
     * Translates from a {@link Chat} to a {@link TextChannel}.
     * <p>
     * Convert this to a {@link Translator} with {@code linker::translate}.
     *
     * @param chat
     *         The chat to translate.
     * @param callback
     *         The callback to invoke once a translation is (not) found.
     */
    void translate(Chat chat, FutureCallback<TextChannel> callback);

    /**
     * Performs a lookup for {@link TextChannel} instances given a specific id.
     * <p>
     * Convert this to a {@link Translator} with {@code linker::lookup}.
     *
     * @param id
     *         The ID of the channel.
     * @param callback
     *         The callback to invoke when (not) found.
     */
    void lookup(long id, FutureCallback<TextChannel> callback);

    /**
     * Translates from a {@link TextChannel} to a {@link Chat}.
     * <p>
     * Convert this to a {@link Translator} with {@code linker::translate}.
     *
     * @param channel
     *         The channel to translate.
     * @param callback
     *         The callback to invoke once a translation is (not) found.
     */
    void translate(TextChannel channel, FutureCallback<Chat> callback);

    /**
     * Performs a lookup for {@link Chat} instances given a specific id.
     * <p>
     * Convert this to a {@link Translator} with {@code linker::lookup}.
     *
     * @param id
     *         The ID of the chat.
     * @param callback
     *         The callback to invoke when (not) found.
     */
    void lookup(String id, FutureCallback<Chat> callback);

}
