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

import com.discordsrv.core.api.role.Team;
import com.discordsrv.core.api.role.TeamRoleLinker;
import com.discordsrv.core.api.role.TeamRoleLookup;
import com.discordsrv.core.api.user.MinecraftPlayer;
import com.google.common.util.concurrent.FutureCallback;
import net.dv8tion.jda.core.entities.Role;
import org.apache.commons.collections4.BidiMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Leverages a local storage for role/team linking.
 */
public class LocalTeamRoleLinker implements TeamRoleLinker {

    private final BidiMap<String, Long> roleStorage;
    private final TeamRoleLookup lookup;

    /**
     * Main constructor for the LocalTeamRoleLinker type.
     *
     * @param roleStorage
     *         The map to store the links in. This should update files when updated.
     * @param lookup
     *         The lookup service.
     */
    public LocalTeamRoleLinker(final BidiMap<String, Long> roleStorage, final TeamRoleLookup lookup) {
        this.roleStorage = roleStorage;
        this.lookup = lookup;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void translate(final @Nonnull Team<MinecraftPlayer> playerTeam,
                          final @Nonnull FutureCallback<Role> callback) {
        playerTeam.getUniqueIdentifier(ident -> {
            @Nullable Long result = roleStorage.get(ident);
            if (result == null) {
                callback.onSuccess(null);
            } else {
                lookup.lookup(result, callback);
            }
        });
    }

    @Override
    public void translate(final @Nonnull Role role, final @Nonnull FutureCallback<Team<MinecraftPlayer>> callback) {
        @Nullable String result = roleStorage.getKey(role.getIdLong());
        if (result == null) {
            callback.onSuccess(null);
        } else {
            lookup.lookup(result, callback);
        }
    }

}
