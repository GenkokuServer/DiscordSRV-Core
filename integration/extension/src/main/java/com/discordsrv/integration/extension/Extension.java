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
package com.discordsrv.integration.extension;

import com.discordsrv.core.api.channel.Chat;
import com.discordsrv.core.test.mocker.Mocker;
import com.discordsrv.integration.platform.IntegrationDSRVContext;
import com.discordsrv.integration.platform.Plugin;

public class Extension implements
    com.discordsrv.core.api.dsrv.platform.extension.Extension<IntegrationDSRVContext, Plugin> {

    private Plugin plugin;

    @Override
    public void setPlugin(final Plugin plugin) {
        this.plugin = plugin;
        plugin.getContext().getChatChannelLookup().addChatTranslator(
            (id, callback) -> callback.onSuccess(Mocker.getInstance(Chat.class, new Mocker.NoopInvocationHandler() {
            })));
    }

    @Override
    public String getName() {
        return "TestExtension";
    }
}
