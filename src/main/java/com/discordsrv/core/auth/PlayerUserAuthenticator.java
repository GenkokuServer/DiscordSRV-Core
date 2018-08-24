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
package com.discordsrv.core.auth;

import com.discordsrv.core.api.auth.AuthenticationStore;
import com.discordsrv.core.api.auth.Token;
import com.discordsrv.core.api.user.MinecraftPlayer;
import com.discordsrv.core.conf.annotation.Configured;
import com.discordsrv.core.conf.annotation.Val;
import com.google.common.util.concurrent.FutureCallback;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.naming.AuthenticationException;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * PlayerUserAuthenticator type, for performing OAuth handshakes between users and players.
 */
@SuppressWarnings("WeakerAccess")
public class PlayerUserAuthenticator {

    private final int tokenRadix = 36;
    private final SecureRandom random;
    private final AuthenticationStore<MinecraftPlayer, User> userStore;
    private final ConcurrentMap<UserAuthToken, MinecraftPlayer> tokenMap;
    private final ScheduledExecutorService scheduler;

    /**
     * Main constructor for the PlayerUserAuthenticator type.
     *
     * @param userStore
     *         The authentication store to maintain.
     * @param scheduledExecutorService
     *         The executor service to schedule on.
     */
    @Configured
    public PlayerUserAuthenticator(final @Nonnull @Val("store") AuthenticationStore<MinecraftPlayer, User> userStore,
                                   final @Nonnull @Val("executor") ScheduledExecutorService scheduledExecutorService) {
        this.random = new SecureRandom();
        this.userStore = userStore;
        this.tokenMap = new ConcurrentHashMap<>();
        this.scheduler = scheduledExecutorService;
    }

    /**
     * Start a handshake.
     *
     * @param player
     *         The user starting the handshake.
     * @param callback
     *         The callback for this method.
     */
    public void beginAuth(final @Nonnull MinecraftPlayer player, final @Nonnull FutureCallback<Token> callback) {
        userStore.contains(player, null, new FutureCallback<Boolean>() {
            @Override
            public void onSuccess(@Nullable final Boolean result) {
                if (result == null) {
                    onFailure(new NullPointerException());
                    return;
                }
                try {
                    if (result || tokenMap.entrySet().stream().anyMatch(entry -> entry.getValue().equals(player))) {
                        throw new IllegalStateException("User was not unauthenticated.");
                    } else {
                        UserAuthToken token = new UserAuthToken();
                        tokenMap.put(token, player);
                        scheduler.schedule(token::invalidate, 1, TimeUnit.MINUTES);
                        callback.onSuccess(token);
                    }
                } catch (Throwable t) {
                    callback.onFailure(t);
                }
            }

            @Override
            public void onFailure(final @Nonnull Throwable t) {
            }
        });
    }

    /**
     * Attempt to complete the OAuth handshake.
     *
     * @param user
     *         The user attempting to complete the handshake.
     * @param tokenString
     *         The verification string attempted.
     * @param callback
     *         The callback for this method.
     */
    public void attemptVerify(final @Nonnull User user, final @Nonnull String tokenString,
                              final @Nonnull FutureCallback<Pair<MinecraftPlayer, User>> callback) {
        final AtomicBoolean matched = new AtomicBoolean(false);
        this.tokenMap.forEach((key, value) -> key.getUniqueIdentifier(tokenIdent -> {
            if (tokenIdent.equals(tokenString)) {
                matched.set(true);
                userStore.push(value, user, new PushCallback(key, callback, value, user));
            }
        }));
        if (!matched.get()) {
            callback.onFailure(new AuthenticationException());
        }
    }

    /**
     * Removes the authentication entry from the store.
     *
     * @param player
     *         The player to remove.
     * @param callback
     *         The callback for this method.
     */
    public void unauthenticate(final @Nonnull MinecraftPlayer player, final @Nonnull FutureCallback<Boolean> callback) {
        userStore.remove(player, null, callback);
    }

    /**
     * Removes the authentication entry from the store.
     *
     * @param user
     *         The user to remove.
     * @param callback
     *         The callback for this method.
     */
    public void unauthenticate(final @Nonnull User user, final @Nonnull FutureCallback<Boolean> callback) {
        userStore.remove(null, user, callback);
    }

    private final class PushCallback implements FutureCallback<Boolean> {

        private final UserAuthToken key;
        private final FutureCallback<Pair<MinecraftPlayer, User>> callback;
        private final MinecraftPlayer player;
        private final User user;

        private PushCallback(final UserAuthToken key, final FutureCallback<Pair<MinecraftPlayer, User>> callback,
                             final MinecraftPlayer player, final User user) {
            this.key = key;
            this.callback = callback;
            this.player = player;
            this.user = user;
        }

        @Override
        public void onSuccess(@Nullable final Boolean result) {
            if (Objects.requireNonNull(result)) {
                scheduler.execute(key::invalidate);
                callback.onSuccess(new Pair<MinecraftPlayer, User>() {
                    @Override
                    public MinecraftPlayer getLeft() {
                        return player;
                    }

                    @Override
                    public User getRight() {
                        return user;
                    }
                });
            } else {
                callback.onFailure(new IllegalStateException("User found to be already authenticated."));
            }
        }

        @Override
        public void onFailure(final @Nonnull Throwable t) {
            callback.onFailure(t);
        }
    }

    private final class UserAuthToken implements Token {

        private final String ident;
        private boolean valid;

        private UserAuthToken() {
            this.ident = String.format("%1$5s",
                Integer.toString(random.nextInt(Integer.parseInt("zzzzz", tokenRadix)), tokenRadix).toUpperCase())
                .replace(' ', '0');
        }

        @Override
        public synchronized void getUniqueIdentifier(final @Nonnull Consumer<String> callback) {
            callback.accept(ident);
        }

        public synchronized void invalidate() {
            valid = false;
            tokenMap.remove(this);
        }

        @Override
        public int hashCode() {
            return Integer.parseInt(ident.toLowerCase(), tokenRadix);
        }

        public boolean isValid() {
            return this.valid;
        }
    }
}
