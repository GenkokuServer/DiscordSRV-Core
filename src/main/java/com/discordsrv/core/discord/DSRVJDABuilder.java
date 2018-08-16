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
package com.discordsrv.core.discord;

import com.discordsrv.core.conf.annotation.Configured;
import com.discordsrv.core.conf.annotation.Val;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;

public class DSRVJDABuilder extends JDABuilder {

    private final CommandClientBuilder commandClientBuilder;

    @Configured
    public DSRVJDABuilder(final @Val("token") String token, final @Val("prefix") String prefix,
                          final @Val("owner") String owner, final @Val("game.name") String gameName,
                          final @Val("game.type") int gameType) {
        super(AccountType.BOT);
        this.setToken(token);
        this.setGame(Game.of(Game.GameType.fromKey(gameType), gameName));
        commandClientBuilder = new CommandClientBuilder();
        commandClientBuilder.setPrefix(prefix);
        commandClientBuilder.setOwnerId(owner);
    }

    public DSRVJDABuilder addCommands(final @Nonnull Command... commands) {
        commandClientBuilder.addCommands(commands);
        return this;
    }

    @Override
    public JDA build() throws LoginException {
        this.addEventListener(new EventWaiter());
        this.addEventListener(this.commandClientBuilder.build());
        return super.build();
    }
}
