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
package com.discordsrv.integration.plugin.chat;

import com.discordsrv.core.api.channel.Chat;
import com.discordsrv.core.test.mocker.Mocker;
import com.discordsrv.integration.Minecraft;
import com.discordsrv.integration.plugin.Plugin;
import com.google.common.util.concurrent.FutureCallback;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MalleableChatChannelLookupTest {

    private static Plugin plugin;

    @BeforeClass
    public static void setup() throws IOException {
        Mocker mocker = new Mocker();
        plugin = new Plugin();
        plugin.getContext().setJda(mocker
            .getMockedJDA(mocker::getSimpleMockedTextChannel, mocker::getMockedUser, mocker.getMockedUser("self")));
        plugin.setMinecraft(new Minecraft(new LinkedList<>(), new LinkedList<>(), new LinkedList<>()));
    }

    @AfterClass
    public static void tearDown() {
        plugin = null;
    }

    @Test
    public void lookupChat() throws InterruptedException {
        plugin.getContext().getChatChannelLookup().getChatTranslators().add(
            (id, callback) -> callback.onSuccess(Mocker.getInstance(Chat.class, new Mocker.NoopInvocationHandler() {
            })));
        ensureLookup();
    }

    private void ensureLookup() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        plugin.getContext().getChatChannelLookup().lookupChat("test", new FutureCallback<Chat>() {
            @Override
            public void onSuccess(@Nullable final Chat result) {
                if (result == null) {
                    fail();
                } else {
                    assertTrue(Proxy.isProxyClass(result.getClass()));
                    latch.countDown();
                }
            }

            @Override
            public void onFailure(final @Nonnull Throwable t) {
                fail();
            }
        });
        if (!latch.await(10, TimeUnit.SECONDS)) {
            fail();
        }
    }

}
