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
package com.discordsrv.core.api.channel;

import com.discordsrv.core.api.common.functional.Translator;
import com.google.common.util.concurrent.FutureCallback;
import net.dv8tion.jda.core.entities.TextChannel;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.ThreadSafe;

/**
 * ChatChannelLinker type, for looking up {@link Chat} instances and {@link TextChannel} instances.
 */
@ThreadSafe
@ParametersAreNonnullByDefault
public interface ChatChannelLookup {

    /**
     * Performs a lookup for {@link TextChannel} instances given a specific id.
     * <p>
     * Convert this to a {@link Translator} with {@code lookup::lookup}.
     *
     * @param id
     *         The ID of the channel.
     * @param callback
     *         The callback to invoke when (not) found.
     */
    void lookupChannel(String id, FutureCallback<TextChannel> callback);

    /**
     * Performs a lookup for {@link Chat} instances given a specific id.
     * <p>
     * Convert this to a {@link Translator} with {@code lookup::lookup}.
     *
     * @param id
     *         The ID of the chat.
     * @param callback
     *         The callback to invoke when (not) found.
     */
    void lookupChat(String id, FutureCallback<Chat> callback);

}
