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
import com.discordsrv.core.api.channel.ChatMessage;
import com.google.common.util.concurrent.FutureCallback;
import lombok.Value;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@Value
public class ChatWrapper implements Chat {

    com.discordsrv.integration.chat.Chat chat;

    @Override
    public void sendMessage(final ChatMessage<Long> message, final FutureCallback<Void> resultCallback) {
        chat.sendMessage(message.getMessage());
        resultCallback.onSuccess(null);
    }

    @Override
    public void getName(final Consumer<CharSequence> callback) {
        callback.accept(chat.getName());
    }

    @Override
    public void getUniqueIdentifier(final Consumer<String> callback) {
        callback.accept(chat.getName());
    }
}
