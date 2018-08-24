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
import org.apache.commons.collections4.bidimap.DualTreeBidiMap;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Tests {@link LocalPlayerUserLinker}.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LocalPlayerUserLinkerTest {

    private static LocalPlayerUserLinker linker;
    private static UUID testMCId;
    private final String testDiscordId = "1234";
    private final Mocker mocker = new Mocker();

    /**
     * Creates the linker.
     */
    @BeforeClass
    public static void setup() {
        testMCId = UUID.randomUUID();
        linker = new LocalPlayerUserLinker(new DualTreeBidiMap<>(), new TestPlayerUserLookup());
    }

    /**
     * Destroys the linker.
     */
    @AfterClass
    public static void tearDown() {
        testMCId = null;
        linker = null;
    }

    /**
     * Tests {@link LocalPlayerUserLinker#push(MinecraftPlayer, User, FutureCallback)}.
     */
    @Test
    public void stage1Push() {
        linker.push(new TestMinecraftPlayer("Test", testMCId), mocker.getMockedUser(testDiscordId),
            new FutureCallback<Boolean>() {
                @Override
                public void onSuccess(@Nullable final Boolean result) {
                    assertNotNull(result);
                    assertTrue(result);
                }

                @Override
                public void onFailure(final @Nonnull Throwable t) {
                    t.printStackTrace();
                    fail();
                }
            });
    }

    /**
     * Tests {@link LocalPlayerUserLinker#translate(MinecraftPlayer, FutureCallback)}.
     */
    @Test
    public void stage2Translate() {
        linker.translate(new TestMinecraftPlayer("Test", testMCId), new FutureCallback<User>() {
            @Override
            public void onSuccess(@Nullable final User result) {
                assertNotNull(result);
                assertEquals(testDiscordId, result.getId());
            }

            @Override
            public void onFailure(final @Nonnull Throwable t) {
                fail();
            }
        });
    }

    /**
     * Tests {@link LocalPlayerUserLinker#translate(User, FutureCallback)}.
     */
    @Test
    public void stage3Translate() {
        linker.translate(mocker.getMockedUser(testDiscordId), new FutureCallback<MinecraftPlayer>() {
            @Override
            public void onSuccess(@Nullable final MinecraftPlayer result) {
                assertNotNull(result);
                result.getUniqueIdentifier(ident -> assertEquals(testMCId, ident));
            }

            @Override
            public void onFailure(final @Nonnull Throwable t) {
                fail();
            }
        });
    }

    /**
     * Tests {@link LocalPlayerUserLinker#remove(MinecraftPlayer, User, FutureCallback)}.
     */
    @Test
    public void stage4Remove() {
        linker.remove(new TestMinecraftPlayer("Test", testMCId), mocker.getMockedUser(testDiscordId),
            new FutureCallback<Boolean>() {
                @Override
                public void onSuccess(@Nullable final Boolean result) {
                    assertNotNull(result);
                    assertTrue(result);
                    try {
                        stage2Translate();
                        fail();
                    } catch (AssertionError ignored) {
                    }
                    try {
                        stage3Translate();
                        fail();
                    } catch (AssertionError ignored) {
                    }
                }

                @Override
                public void onFailure(final @Nonnull Throwable t) {
                    fail();
                }
            });
    }

}
