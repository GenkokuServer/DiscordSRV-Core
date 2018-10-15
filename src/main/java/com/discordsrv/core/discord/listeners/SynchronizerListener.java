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
package com.discordsrv.core.discord.listeners;

import com.discordsrv.core.api.channel.Chat;
import com.discordsrv.core.api.channel.ChatChannelLinker;
import com.discordsrv.core.api.channel.ChatMessage;
import com.discordsrv.core.api.common.unit.Named;
import com.discordsrv.core.api.minecraft.Console;
import com.discordsrv.core.api.role.Team;
import com.discordsrv.core.api.role.TeamRoleLinker;
import com.discordsrv.core.api.user.MinecraftPlayer;
import com.discordsrv.core.api.user.PlayerUserLinker;
import com.discordsrv.core.conf.annotation.Configured;
import com.discordsrv.core.conf.annotation.Val;
import com.google.common.util.concurrent.FutureCallback;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.role.RoleDeleteEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * SynchronizerListener type, for synchronizing between Minecraft and Discord.
 */
public class SynchronizerListener extends ListenerAdapter {

    private final PlayerUserLinker playerUserLinker;
    private final ChatChannelLinker chatChannelLinker;
    private final TeamRoleLinker teamRoleLinker;
    private final List<UnaryOperator<String>> filters;
    private final String format;
    private final Logger logger;

    /**
     * Main configured constructor for the SynchronizedListener type.
     *
     * @param playerUserLinker
     *         The linker for players and users.
     * @param chatChannelLinker
     *         The linker for chats and channels.
     * @param teamRoleLinker
     *         The linker for teams and roles.
     * @param filters
     *         The filters to be applied to Discord messages.
     * @param format
     *         The format of the messages from Discord to Minecraft.
     */
    @Configured
    public SynchronizerListener(final @Val("playerUserLinker") PlayerUserLinker playerUserLinker,
                                final @Val("chatChannelLinker") ChatChannelLinker chatChannelLinker,
                                final @Val("teamRoleLinker") TeamRoleLinker teamRoleLinker,
                                final @Val("filters") List<UnaryOperator<String>> filters,
                                final @Val("message_format") String format) {
        this.playerUserLinker = playerUserLinker;
        this.chatChannelLinker = chatChannelLinker;
        this.teamRoleLinker = teamRoleLinker;
        this.filters = filters;
        this.format = format;
        this.logger = LoggerFactory.getLogger(String.format("DSRV: %s", this.getClass().getSimpleName()));
    }

    @Override
    public void onGuildMessageReceived(final GuildMessageReceivedEvent event) {
        if (event.getAuthor().isFake() || event.getJDA().getSelfUser().equals(event.getAuthor())) {
            return;
        }
        if (event.getChannel().getId().equals(chatChannelLinker.getConsoleChannelId())) {
            chatChannelLinker.getConsole(new FutureCallback<Console>() {
                @Override
                public void onSuccess(@Nullable final Console result) {
                    if (result != null) {
                        result.invoke(event.getMessage().getContentRaw());
                    }
                }

                @Override
                public void onFailure(final Throwable t) {
                    logger.warn("Exception encountered while attempting to lookup console.", t);
                }
            });
        } else {
            chatChannelLinker.translate(event.getChannel(), new FutureCallback<Chat>() {
                @Override
                public void onSuccess(@Nullable final Chat result) {
                    if (result != null) {
                        onChatFound(result, event);
                    }
                }

                @Override
                public void onFailure(final Throwable t) {
                    logger.warn("Encountered exception while performing channel translation.", t);
                }
            });
        }
    }

    @Override
    public void onGuildMemberRoleAdd(final GuildMemberRoleAddEvent event) {
        updateRole(event.getUser(), event.getMember().getRoles());
    }

    @Override
    public void onGuildMemberRoleRemove(final GuildMemberRoleRemoveEvent event) {
        updateRole(event.getUser(), event.getMember().getRoles());
    }

