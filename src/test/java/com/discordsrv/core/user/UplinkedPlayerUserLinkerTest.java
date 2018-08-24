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
package com.discordsrv.core.user;

import com.discordsrv.core.api.user.MinecraftPlayer;
import com.discordsrv.core.test.mocker.Mocker;
import com.discordsrv.core.test.user.TestMinecraftPlayer;
import com.discordsrv.core.test.user.TestPlayerUserLookup;
import com.google.common.util.concurrent.FutureCallback;
import net.dv8tion.jda.core.entities.User;
import org.junit.Ignore;
import org.junit.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Tests for the {@link UplinkedPlayerUserLinker} class.
 * <p>
 * TODO Finish link.scarsz.me first
 */
public class UplinkedPlayerUserLinkerTest {

    private final Mocker mocker = new Mocker();

    /**
     * Tests the player -> user translation.
     */
    @Ignore // TODO remove
    @Test
    public void translate() {
        UplinkedPlayerUserLinker linker = new UplinkedPlayerUserLinker(new TestPlayerUserLookup());
        MinecraftPlayer player =
            new TestMinecraftPlayer("Scarsz", UUID.fromString("d7c1db4d-e57b-488b-b8bc-4462fe49a3e8"));
        linker.translate(player, new FutureCallback<User>() {
            @Override
            public void onSuccess(@Nullable final User result) {
                assertNotNull(result);
                assertEquals("95088531931672576", result.getId());
            }

            @Override
            public void onFailure(final @Nonnull Throwable t) {
                fail();
            }
        });
    }

    /**
     * Tests the user -> player translation.
     */
    @Ignore // TODO remove
    @Test
    public void translate1() {
        UplinkedPlayerUserLinker linker = new UplinkedPlayerUserLinker(new TestPlayerUserLookup());
        User user = mocker.getMockedUser("95088531931672576");
        linker.translate(user, new FutureCallback<MinecraftPlayer>() {
            @Override
            public void onSuccess(@Nullable final MinecraftPlayer result) {
                assertNotNull(result);
                result.getUniqueIdentifier(
                    ident -> assertEquals(UUID.fromString("d7c1db4d-e57b-488b-b8bc-4462fe49a3e8"), ident));
            }

            @Override
            public void onFailure(final @Nonnull Throwable t) {
                fail();
            }
        });
    }

}
