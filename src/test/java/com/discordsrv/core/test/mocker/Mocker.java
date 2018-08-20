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
package com.discordsrv.core.test.mocker;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.requests.restaction.MessageAction;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

/**
 * Mocks various interfaces within the JDA library.
 */
@SuppressWarnings("WeakerAccess")
@ParametersAreNonnullByDefault
public class Mocker {

    /**
     * Fetches an instance of a given interface with the given invocation handler.
     *
     * @param targetType
     *         The target interface to proxy.
     * @param handler
     *         The invocation handler to handle methods with.
     * @param <T>
     *         The type specified by the type argument.
     *
     * @return proxy A proxy instance for the specified interface.
     */
    public static <T> T getInstance(final Class<? extends T> targetType, final InvocationHandler handler) {
        return targetType.cast(Proxy.newProxyInstance(targetType.getClassLoader(), new Class[]{
            targetType
        }, handler));
    }

    /**
     * Mocks a JDA instance.
     *
     * @param textChannelFunction
     *         A Function for fetching TextChannels from.
     * @param userFunction
     *         A Function for fetching Users from.
     *
     * @return jda A mocked JDA instance.
     */
    public JDA getMockedJDA(final Function<String, ? extends TextChannel> textChannelFunction,
                            final Function<String, ? extends User> userFunction, final User user) {
        return getInstance(JDA.class, new NoopInvocationHandler() {
            @Override
            public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                if (method.equals(JDA.class.getMethod("getTextChannelById", String.class))) {
                    return textChannelFunction.apply((String) args[0]);
                } else if (method.equals(JDA.class.getMethod("getUserById", String.class))) {
                    return userFunction.apply((String) args[0]);
                } else if (method.equals(JDA.class.getMethod("getSelfUser"))) {
                    return user;
                } else {
                    return super.invoke(proxy, method, args);
                }
            }
        });
    }

    /**
     * Mocks a User instance.
     *
     * @param id
     *         The ID of this User.
     *
     * @return user A mocked User instance.
     */
    public SelfUser getMockedUser(final String id) {
        return getInstance(SelfUser.class, new NoopInvocationHandler() {
            @Override
            public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                if (method.equals(SelfUser.class.getMethod("getId"))) {
                    return id;
                } else if (method.equals(SelfUser.class.getMethod("isFake"))) {
                    return false;
                } else if (method.equals(Object.class.getMethod("equals", Object.class))) {
                    Object arg = args[0];
                    return arg instanceof User && ((User) arg).getId().equals(id);
                } else {
                    return super.invoke(proxy, method, args);
                }
            }
        });
    }

    /**
     * Mocks a Role instance.
     *
     * @param id
     *         The ID of this Role.
     *
     * @return user A mocked Role instance.
     */
    public Role getMockedRole(final String id) {
        return getInstance(Role.class, new NoopInvocationHandler() {
            @Override
            public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                if (method.equals(User.class.getMethod("getId"))) {
                    return id;
                } else {
                    return super.invoke(proxy, method, args);
                }
            }
        });
    }

    /**
     * Mocks a TextChannel instance (for use with simple tests).
     *
     * @param id
     *         The ID of this TextChannel.
     *
     * @return textChannel A mocked TextChannel instance.
     */
    public TextChannel getSimpleMockedTextChannel(final String id) {
        return getInstance(TextChannel.class, new NoopInvocationHandler() {
            @Override
            public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                if (method.equals(TextChannel.class.getMethod("getId"))) {
                    return id;
                } else {
                    return super.invoke(proxy, method, args);
                }
            }
        });
    }

    /**
     * Mocks a TextChannel interface (for use with mocked GuildMessageReceived events, as they scan upwards).
     *
     * @param id
     *         The ID of this TextChannel.
     * @param guild
     *         The guild in which this TextChannel exists.
     * @param messageFunction
     *         The message Function for "sending" messages with.
     *
     * @return textChannel A mocked TextChannel instance.
     */
    public TextChannel getMockedTextChannel(final String id, final Guild guild,
                                            final Function<CharSequence, ? extends MessageAction> messageFunction) {
        return getInstance(TextChannel.class, new NoopInvocationHandler() {
            @Override
            public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                if (method.equals(TextChannel.class.getMethod("getId"))) {
                    return id;
                } else if (method.equals(TextChannel.class.getMethod("getGuild"))) {
                    return guild;
                } else if (method.equals(TextChannel.class.getMethod("sendMessage", CharSequence.class))) {
                    return messageFunction.apply((CharSequence) args[0]);
                } else if (method.getName().equals("equals")) {
                    return args[0] instanceof TextChannel && Objects.equals(((TextChannel) args[0]).getId(), id);
                } else {
                    return super.invoke(proxy, method, args);
                }
            }
        });
    }

    /**
     * Mocks a Message instance.
     *
     * @param message
     *         The string message to provide with this Message.
     * @param messageId
     *         The message ID.
     * @param channel
     *         The TextChannel that this message came from.
     * @param user
     *         The User that authored this message.
     *
     * @return message A mocked message instance.
     */
    public Message getMockedMessage(final String message, final long messageId, final TextChannel channel,
                                    final User user) {
        return getInstance(Message.class, new NoopInvocationHandler() {
            @Override
            public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                if (method.equals(Message.class.getMethod("getContentRaw"))) {
                    return message;
                } else if (method.equals(Message.class.getMethod("getIdLong"))) {
                    return messageId;
                } else if (method.equals(Message.class.getMethod("getTextChannel"))) {
                    return channel;
                } else if (method.equals(Message.class.getMethod("getAuthor"))) {
                    return user;
                } else {
                    return super.invoke(proxy, method, args);
                }
            }
        });
    }

    /**
     * Mocks a Guild instance.
     *
     * @return guild A mocked Guild instance.
     */
    public Guild getMockedGuild() {
        return getInstance(Guild.class, new NoopInvocationHandler() {
        });
    }

    /**
     * Simple failfast invocation handler.
     *
     * @author vtcakavsmoace
     * @version 2.0
     */
    public abstract static class NoopInvocationHandler implements InvocationHandler {

        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            throw new UnsupportedOperationException(String
                .format("Unimplemented method: %s:%s(%s)", method.getDeclaringClass().getSimpleName(), method.getName(),
                    args == null || args.length == 0 ? ""
                        : Arrays.stream(args).map(Object::toString).reduce((s, s2) -> s + ", " + s2)));
        }

    }

}
