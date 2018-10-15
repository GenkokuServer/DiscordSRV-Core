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
package com.discordsrv.core.discord.commands;

import com.discordsrv.core.api.user.MinecraftPlayer;
import com.discordsrv.core.api.user.PlayerUserLinker;
import com.discordsrv.core.auth.PlayerUserAuthenticator;
import com.discordsrv.core.conf.annotation.Configured;
import com.discordsrv.core.conf.annotation.Val;
import com.google.common.util.concurrent.FutureCallback;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.tuple.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;

/**
 * AuthenticateCommand type, for authenticating users.
 */
public class AuthenticateCommand extends Command {

    private final String failedAuth;
    private final String successfulAuth;
    private final String alreadyAuth;
    private final String badConfig;
    private PlayerUserAuthenticator authenticator;
    private PlayerUserLinker linker;

    /**
     * Main configured constructor for the AuthenticateCommand type.
     *
     * @param name
     *         The name of the command.
     * @param aliases
     *         Aliases of the command.
     * @param arguments
     *         Argument help for the command.
     * @param help
     *         Help message for the command.
     * @param failedAuth
     *         Message to send when an auth attempt fails.
     * @param successfulAuth
     *         Message to send when an auth attempt succeeds.
     * @param alreadyAuth
     *         Message to send when a user requesting authentication already has an authentication entry.
     * @param badConfig
     *         Message to send if the server configuration is incorrect (i.e. authenticators or linkers are not
     *         provided).
     */
    @Configured
    public AuthenticateCommand(final @Val("name") String name, final @Val("aliases") ArrayList<String> aliases,
                               final @Val("arguments") String arguments, final @Val("help") String help,
                               final @Val("cooldown") int cooldown, final @Val("failed_auth") String failedAuth,
                               final @Val("successful_auth") String successfulAuth,
                               final @Val("already_auth") String alreadyAuth,
                               final @Val("bad_config") String badConfig) {
        this.name = name;
        this.aliases = aliases.toArray(new String[0]);
        this.arguments = arguments;
        this.botPermissions = new Permission[]{
            Permission.MESSAGE_READ,
            Permission.MESSAGE_WRITE
        };
        this.cooldown = cooldown;
        this.cooldownScope = CooldownScope.USER;
        this.help = help;
        this.failedAuth = failedAuth;
        this.successfulAuth = successfulAuth;
        this.alreadyAuth = alreadyAuth;
        this.badConfig = badConfig;
    }

    @Override
    protected void execute(final CommandEvent event) {
        try {
            linker.translate(event.getAuthor(), new FutureCallback<MinecraftPlayer>() {
                @Override
                public void onSuccess(@Nullable final MinecraftPlayer result) {
                    if (result != null) {
                        result.getName(name -> {
                            event.reactWarning();
                            event.reply(alreadyAuth.replace("%author%", event.getAuthor().getAsMention())
                                .replace("%player%", name));
                        });
                    } else {
                        authenticator.attemptVerify(event.getAuthor(), event.getArgs(),
                            new FutureCallback<Pair<MinecraftPlayer, User>>() {
                                @Override
                                public void onSuccess(@Nullable final Pair<MinecraftPlayer, User> result) {
                                    if (result != null) {
                                        result.getLeft().getName(name -> {
                                            event.reactSuccess();
                                            event.reply(
                                                successfulAuth.replace("%author%", result.getRight().getAsMention())
                                                    .replace("%player%", name));
                                        });
                                    } else {
                                        event.reactError();
                                        event.reply(badConfig.replace("%owner%",
                                            event.getJDA().getUserById(event.getClient().getOwnerId()).getAsMention()));
                                    }
                                }

                                @Override
                                public void onFailure(final Throwable t) {
                                    event.reactWarning();
                                    event.reply(failedAuth.replace("%token%", event.getArgs()));
                                }
                            });
                    }
                }

                @Override
                public void onFailure(final Throwable t) {
                    event.reactError();
                    event.reply(badConfig
                        .replace("%owner%", event.getJDA().getUserById(event.getClient().getOwnerId()).getAsMention()));
                }
            });
        } catch (Throwable t) {
            event.reactError();
            event.reply(badConfig
                .replace("%owner%", event.getJDA().getUserById(event.getClient().getOwnerId()).getAsMention()));
        }
    }

    public void setAuthenticator(final PlayerUserAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    public void setLinker(final PlayerUserLinker linker) {
        this.linker = linker;
    }

}
