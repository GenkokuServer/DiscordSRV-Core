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
package com.discordsrv.core.role;

import com.discordsrv.core.common.functional.Translator;
import com.discordsrv.core.user.MinecraftPlayer;
import com.google.common.util.concurrent.FutureCallback;
import net.dv8tion.jda.core.entities.Role;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.ThreadSafe;

/**
 * TeamRoleLinker type, for linking {@link Team} instances and {@link Role} instances and performing lookups between
 * them.
 */
@ThreadSafe
@ParametersAreNonnullByDefault
public interface TeamRoleLinker {

    /**
     * Translates from a {@link Team} to a {@link Role}.
     * <p>
     * Convert this to a {@link Translator} with {@code linker::translate}.
     *
     * @param team
     *         The team to translate.
     * @param callback
     *         The callback to invoke once a translation is (not) found.
     */
    void translate(Team<MinecraftPlayer> team, FutureCallback<Role> callback);

    /**
     * Performs a lookup for {@link Role} instances given a specific id.
     * <p>
     * Convert this to a {@link Translator} with {@code linker::lookup}.
     *
     * @param id
     *         The ID of the role.
     * @param callback
     *         The callback to invoke when (not) found.
     */
    void lookup(long id, FutureCallback<Role> callback);

    /**
     * Translates from a {@link Role} to a {@link Team}.
     * <p>
     * Convert this to a {@link Translator} with {@code linker::translate}.
     *
     * @param role
     *         The role to translate.
     * @param callback
     *         The callback to invoke once a translation is (not) found.
     */
    void translate(Role role, FutureCallback<Team<MinecraftPlayer>> callback);

    /**
     * Performs a lookup for {@link Team} instances given a specific id.
     * <p>
     * Convert this to a {@link Translator} with {@code linker::lookup}.
     *
     * @param id
     *         The ID of the team.
     * @param callback
     *         The callback to invoke when (not) found.
     */
    void lookup(String id, FutureCallback<Team<MinecraftPlayer>> callback);

}
