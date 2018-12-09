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
package com.discordsrv.integration.platform;

import com.discordsrv.core.api.channel.Chat;
import com.discordsrv.core.api.dsrv.platform.Platform;
import com.discordsrv.core.api.dsrv.platform.extension.Extension;
import com.discordsrv.core.channel.LocalChatChannelLinker;
import com.discordsrv.core.channel.MalleableChatChannelLookup;
import com.discordsrv.core.conf.Configuration;
import com.discordsrv.core.dsrv.plugin.extension.ExtensionClassLoader;
import com.discordsrv.core.test.minecraft.TestConsole;
import com.discordsrv.integration.Minecraft;
import com.discordsrv.integration.platform.chat.ChatWrapper;
import com.google.common.util.concurrent.FutureCallback;
import lombok.Value;
import net.dv8tion.jda.core.entities.Channel;
import org.apache.commons.collections4.bidimap.DualLinkedHashBidiMap;
import org.apache.commons.collections4.bidimap.DualTreeBidiMap;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

@Value
public class Plugin implements Platform<IntegrationDSRVContext>, MinecraftPlugin {

    IntegrationDSRVContext context;

    public Plugin() throws IOException {
        this.context = new IntegrationDSRVContext();
        Yaml yaml = new Yaml();
        this.context.setConfiguration(Configuration.getStandardConfiguration(yaml));
        MalleableChatChannelLookup<IntegrationDSRVContext> lookup =
            new MalleableChatChannelLookup<IntegrationDSRVContext>(context) {
                @Override
                public void getKnownChats(@Nonnull final FutureCallback<Stream<Chat>> callback) {
                    callback.onSuccess(Stream.empty());
                }

                @Override
                public void getKnownChannels(@Nonnull final FutureCallback<Stream<Channel>> callback) {
                    callback.onSuccess(Stream.empty());
                }
            };
        lookup.addChatTranslator((id, callback) -> {
            try {
                callback.onSuccess(
                    context.getMinecraft().getChats().stream().filter(chat -> chat.getName().equals(id)).findAny()
                        .map(ChatWrapper::new).orElse(null));
            } catch (Throwable t) {
                callback.onFailure(t);
            }
        });
        this.context.setChatChannelLookup(lookup);
        this.context.setChatChannelLinker(
            new LocalChatChannelLinker(new DualTreeBidiMap<>(), this.context.getChatChannelLookup(), new TestConsole(),
                "console-channel"));
    }

    @Override
    public void onEnable() {
        ExtensionClassLoader<IntegrationDSRVContext, Plugin> loader =
            new ExtensionClassLoader<>(this.getClass().getClassLoader());
        try {
            loader.addURL(
                new File("extension/build/libs/extension-TEST.jar").toURI().toURL()); // assuming from :integration
            Extension<IntegrationDSRVContext, Plugin> extension = loader.getExtensions().findAny().orElse(null);
            if (extension == null) {
                throw new IllegalArgumentException("Extension did not exist.");
            }
            extension.setPlugin(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setMinecraft(final Minecraft minecraft) {
        this.context.setMinecraft(minecraft);
    }
}
