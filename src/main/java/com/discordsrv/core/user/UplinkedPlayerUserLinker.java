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
import com.discordsrv.core.api.user.PlayerUserLookup;
import com.discordsrv.core.conf.annotation.Configured;
import com.discordsrv.core.conf.annotation.Val;
import com.google.common.util.concurrent.FutureCallback;
import net.dv8tion.jda.core.entities.User;

import javax.annotation.Nonnull;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Leverages link.discordsrv.com to perform lookups of player/user links.
 * <p>
 * TODO Finish link.discordsrv.com
 */
public class UplinkedPlayerUserLinker implements PlayerUserLinker {

    private final ConcurrentMap<String, User> playerCache = new ConcurrentHashMap<>();
    private final PlayerUserLookup lookup;

    /**
     * Main constructor for the UplinkedPlayerUserLinker type.
     *
     * @param lookup
     *         The lookup service.
     */
    @Configured
    public UplinkedPlayerUserLinker(final @Val("lookup") PlayerUserLookup lookup) {
        this.lookup = lookup;
    }

    @Override
    public void translate(final @Nonnull MinecraftPlayer player, final @Nonnull FutureCallback<User> callback) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public void translate(final @Nonnull User user, final @Nonnull FutureCallback<MinecraftPlayer> callback) {
        // TODO
        throw new UnsupportedOperationException();
    }

}
