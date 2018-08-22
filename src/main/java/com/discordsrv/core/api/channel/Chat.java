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
package com.discordsrv.core.api.channel;

import com.discordsrv.core.api.common.unit.Messageable;
import com.discordsrv.core.api.common.unit.Named;
import com.discordsrv.core.api.common.unit.Translatable;
import net.dv8tion.jda.core.entities.TextChannel;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Chat type, for representing Minecraft chats.
 */
@ThreadSafe
public interface Chat extends Translatable<String, TextChannel>, Named, Messageable<ChatMessage<Long>, Void> {

}
