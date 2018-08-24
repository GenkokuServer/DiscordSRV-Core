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
package com.discordsrv.core.discord;

import com.discordsrv.core.conf.ConfigAware;
import com.discordsrv.core.conf.Configuration;
import com.discordsrv.core.conf.annotation.Configured;
import com.discordsrv.core.conf.annotation.Val;
import com.discordsrv.core.debug.annotation.DisableDebug;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;

/**
 * DSRVJDABuilder type, for building JDAs for DSRV.
 */
public class DSRVJDABuilder extends JDABuilder implements ConfigAware {

    private final CommandClientBuilder commandClientBuilder;
    private Configuration config;

    /**
     * Main configured constructor for the DSRVJDABuilder type.
     *
     * @param token
     *         The token to be used for this JDA instance.
     * @param prefix
     *         The prefix to be used for commands.
     * @param gameName
     *         The name to use for the game.
     * @param gameType
     *         The type to use for the game.
     */
    @Configured
    public DSRVJDABuilder(final @Val("token") String token, final @Val("prefix") String prefix,
                          final @Val("game.name") String gameName, final @Val("game.type") int gameType) {
        super(AccountType.BOT);
        this.setToken(token);
        commandClientBuilder = new CommandClientBuilder();
        commandClientBuilder.setPrefix(prefix);
        commandClientBuilder.setOwnerId("000000000000000000"); // Nobody
        commandClientBuilder.setGame(Game.of(Game.GameType.fromKey(gameType), gameName));
    }

    @DisableDebug
    @Override
    public JDABuilder setToken(final String token) {
        return super.setToken(token);
    }

    @Override
    public JDA build() throws LoginException {
        this.addEventListener(new EventWaiter());
        this.addEventListener(this.commandClientBuilder.build());
        return super.build();
    }

    /**
     * Add commands to this DSRV JDA instance.
     *
     * @param commands
     *         The commands to be added.
     *
     * @return self This instance.
     */
    public DSRVJDABuilder addCommands(final @Nonnull Command... commands) {
        commandClientBuilder.addCommands(commands);
        return this;
    }

    @Override
    public void setConfig(final Configuration config) {
        this.config = config;
    }
}
