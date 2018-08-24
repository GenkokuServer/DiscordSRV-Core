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
package com.discordsrv.integration.platform.player;

import com.discordsrv.core.api.user.MinecraftPlayer;
import com.discordsrv.integration.user.Player;
import com.google.common.util.concurrent.FutureCallback;
import lombok.Value;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@Value
public class PlayerWrapper implements MinecraftPlayer {

    Player player;

    @Override
    public void sendMessage(final String message, final FutureCallback<Void> resultCallback) {
        player.sendMessage(message);
        resultCallback.onSuccess(null);
    }

    @Override
    public void getName(final Consumer<CharSequence> callback) {
        callback.accept(player.getName());
    }

    @Override
    public void getUniqueIdentifier(final Consumer<UUID> callback) {
        callback.accept(UUID.nameUUIDFromBytes(player.getName().getBytes()));
    }
}
