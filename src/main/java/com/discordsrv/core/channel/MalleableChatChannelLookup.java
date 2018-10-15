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
import com.discordsrv.core.api.common.callback.MultiCallbackWrapper;
import com.discordsrv.core.api.common.functional.Translator;
import com.discordsrv.core.api.dsrv.Context;
import com.google.common.util.concurrent.FutureCallback;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * MalleableChatChannelLookup type, for making dynamic lookups.
 *
 * @param <T>
 *         The specific type of context that this lookup is performing in.
 */
@ParametersAreNonnullByDefault
public abstract class MalleableChatChannelLookup<T extends Context> extends AbstractChatChannelLookup<T> {

    private final Set<Translator<String, Chat>> chatTranslators = new CopyOnWriteArraySet<>();

    /**
     * Main constructor for the MalleableChatChannelLookup type.
     *
     * @param context
     *         The context in which this lookup is performing.
     */
    public MalleableChatChannelLookup(final T context) {
        super(context);
    }

    @Override
    public void lookupChat(final String id, final FutureCallback<Chat> callback) {
        new MultiCallbackWrapper<>(chatTranslators.stream()
            .map(translator -> (Consumer<FutureCallback<Chat>>) internal -> translator.translate(id, internal))
            .collect(Collectors.toList()), callback).run();
    }

    /**
     * Adds a translator to this lookup.
     *
     * @param translator
     *         The translator to invoke for getting a chat.
     */
    public void addChatTranslator(Translator<String, Chat> translator) {
        this.chatTranslators.add(translator);
    }

}
