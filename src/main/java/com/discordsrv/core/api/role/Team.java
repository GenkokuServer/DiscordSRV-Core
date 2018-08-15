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

import com.discordsrv.core.api.common.unit.Named;
import com.discordsrv.core.api.common.unit.Translatable;
import com.google.common.util.concurrent.FutureCallback;
import net.dv8tion.jda.core.entities.Role;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * Team type, for establishing a team or organization of members.
 *
 * @param <T>
 *         The type that this team contains.
 */
@ParametersAreNonnullByDefault
public interface Team<T> extends Translatable<String, Role>, Named {

    /**
     * Fetches the members of this team.
     *
     * @param callback
     *         The callback for the getter.
     */
    void getMembers(Consumer<Collection<T>> callback);

    /**
     * Adds members to this team.
     *
     * @param member
     *         The member to add.
     * @param callback
     *         The callback for this method.
     */
    void addMember(T member, FutureCallback<Void> callback);

}
