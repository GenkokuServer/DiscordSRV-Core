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
package com.discordsrv.core.test.user;

import com.discordsrv.core.api.user.MinecraftPlayer;
import com.google.common.util.concurrent.FutureCallback;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * A simple test implementation of the {@link MinecraftPlayer} type.
 */
public class TestMinecraftPlayer implements MinecraftPlayer {

    private final CharSequence name;
    private final UUID identifier;

    /**
     * Main constructor for the TestMinecraftPlayer type.
     *
     * @param name
     *         The name of this instance.
     * @param identifier
     *         The unique identifier to be associated with this instance.
     */
    public TestMinecraftPlayer(final CharSequence name, final UUID identifier) {
        this.name = name;
        this.identifier = identifier;
    }

    @Override
    public void sendMessage(final @Nonnull String message, final @Nonnull FutureCallback<Void> resultCallback) {
        resultCallback.onSuccess(null);
    }

    @Override
    public void getName(final @Nonnull Consumer<CharSequence> callback) {
        callback.accept(this.name);
    }

    @Override
    public void getUniqueIdentifier(final @Nonnull Consumer<UUID> callback) {
        callback.accept(this.identifier);
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof TestMinecraftPlayer && ((TestMinecraftPlayer) obj).identifier.equals(identifier);
    }

    @Override
    public String toString() {
        return String.format("%s:%s", name, identifier);
    }
}
