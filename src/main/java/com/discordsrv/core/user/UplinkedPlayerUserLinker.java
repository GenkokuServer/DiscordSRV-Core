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

import com.discordsrv.core.api.user.MinecraftPlayer;
import com.discordsrv.core.api.user.PlayerUserLinker;
import com.google.common.util.concurrent.FutureCallback;
import net.dv8tion.jda.core.entities.User;

import javax.annotation.Nonnull;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

/**
 * Leverages link.scarsz.me to perform lookups of users.
 * <p>
 * TODO Finish link.scarsz.me
 */
public abstract class UplinkedPlayerUserLinker implements PlayerUserLinker {

    private final ConcurrentMap<String, User> playerCache;
    private final Consumer<Runnable> runnableConsumer;

    /**
     * Main constructor for the UplinkedPlayerUserLinker abstract type.
     *
     * @param runnableConsumer
     *         The consumer which accepts runnables for optionally async execution. (You could also just pass {@code
     *         Runnable::run} for synchronous handling).
     */
    protected UplinkedPlayerUserLinker(final @Nonnull Consumer<Runnable> runnableConsumer) {
        this.runnableConsumer = runnableConsumer;
        this.playerCache = new ConcurrentHashMap<>();
    }

    @Override
    public void translate(final @Nonnull MinecraftPlayer player, final @Nonnull FutureCallback<User> callback) {
        // TODO
    }

    @Override
    public void translate(final @Nonnull User user, final @Nonnull FutureCallback<MinecraftPlayer> callback) {
        // TODO
    }

}
