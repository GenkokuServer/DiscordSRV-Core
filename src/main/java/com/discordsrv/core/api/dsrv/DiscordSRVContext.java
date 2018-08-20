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
package com.discordsrv.core.api.dsrv;

import com.discordsrv.core.api.channel.ChatChannelLinker;
import com.discordsrv.core.api.channel.ChatChannelLookup;
import com.discordsrv.core.api.role.TeamRoleLinker;
import com.discordsrv.core.api.role.TeamRoleLookup;
import com.discordsrv.core.api.user.PlayerUserLinker;
import com.discordsrv.core.api.user.PlayerUserLookup;
import com.discordsrv.core.auth.PlayerUserAuthenticator;
import com.discordsrv.core.conf.Configuration;
import net.dv8tion.jda.core.JDA;

import java.util.function.Consumer;

/**
 * DiscordSRVContext type, for contextualizing DSRV.
 */
public interface DiscordSRVContext {

    /**
     * Fetches the Configuration of this DSRVContext.
     *
     * @return configuration The Configuration.
     */
    Configuration getConfiguration();

    /**
     * Fetches the event handler of this DSRVContext.
     *
     * @return eventHandler The event handler.
     */
    Consumer<Runnable> getEventHandler();

    /**
     * Fetches the PlayerUserAuthenticator of this DSRVContext.
     *
     * @return playerUserAuthenticator The PlayerUserAuthenticator.
     */
    PlayerUserAuthenticator getUserAuthenticator();

    /**
     * Fetches the PlayerUserLinker of this DSRVContext.
     *
     * @return playerUserLinker The PlayerUserLinker.
     */
    PlayerUserLinker getPlayerUserLinker();

    /**
     * Fetches the PlayerUserLookup of this DSRVContext.
     *
     * @return playerUserLookup The PlayerUserLookup.
     */
    PlayerUserLookup getPlayerUserLookup();

    /**
     * Fetches the TeamRoleLinker of this DSRVContext.
     *
     * @return teamRoleLinker The TeamRoleLinker.
     */
    TeamRoleLinker getTeamRoleLinker();

    /**
     * Fetches the TeamRoleLookup of this DSRVContext.
     *
     * @return teamRoleLookup The TeamRoleLookup.
     */
    TeamRoleLookup getTeamRoleLookup();

    /**
     * Fetches the ChatChannelLinker of this DSRVContext.
     *
     * @return chatChannelLinker The ChatChannelLinker.
     */
    ChatChannelLinker getChatChannelLinker();

    /**
     * Fetches the ChatChannelLookup of this DSRVContext.
     *
     * @return chatChannelLookup The ChatChannelLookup.
     */
    ChatChannelLookup getChatChannelLookup();

    /**
     * Fetches the JDA of this DSRVContext.
     *
     * @return jda The JDA.
     */
    JDA getJDA();

}
