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
package com.discordsrv.core.api.common.callback;

import com.google.common.util.concurrent.FutureCallback;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * MultiCallbackWrapper type, for merging responses of multiple callback-based methods into a single callback.
 *
 * @param <T>
 *         The type of this callback.
 */
@ParametersAreNonnullByDefault
public class MultiCallbackWrapper<T> implements FutureCallback<T>, Runnable {

    private final List<Consumer<FutureCallback<T>>> callbackConsumers;
    private final AtomicInteger callbacksRemaining;
    private final FutureCallback<T> callback;
    /**
     * The results encountered while processing (as passed by {@link FutureCallback#onSuccess(Object)}.
     */
    protected final LinkedList<T> results;
    /**
     * The errors encountered while processing (as passed by {@link FutureCallback#onFailure(Throwable)}.
     */
    protected final LinkedList<Throwable> errors;

    /**
     * Main constructor for the MultiCallbackWrapper type.
     *
     * @param callbackConsumers
     *         Consumers of callbacks to invoke with this callback wrapper.
     * @param callback
     *         The callback to invoke upon completion.
     */
    public MultiCallbackWrapper(final List<Consumer<FutureCallback<T>>> callbackConsumers, final FutureCallback<T> callback) {
        this.callbackConsumers = callbackConsumers;
        this.callbacksRemaining = new AtomicInteger(callbackConsumers.size());
        this.callback = callback;
        results = new LinkedList<>();
        errors = new LinkedList<>();
    }

    @Override
    public final void run() {
        callbackConsumers.forEach(consumer -> consumer.accept(this));
    }

    @Override
    public final void onSuccess(@Nullable final T result) {
        results.add(result);
        if (callbacksRemaining.decrementAndGet() == 0) {
            onComplete();
        }
    }

    @Override
    public final void onFailure(final Throwable t) {
        errors.add(t);
        if (callbacksRemaining.decrementAndGet() == 0) {
            onComplete();
        }
    }

    /**
     * Method to invoke upon completion.
     */
    protected void onComplete() {
        if (this.errors.size() > 0) {
            Throwable throwable = new Throwable();
            this.errors.forEach(throwable::addSuppressed);
            callback.onFailure(throwable);
        } else {
            callback.onSuccess(this.results.stream().filter(Objects::nonNull).findAny().orElse(null));
        }
    }
}
