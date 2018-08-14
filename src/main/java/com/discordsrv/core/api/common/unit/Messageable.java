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
package com.discordsrv.core.api.common.unit;

import com.google.common.util.concurrent.FutureCallback;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Messageable type, for establishing that this type can be messaged.
 *
 * @param <M>
 *         The type of message to be sent.
 * @param <R>
 *         The result type of this message.
 */
@ThreadSafe
public interface Messageable<M, R> {

    /**
     * Sends a message to this instance. If the message could not be completed, the {@link
     * FutureCallback#onFailure(Throwable)} method will be invoked. Otherwise, a result will be sent to {@link
     * FutureCallback#onSuccess(Object)} which is appropriate for this message (possibly null).
     *
     * @param message
     *         The message which needs to be sent.
     * @param resultCallback
     *         The callback for this method.
     */
    void sendMessage(M message, FutureCallback<R> resultCallback);

}
