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
package com.discordsrv.core.user;

import com.discordsrv.core.api.auth.AuthenticationStore;
import com.discordsrv.core.api.user.MinecraftPlayer;
import com.discordsrv.core.api.user.PlayerUserLinker;
import com.discordsrv.core.api.user.PlayerUserLookup;
import com.google.common.util.concurrent.FutureCallback;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.collections4.BidiMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Leverages a local storage for player/user linking.
 */
@RequiredArgsConstructor
public class LocalPlayerUserLinker implements PlayerUserLinker, AuthenticationStore<MinecraftPlayer, User> {

    private final BidiMap<String, Long> playerStorage;
    private final PlayerUserLookup lookup;

    @SuppressWarnings("Duplicates")
    @Override
    public void translate(final @Nonnull MinecraftPlayer player, final @Nonnull FutureCallback<User> callback) {
        player.getUniqueIdentifier(ident -> {
            @Nullable Long result = playerStorage.get(ident);
            if (result == null) {
                callback.onSuccess(null);
            } else {
                lookup.lookup(result, callback);
            }
        });
    }

    @Override
    public void translate(final @Nonnull User user, final @Nonnull FutureCallback<MinecraftPlayer> callback) {
        @Nullable String result = playerStorage.getKey(user.getIdLong());
        if (result == null) {
            callback.onSuccess(null);
        } else {
            lookup.lookup(result, callback);
        }
    }

    @Override
    public void push(final @Nonnull MinecraftPlayer first, final @Nonnull User last) {
        first.getUniqueIdentifier(ident -> playerStorage.put(ident, last.getIdLong()));
    }

    @Override
    public void remove(final @Nonnull MinecraftPlayer first, final @Nonnull User last) {
        first.getUniqueIdentifier(ident -> {
            playerStorage.remove(ident);
            playerStorage.removeValue(last.getIdLong());
        });
    }
}
