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
package com.discordsrv.core.discord.commands;

import com.discordsrv.core.api.user.MinecraftPlayer;
import com.discordsrv.core.api.user.PlayerUserLookup;
import com.discordsrv.core.conf.annotation.Configured;
import com.discordsrv.core.conf.annotation.Val;
import com.google.common.util.concurrent.FutureCallback;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.Permission;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * PlayerlistCommand type, for listing the online players.
 */
public class PlayerlistCommand extends Command {

    private final String listHeader;
    private final String listItem;
    private final String badConfig;
    private PlayerUserLookup lookup;

    /**
     * Main configured constructor for the PlayerlistCommand type.
     *
     * @param name
     *         The name of the playerlist command.
     * @param aliases
     *         The aliases of the playerlist command.
     * @param arguments
     *         The arguments of the playerlist command.
     * @param help
     *         The help string of the playerlist command.
     * @param listHeader
     *         The header of the list result.
     * @param listItem
     *         The string for item formatting.
     * @param badConfig
     *         Message to send if the server configuration is incorrect (i.e. authenticators or linkers are not
     *         provided).
     */
    @Configured
    public PlayerlistCommand(final @Val("name") String name, final @Val("aliases") ArrayList<String> aliases,
                             final @Val("arguments") String arguments, final @Val("help") String help,
                             final @Val("list_header") String listHeader, final @Val("list_item") String listItem,
                             final @Val("bad_config") String badConfig) {
        this.name = name;
        this.aliases = aliases.toArray(new String[0]);
        this.arguments = arguments;
        this.botPermissions = new Permission[]{
            Permission.MESSAGE_READ,
            Permission.MESSAGE_WRITE
        };
        this.help = help;
        this.listHeader = listHeader;
        this.listItem = listItem;
        this.badConfig = badConfig;
    }

    @Override
    protected void execute(final CommandEvent event) {
        lookup.getOnline(new FutureCallback<Stream<MinecraftPlayer>>() {
            @Override
            public void onSuccess(@Nullable final Stream<MinecraftPlayer> result) {
                if (result != null) {
                    event.reactSuccess();
                    List<MinecraftPlayer> collected = result.collect(Collectors.toList());
                    StringBuffer builder =
                        new StringBuffer(listHeader.replace("%count%", Integer.toString(collected.size())))
                            .append('\n');
                    collected.forEach(player -> player
                        .getName(name -> builder.append(String.format("%s\n", listItem.replace("%player%", name)))));
                    event.reply(builder.toString());
                } else {
                    event.reactError();
                    event.reply(badConfig
                        .replace("%owner%", event.getJDA().getUserById(event.getClient().getOwnerId()).getAsMention()));
                }
            }

            @Override
            public void onFailure(final @Nonnull Throwable t) {
                event.reactError();
                event.reply(badConfig
                    .replace("%owner%", event.getJDA().getUserById(event.getClient().getOwnerId()).getAsMention()));
            }
        });
    }

    public void setLookup(final PlayerUserLookup lookup) {
        this.lookup = lookup;
    }
}
