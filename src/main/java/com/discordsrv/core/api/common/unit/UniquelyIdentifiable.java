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
package com.discordsrv.core.api.common.unit;

import com.google.common.util.concurrent.FutureCallback;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * UniquelyIdentifiable type, for establishing types as uniquely identifiable.
 *
 * @param <T>
 *         The type used to identify the identifiable instance.
 */
@ThreadSafe
public interface UniquelyIdentifiable<T> {

    /**
     * Fetches the identifier for this uniquely identifiable type.
     *
     * @param callback
     *         The callback of this comparison.
     */
    void getUniqueIdentifier(Consumer<T> callback);

    /**
     * Compares two uniquely identifiable types with the same identifiable parameter.
     *
     * @param identifiable
     *         The other identifiable.
     * @param callback
     *         The callback of this comparison.
     */
    default void hasMatchingIdentifier(UniquelyIdentifiable<T> identifiable, FutureCallback<Boolean> callback) {
        getUniqueIdentifier(thisIdentifier -> identifiable.getUniqueIdentifier(
            otherIdentifiable -> callback.onSuccess(Objects.equals(thisIdentifier, otherIdentifiable))));
    }

}
