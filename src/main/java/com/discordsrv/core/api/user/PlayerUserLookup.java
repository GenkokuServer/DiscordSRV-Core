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
package com.discordsrv.core.api.user;

import com.discordsrv.core.api.common.functional.Translator;
import com.google.common.util.concurrent.FutureCallback;
import net.dv8tion.jda.core.entities.User;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.ThreadSafe;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * PlayerUserLookup type, for looking up {@link MinecraftPlayer} instances and {@link User} instances.
 */
@ThreadSafe
@ParametersAreNonnullByDefault
public interface PlayerUserLookup {

    /**
     * Performs a lookup for {@link User} instances given a specific id.
     * <p>
     * Convert this to a {@link Translator} with {@code lookup::lookupUser}.
     *
     * @param id
     *         The ID of the user.
     * @param callback
     *         The callback to invoke when (not) found.
     */
    void lookupUser(String id, FutureCallback<User> callback);

    /**
     * Performs a lookup for {@link MinecraftPlayer} instances given a specific id.
     * <p>
     * Convert this to a {@link Translator} with {@code lookup::lookupPlayer}.
     *
     * @param id
     *         The ID of the player.
     * @param callback
     *         The callback to invoke when (not) found.
     */
    void lookupPlayer(UUID id, FutureCallback<MinecraftPlayer> callback);

    /**
     * Fetches all online Minecraft players.
     *
     * @param callback
     *         The callback to invoke when users have been collected.
     */
    void getOnline(FutureCallback<Stream<MinecraftPlayer>> callback);

}
