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
package com.discordsrv.core.api.role;

import com.discordsrv.core.api.common.functional.Translator;
import com.discordsrv.core.api.user.MinecraftPlayer;
import com.google.common.util.concurrent.FutureCallback;
import net.dv8tion.jda.core.entities.Role;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.ThreadSafe;

/**
 * TeamRoleLinker type, for linking {@link Team} instances and {@link Role} instances.
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

}
