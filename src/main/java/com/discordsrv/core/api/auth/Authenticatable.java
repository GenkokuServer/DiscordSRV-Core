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

import com.discordsrv.core.api.common.unit.Translatable;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.ThreadSafe;
import java.util.function.Consumer;

/**
 * Authenticatable type, for representing types that require authentication.
 *
 * @param <T>
 *         The type to uniquely identify this authenticatable instance with.
 * @param <R>
 *         The type to authenticate this instance as.
 */
@ThreadSafe
@ParametersAreNonnullByDefault
public interface Authenticatable<T, R> extends Translatable<T, R> {

    /**
     * Fetches the authentication state of this authenticatable instance.
     *
     * @param callback
     *         The callback for this getter.
     */
    void getAuthenticationStage(Consumer<State> callback);

    /**
     * Sets the authentication state of this authenticatable instance.
     *
     * @param state
     *         The new state of this authenticatable instance.
     */
    void setAuthenticationStage(State state);

}
