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
import com.discordsrv.core.api.channel.ChatMessage;
import com.google.common.util.concurrent.FutureCallback;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * A simple test implementation of the {@link Chat} type.
 */
@RequiredArgsConstructor
public class TestChat implements Chat {

    private final CharSequence name;
    private final String identifier;

    @Override
    public void getName(final @Nonnull Consumer<CharSequence> callback) {
        callback.accept(this.name);
    }

    @Override
    public void getUniqueIdentifier(final @Nonnull Consumer<String> callback) {
        callback.accept(this.identifier);
    }

    @Override
    public void sendMessage(final @Nonnull ChatMessage message, final @Nonnull FutureCallback<Void> resultCallback) {
        throw new UnsupportedOperationException();
    }
}
