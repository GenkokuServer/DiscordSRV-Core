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
package com.discordsrv.integration.plugin;

import com.discordsrv.core.api.channel.ChatChannelLinker;
import com.discordsrv.core.api.dsrv.DiscordSRVContext;
import com.discordsrv.core.api.role.TeamRoleLinker;
import com.discordsrv.core.api.role.TeamRoleLookup;
import com.discordsrv.core.api.user.PlayerUserLinker;
import com.discordsrv.core.api.user.PlayerUserLookup;
import com.discordsrv.core.auth.PlayerUserAuthenticator;
import com.discordsrv.core.conf.Configuration;
import com.discordsrv.integration.Minecraft;
import com.discordsrv.integration.plugin.chat.MalleableChatChannelLookup;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.dv8tion.jda.core.JDA;

import java.util.function.Consumer;

/**
 * This is a DSRVContext implementation, but note that it is suggested that you do <strong>NOT</strong> have this be
 * settable! Instead, use final values and configure as-necessary with malleable return values.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public final class IntegrationDSRVContext implements DiscordSRVContext {

    Configuration configuration;
    Consumer<Runnable> eventHandler;
    PlayerUserAuthenticator userAuthenticator;
    PlayerUserLinker playerUserLinker;
    PlayerUserLookup playerUserLookup;
    TeamRoleLinker teamRoleLinker;
    TeamRoleLookup teamRoleLookup;
    ChatChannelLinker chatChannelLinker;
    MalleableChatChannelLookup chatChannelLookup;
    Minecraft minecraft;
    JDA jda;

}
