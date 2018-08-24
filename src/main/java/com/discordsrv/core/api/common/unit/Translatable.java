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

import javax.annotation.concurrent.ThreadSafe;

/**
 * Translatable type, for establishing types that may be translated from this type to another.
 *
 * @param <T>
 *         The type to be used to identify this identifiable.
 * @param <R>
 *         The type to be converted to. Note that this type is not necessarily also {@link UniquelyIdentifiable}.
 */
@ThreadSafe
public interface Translatable<T, R> extends UniquelyIdentifiable<T> {

}
