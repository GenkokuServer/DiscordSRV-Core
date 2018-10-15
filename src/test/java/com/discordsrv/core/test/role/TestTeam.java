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
package com.discordsrv.core.test.role;

import com.discordsrv.core.api.role.Team;
import com.discordsrv.core.api.user.MinecraftPlayer;
import com.google.common.util.concurrent.FutureCallback;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * A simple test implementation of the {@link Team} type.
 */
@ParametersAreNonnullByDefault
public class TestTeam implements Team<MinecraftPlayer> {

    private final List<MinecraftPlayer> players;
    private final CharSequence name;
    private final String identifier;

    /**
     * Main constructor for the TestTeam type.
     *
     * @param name
     *         The name of this instance.
     * @param identifier
     *         The unique identifier to be associated with this instance.
     */
    public TestTeam(final List<MinecraftPlayer> players, final CharSequence name, final String identifier) {
        this.players = players;
        this.name = name;
        this.identifier = identifier;
    }

    @Override
    public void getName(final Consumer<CharSequence> callback) {
        callback.accept(this.name);
    }

    @Override
    public void getUniqueIdentifier(final Consumer<String> callback) {
        callback.accept(this.identifier);
    }

    @Override
    public void getMembers(final Consumer<Collection<MinecraftPlayer>> callback) {
        callback.accept(Collections.unmodifiableCollection(players));
    }

    @Override
    public void addMember(final MinecraftPlayer member, final FutureCallback<Void> callback) {
        try {
            players.add(member);
            callback.onSuccess(null);
        } catch (Throwable t) {
            callback.onFailure(t);
        }
    }

}
