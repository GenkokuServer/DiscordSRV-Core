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
package com.discordsrv.core.api.auth;

import com.google.common.util.concurrent.FutureCallback;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.ParametersAreNullableByDefault;
import javax.annotation.concurrent.ThreadSafe;

/**
 * AuthenticationStore, for representing types which may store authentication information.
 *
 * @param <T>
 *         The first component of the authentication.
 * @param <R>
 *         The final component of the authentication.
 */
@ThreadSafe
@ParametersAreNonnullByDefault
public interface AuthenticationStore<T, R> {

    /**
     * Pushes an authenticated pair to this store. States of authentication MUST be set after this method is invoked.
     *
     * @param first
     *         The first component of the authentication.
     * @param last
     *         The final component of the authentication.
     * @param callback
     *         The callback of this method.
     */
    void push(T first, R last, FutureCallback<Boolean> callback);

    /**
     * Removes any authenticated pair from this store which has either the first as the first component or the final as
     * the final component. States of authentication MUST be set after this method is invoked.
     *
     * @param first
     *         The first component of the authentication.
     * @param last
     *         The final component of the authentication.
     * @param callback
     *         The callback of this method.
     */
    @ParametersAreNullableByDefault
    void remove(T first, R last, @Nonnull FutureCallback<Boolean> callback);

}
