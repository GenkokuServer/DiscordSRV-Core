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
package com.discordsrv.core.test.minecraft;

import com.discordsrv.core.api.minecraft.Console;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * Test implementation of the {@link Console} type.
 */
public class TestConsole implements Console {

    private Consumer<String> onInvocation;

    @Override
    public void invoke(final String cmd) {
        onInvocation.accept(cmd);
    }

    public Consumer<String> getOnInvocation() {
        return onInvocation;
    }

    public void setOnInvocation(final @Nonnull Consumer<String> onInvocation) {
        this.onInvocation = onInvocation;
    }
}
