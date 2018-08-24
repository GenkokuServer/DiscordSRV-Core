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
package com.discordsrv.core.test.security;

import java.io.FilePermission;
import java.lang.reflect.ReflectPermission;
import java.security.Permission;
import java.util.Arrays;
import java.util.List;

/**
 * A security manager with toggleable security.
 */
public class ToggleableSecurityManager extends SecurityManager {

    private final List<Class<? extends Permission>> alwaysPermitted =
        Arrays.asList(ReflectPermission.class, FilePermission.class, RuntimePermission.class); // junit permissions
    private boolean allowed = true;

    @Override
    public void checkConnect(final String host, final int port) {
        if (!allowed) {
            throw new SecurityException();
        }
    }

    @Override
    public void checkPermission(final Permission perm) {
        if (!allowed && alwaysPermitted.stream().noneMatch(permitted -> permitted.equals(perm.getClass()))) {
            super.checkPermission(perm);
        }
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }
}
