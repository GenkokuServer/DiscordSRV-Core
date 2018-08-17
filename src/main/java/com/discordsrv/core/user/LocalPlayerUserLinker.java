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
import com.discordsrv.core.api.auth.State;
import com.discordsrv.core.api.user.MinecraftPlayer;
import com.discordsrv.core.api.user.PlayerUserLinker;
import com.discordsrv.core.api.user.PlayerUserLookup;
import com.discordsrv.core.conf.annotation.Configured;
import com.discordsrv.core.conf.annotation.Val;
import com.google.common.util.concurrent.FutureCallback;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.collections4.BidiMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Leverages a local storage for player/user linking.
 */
public class LocalPlayerUserLinker implements PlayerUserLinker, AuthenticationStore<MinecraftPlayer, User> {

    private final BidiMap<UUID, String> playerStorage;
    private final PlayerUserLookup lookup;

    /**
     * Main constructor for the LocalPlayerUserLinker type.
     *
     * @param playerStorage
     *         The map to store the links in. This should update files when updated.
     * @param lookup
     *         The lookup service.
     */
    @Configured
    public LocalPlayerUserLinker(final @Val("users") BidiMap<UUID, String> playerStorage,
                                 final @Val("lookup") PlayerUserLookup lookup) {
        this.playerStorage = playerStorage;
        this.lookup = lookup;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void translate(final @Nonnull MinecraftPlayer player, final @Nonnull FutureCallback<User> callback) {
        player.getUniqueIdentifier(ident -> {
            @Nullable String result = playerStorage.get(ident);
            if (result == null) {
                callback.onSuccess(null);
            } else {
                lookup.lookupUser(result, callback);
            }
        });
    }

    @Override
    public void translate(final @Nonnull User user, final @Nonnull FutureCallback<MinecraftPlayer> callback) {
        @Nullable UUID result = playerStorage.getKey(user.getId());
        if (result == null) {
            callback.onSuccess(null);
        } else {
            lookup.lookupPlayer(result, callback);
        }
    }

    @Override
    public void push(final @Nonnull MinecraftPlayer first, final @Nonnull User last,
                     final @Nonnull FutureCallback<Boolean> callback) {
        first.getUniqueIdentifier(ident -> {
            try {
                boolean success = playerStorage.putIfAbsent(ident, last.getId()) == null;
                if (success) {
                    first.setAuthenticationState(State.AUTHENTICATED);
                }
                callback.onSuccess(success);
            } catch (Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    @Override
    public void remove(final @Nullable MinecraftPlayer first, final @Nullable User last,
                       final @Nonnull FutureCallback<Boolean> callback) {
        if (first != null) {
            first.getUniqueIdentifier(key -> {
                boolean success = playerStorage.remove(key) != null;
                first.setAuthenticationState(State.UNAUTHENTICATED);
                callback.onSuccess(success);
            });
        } else if (last != null) {
            UUID player = playerStorage.removeValue(last.getId());
            if (player != null) {
                lookup.lookupPlayer(player, new FutureCallback<MinecraftPlayer>() {
                    @Override
                    public void onSuccess(@Nullable final MinecraftPlayer result) {
                        if (result != null) {
                            result.setAuthenticationState(State.UNAUTHENTICATED);
                        }
                        callback.onSuccess(true);
                    }

                    @Override
                    public void onFailure(final @Nonnull Throwable t) {
                        callback.onFailure(t);
                    }
                });
            } else {
                callback.onSuccess(false);
            }
        } else {
            callback.onFailure(new NullPointerException());
        }
    }
}
