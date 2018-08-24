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

/**
 * PlayerUserLinker type, for linking {@link MinecraftPlayer} instances and {@link User} instances.
 */
@ThreadSafe
@ParametersAreNonnullByDefault
public interface PlayerUserLinker {

    /**
     * Translates from a {@link MinecraftPlayer} to a {@link User}.
     * <p>
     * Convert this to a {@link Translator} with {@code linker::translate}.
     *
     * @param player
     *         The player to translate.
     * @param callback
     *         The callback to invoke once a translation is (not) found.
     */
    void translate(MinecraftPlayer player, FutureCallback<User> callback);

    /**
     * Translates from a {@link User} to a {@link MinecraftPlayer}.
     * <p>
     * Convert this to a {@link Translator} with {@code linker::translate}.
     *
     * @param user
     *         The user to translate.
     * @param callback
     *         The callback to invoke once a translation is (not) found.
     */
    void translate(User user, FutureCallback<MinecraftPlayer> callback);

}
