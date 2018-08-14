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
package com.discordsrv.core.api.common.functional;

import com.google.common.util.concurrent.FutureCallback;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Translator type, for translating from one thing to another.
 *
 * @param <T>
 *         The original type to translate from.
 * @param <R>
 *         The type of the result of the translation.
 */
@ThreadSafe
@FunctionalInterface
public interface Translator<T, R> {

    /**
     * Convert from the original type to the target translation.
     *
     * @param original
     *         The original instance to translate.
     * @param callback
     *         The callback to invoke after translation.
     */
    void translate(T original, FutureCallback<R> callback);

}
