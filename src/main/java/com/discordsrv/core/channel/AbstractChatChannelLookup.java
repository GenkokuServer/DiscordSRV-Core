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

import com.discordsrv.core.api.channel.ChatChannelLookup;
import com.discordsrv.core.api.dsrv.Context;
import com.google.common.util.concurrent.FutureCallback;
import net.dv8tion.jda.core.entities.TextChannel;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * AbstractChatChannelLookup type, for simplifying implementations for platform development. You probably want to use
 * {@link MalleableChatChannelLookup}.
 *
 * @param <T>
 *         The specific type of context that this lookup is performing in.
 */
@ParametersAreNonnullByDefault
public abstract class AbstractChatChannelLookup<T extends Context> implements ChatChannelLookup {

    private final T context;

    /**
     * Main constructor for the AbstractChatChannelLookup type.
     *
     * @param context
     *         The context in which this lookup is performing.
     */
    protected AbstractChatChannelLookup(final T context) {
        this.context = context;
    }

    @Override
    public void lookupChannel(final String id, final FutureCallback<TextChannel> callback) {
        try {
            callback.onSuccess(context.getJda().getTextChannelById(id));
        } catch (Throwable t) {
            callback.onFailure(t);
        }
    }

    protected T getContext() {
        return context;
    }
}
