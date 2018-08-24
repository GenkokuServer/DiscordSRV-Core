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
package com.discordsrv.integration.platform.chat;

import com.discordsrv.core.api.channel.Chat;
import com.discordsrv.core.api.channel.ChatChannelLookup;
import com.discordsrv.core.api.common.callback.MultiCallbackWrapper;
import com.discordsrv.core.api.common.functional.Translator;
import com.discordsrv.integration.platform.IntegrationDSRVContext;
import com.google.common.util.concurrent.FutureCallback;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import net.dv8tion.jda.core.entities.TextChannel;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
@Value
@RequiredArgsConstructor
public class MalleableChatChannelLookup implements ChatChannelLookup {

    IntegrationDSRVContext context;
    List<Translator<String, Chat>> chatTranslators = new LinkedList<>();

    @Override
    public void lookupChannel(final String id, final FutureCallback<TextChannel> callback) {
        try {
            callback.onSuccess(context.getJda().getTextChannelById(id));
        } catch (Throwable t) {
            callback.onFailure(t);
        }
    }

    @Override
    public void lookupChat(final String id, final FutureCallback<Chat> callback) {
        new MultiCallbackWrapper<>(chatTranslators.stream()
            .map(translator -> (Consumer<FutureCallback<Chat>>) internal -> translator.translate(id, internal))
            .collect(Collectors.toList()), callback).run();
    }

}
