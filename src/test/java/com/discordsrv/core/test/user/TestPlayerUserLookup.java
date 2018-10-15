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
package com.discordsrv.core.test.user;

import com.discordsrv.core.api.user.MinecraftPlayer;
import com.discordsrv.core.api.user.PlayerUserLookup;
import com.discordsrv.core.test.mocker.Mocker;
import com.google.common.util.concurrent.FutureCallback;
import net.dv8tion.jda.core.entities.User;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * A simple Test implementation of the PlayerUserLookup interface.
 */
@ParametersAreNonnullByDefault
public class TestPlayerUserLookup implements PlayerUserLookup {

    private final Mocker mocker = new Mocker();

    @Override
    public void lookupUser(final String id, final FutureCallback<User> callback) {
        callback.onSuccess(mocker.getMockedUser(id));
    }

    @Override
    public void lookupPlayer(final UUID id, final FutureCallback<MinecraftPlayer> callback) {
        callback.onSuccess(new TestMinecraftPlayer("Test", id));
    }

    @Override
    public void getOnlinePlayers(final FutureCallback<Stream<MinecraftPlayer>> callback) {
        callback.onSuccess(Stream.empty());
    }

    @Override
    public void getOnlineUsers(final FutureCallback<Stream<User>> callback) {
        callback.onSuccess(Stream.empty());
    }
}