    @Override
    public void onRoleDelete(final RoleDeleteEvent event) {
        super.onRoleDelete(event);
        event.getGuild().getMembersWithRoles(event.getRole())
            .forEach(member -> updateRole(member.getUser(), member.getRoles()));
    }

    private void onChatFound(final Chat chat, final GuildMessageReceivedEvent event) {
        playerUserLinker.translate(event.getAuthor(), new FutureCallback<MinecraftPlayer>() {
            @Override
            public void onSuccess(@Nullable final MinecraftPlayer result) {
                AtomicReference<String> filtered = new AtomicReference<>(event.getMessage().getContentRaw());
                filters.forEach(filtered::updateAndGet);
                if (result != null) {
                    result.getName(name -> sendMessage(chat, name,
                        format.replace("%name%", name).replace("%message%", filtered.get())));
                } else {
                    sendMessage(chat, event.getMember().getEffectiveName(), format
                        .replace("%name%", event.getMember().getEffectiveName().replace("%message%", filtered.get())));
                }
            }

            @Override
            public void onFailure(final Throwable t) {
                logger.warn("Encountered exception while performing user translation.", t);
            }
        });
    }

    private void sendMessage(final Chat chat, final CharSequence senderName, final String message) {
        chat.sendMessage(new ChatMessage<Long>() {
            @Override
            public Named getSender() {
                return (callback) -> callback.accept(senderName);
            }

            @Override
            public String getMessage() {
                return message;
            }

            @Override
            public void getUniqueIdentifier(@Nullable final Consumer<Long> callback) {
                if (callback != null) {
                    callback.accept(TimeUnit.NANOSECONDS.convert(System.nanoTime(), TimeUnit.MILLISECONDS));
                }
            }
        }, new FutureCallback<Void>() {
            @Override
            public void onSuccess(@Nullable final Void result) {
                // hooray?
            }

            @Override
            public void onFailure(final Throwable t) {
                logger.warn("Exception encountered while relaying message.", t);
            }
        });
    }

    private void updateRole(final User user, final List<Role> roles) {
        AtomicInteger count = new AtomicInteger(roles.size());
        ConcurrentSkipListMap<Role, Team<MinecraftPlayer>> map = new ConcurrentSkipListMap<>();
        roles.forEach(role -> teamRoleLinker.translate(role, new FutureCallback<Team<MinecraftPlayer>>() {
            @Override
            public void onSuccess(@Nullable final Team<MinecraftPlayer> result) {
                if (result != null) {
                    map.put(role, result);
                }
                if (count.decrementAndGet() == 0) {
                    updateTeam(user, map);
                }
            }

            @Override
            public void onFailure(final Throwable t) {
                logger.warn("Exception encountered while performing team lookup.", t);
                if (count.decrementAndGet() == 0) {
                    updateTeam(user, map);
                }
            }
        }));
    }

    private void updateTeam(User user, ConcurrentSkipListMap<Role, Team<MinecraftPlayer>> roleMap) {
        playerUserLinker.translate(user, new FutureCallback<MinecraftPlayer>() {
            @Override
            public void onSuccess(@Nullable final MinecraftPlayer result) {
                if (!roleMap.isEmpty() && result != null) {
                    updateTeam(result, roleMap.lastEntry().getValue());
                }
            }

            @Override
            public void onFailure(final Throwable t) {
                logger.warn("Exception encountered while performing player lookup.", t);
            }
        });
    }

    private void updateTeam(MinecraftPlayer player, Team<MinecraftPlayer> team) {
        team.addMember(player, new FutureCallback<Void>() {
            @Override
            public void onSuccess(@Nullable final Void result) {
                // hooray?
            }

            @Override
            public void onFailure(final Throwable t) {
                logger.warn("Exception encountered while performing player team modification.", t);
            }
        });
    }
}
