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
package com.discordsrv.core.auth;

import com.discordsrv.core.api.auth.Token;
import com.discordsrv.core.api.user.MinecraftPlayer;
import com.discordsrv.core.test.mocker.Mocker;
import com.discordsrv.core.test.user.TestMinecraftPlayer;
import com.discordsrv.core.test.user.TestPlayerUserLookup;
import com.discordsrv.core.user.LocalPlayerUserLinker;
import com.google.common.util.concurrent.FutureCallback;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.tuple.Pair;
import org.apache.commons.collections4.bidimap.DualTreeBidiMap;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static org.junit.Assert.*;

/**
 * Tests {@link PlayerUserAuthenticator}.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ParametersAreNonnullByDefault
public class PlayerUserAuthenticatorTest {

    private static Mocker mocker;
    private static MinecraftPlayer player;
    private static User user;
    private static ScheduledExecutorService service;
    private static LocalPlayerUserLinker linker;
    private static PlayerUserAuthenticator authenticator;
    private static String token;

    /**
     * Creates static fields.
     */
    @BeforeClass
    public static void setup() {
        mocker = new Mocker();
        player = new TestMinecraftPlayer("Test", UUID.randomUUID());
        user = mocker.getMockedUser("1234");
        service = Executors.newSingleThreadScheduledExecutor();
        linker = new LocalPlayerUserLinker(new DualTreeBidiMap<>(), new TestPlayerUserLookup());
        authenticator = new PlayerUserAuthenticator(linker, service);
    }

    /**
     * Destroys static fields.
     */
    @AfterClass
    public static void tearDown() {
        mocker = null;
        player = null;
        user = null;
        service.shutdown();
        service = null;
        linker = null;
        authenticator = null;
    }

    /**
     * Tests {@link PlayerUserAuthenticator#beginAuth(MinecraftPlayer, FutureCallback)}.
     */
    @Test
    public void stage1BeginAuth() {
        authenticator.beginAuth(player, new FutureCallback<Token>() {
            @Override
            public void onSuccess(@Nullable final Token result) {
                assertNotNull(result);
                result.getUniqueIdentifier(ident -> token = ident);
            }

            @Override
            public void onFailure(final Throwable t) {
                t.printStackTrace();
                fail();
            }
        });
    }

    /**
     * Tests {@link PlayerUserAuthenticator#attemptVerify(User, String, FutureCallback)}.
     */
    @Test
    public void stage2AttemptVerify() {
        authenticator.attemptVerify(user, token, new FutureCallback<Pair<MinecraftPlayer, User>>() {
            @Override
            public void onSuccess(@Nullable final Pair<MinecraftPlayer, User> result) {
                assertNotNull(result);
                result.getLeft().hasMatchingIdentifier(player, new FutureCallback<Boolean>() {
                    @Override
                    public void onSuccess(@Nullable final Boolean result) {
                        assertNotNull(result);
                        assertTrue(result);
                        linker.translate(player, new FutureCallback<User>() {
                            @Override
                            public void onSuccess(@Nullable final User result) {
                                assertNotNull(result);
                                assertEquals(user.getId(), result.getId());
                            }

                            @Override
                            public void onFailure(final Throwable t) {
                                fail();
                            }
                        });
                        linker.translate(user, new FutureCallback<MinecraftPlayer>() {
                            @Override
                            public void onSuccess(@Nullable final MinecraftPlayer result) {
                                assertNotNull(result);
                                result.hasMatchingIdentifier(player, new FutureCallback<Boolean>() {
                                    @Override
                                    public void onSuccess(@Nullable final Boolean result) {
                                        assertNotNull(result);
                                        assertTrue(result);
                                    }

                                    @Override
                                    public void onFailure(final Throwable t) {
                                        fail();
                                    }
                                });
                            }

                            @Override
                            public void onFailure(final Throwable t) {
                                fail();
                            }
                        });
                    }

                    @Override
                    public void onFailure(final Throwable t) {
                        fail();
                    }
                });
                assertEquals(user.getId(), result.getRight().getId());
            }

            @Override
            public void onFailure(final Throwable t) {
                t.printStackTrace();
                fail();
            }
        });
    }

    /**
     * Tests {@link PlayerUserAuthenticator#unauthenticate(MinecraftPlayer, FutureCallback)}.
     */
    @Test
    public void stage3unauthenticate() {
        authenticator.unauthenticate(player, new FutureCallback<Boolean>() {
            @Override
            public void onSuccess(@javax.annotation.Nullable final Boolean result) {
                assertNotNull(result);
                assertTrue(result);
            }

            @Override
            public void onFailure(final Throwable t) {
                fail();
            }
        });
    }

    /**
     * Tests {@link PlayerUserAuthenticator#unauthenticate(User, FutureCallback)}.
     */
    @Test
    public void stage4unauthenticate() {
        stage1BeginAuth();
        stage2AttemptVerify();
        authenticator.unauthenticate(user, new FutureCallback<Boolean>() {
            @Override
            public void onSuccess(@javax.annotation.Nullable final Boolean result) {
                assertNotNull(result);
                assertTrue(result);
            }

            @Override
            public void onFailure(final Throwable t) {
                fail();
            }
        });
    }

}
