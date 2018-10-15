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
import com.discordsrv.core.test.security.ToggleableSecurityManager;
import com.discordsrv.core.test.user.TestMinecraftPlayer;
import com.discordsrv.core.test.user.TestPlayerUserLookup;
import com.google.common.util.concurrent.FutureCallback;
import net.dv8tion.jda.core.entities.User;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.annotation.Nullable;
import java.util.UUID;

import static com.discordsrv.core.test.Values.HTTP_PORT;
import static org.junit.Assert.*;

/**
 * Tests for the {@link UplinkedPlayerUserLinker} class.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UplinkedPlayerUserLinkerTest {

    private final Mocker mocker = new Mocker();
    private static UplinkedPlayerUserLinker linker;
    private static ToggleableSecurityManager manager;

    /**
     * Sets up test resources.
     */
    @BeforeClass
    public static void setup() {
        linker = new UplinkedPlayerUserLinker(new TestPlayerUserLookup());
        manager = new ToggleableSecurityManager();
    }

    /**
     * Tears down test resources.
     */
    @AfterClass
    public static void tearDown() {
        linker = null;
        manager = null;
    }

    /**
     * Tests the player -> user translation.
     */
    @Test
    public void stage1TranslatePlayer() {
        MinecraftPlayer player =
            new TestMinecraftPlayer("Scarsz", UUID.fromString("d7c1db4d-e57b-488b-b8bc-4462fe49a3e8"));
        linker.translate(player, new FutureCallback<User>() {
            @Override
            public void onSuccess(@Nullable final User result) {
                assertNotNull(result);
                assertEquals("95088531931672576", result.getId());
            }

            @Override
            public void onFailure(final Throwable t) {
                t.printStackTrace();
                fail();
            }
        });
    }

    /**
     * Applies a security manager which disables access to link.discordsrv.com.
     */
    @Test
    public void stage2ApplySecurity() {
        manager.setAllowed(false);
        System.setSecurityManager(manager);
        try {
            manager.checkConnect("link.discordsrv.com", HTTP_PORT);
            fail();
        } catch (SecurityException ignored) {
        }
    }

    /**
     * Tests the user -> player translation, offline.
     */
    @Test
    public void stage3TranslateUserOffline() {
        User user = mocker.getMockedUser("95088531931672576");
        linker.translate(user, new FutureCallback<MinecraftPlayer>() {
            @Override
            public void onSuccess(@Nullable final MinecraftPlayer result) {
                assertNotNull(result);
                result.getUniqueIdentifier(
                    ident -> assertEquals(UUID.fromString("d7c1db4d-e57b-488b-b8bc-4462fe49a3e8"), ident));
            }

            @Override
            public void onFailure(final Throwable t) {
                fail();
            }
        });
    }

    /**
     * Tests the player -> user translation, offline.
     */
    @Test
    public void stage4TranslatePlayerOffline() {
        stage1TranslatePlayer();
    }

    /**
     * Perform an uncache of the known player.
     */
    @Test
    public void stage5Uncache() {
        MinecraftPlayer player =
            new TestMinecraftPlayer("Scarsz", UUID.fromString("d7c1db4d-e57b-488b-b8bc-4462fe49a3e8"));
        linker.uncache(player);
    }

    /**
     * Perform a lookup which must fail due to a SecurityException (as the socket permission is still disallowed).
     */
    @Test
    public void stage6FailedLookup() {
        MinecraftPlayer player =
            new TestMinecraftPlayer("Scarsz", UUID.fromString("d7c1db4d-e57b-488b-b8bc-4462fe49a3e8"));
        linker.translate(player, new FutureCallback<User>() {
            @Override
            public void onSuccess(@Nullable final User result) {
                fail();
            }

            @Override
            public void onFailure(final Throwable t) {
                assertTrue(t instanceof SecurityException);
            }
        });
    }

    /**
     * Removes the security manager.
     */
    @Test
    public void stage7RemoveSecurity() {
        manager.setAllowed(true);
        System.setSecurityManager(null);
    }

}
