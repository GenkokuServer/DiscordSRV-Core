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
package com.discordsrv.core.api.role;

import com.discordsrv.core.api.common.functional.Translator;
import com.discordsrv.core.api.user.MinecraftPlayer;
import com.google.common.util.concurrent.FutureCallback;
import net.dv8tion.jda.core.entities.Role;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.ThreadSafe;

/**
 * TeamRoleLinker type, for looking up {@link Team} instances and {@link Role} instances.
 */
@ThreadSafe
@ParametersAreNonnullByDefault
public interface TeamRoleLookup {

    /**
     * Performs a lookup for {@link Role} instances given a specific id.
     * <p>
     * Convert this to a {@link Translator} with {@code lookup::lookupRole}.
     *
     * @param id
     *         The ID of the role.
     * @param callback
     *         The callback to invoke when (not) found.
     */
    void lookupRole(String id, FutureCallback<Role> callback);

    /**
     * Performs a lookup for {@link Team} instances given a specific id.
     * <p>
     * Convert this to a {@link Translator} with {@code lookup::lookupTeam}.
     *
     * @param id
     *         The ID of the team.
     * @param callback
     *         The callback to invoke when (not) found.
     */
    void lookupTeam(String id, FutureCallback<Team<MinecraftPlayer>> callback);

}
