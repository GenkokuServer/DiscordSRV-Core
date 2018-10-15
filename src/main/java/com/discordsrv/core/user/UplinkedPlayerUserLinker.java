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
package com.discordsrv.core.user;

import com.discordsrv.core.api.user.MinecraftPlayer;
import com.discordsrv.core.api.user.PlayerUserLinker;
import com.discordsrv.core.api.user.PlayerUserLookup;
import com.discordsrv.core.conf.annotation.Configured;
import com.discordsrv.core.conf.annotation.Val;
import com.google.common.util.concurrent.FutureCallback;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.IOUtil;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualLinkedHashBidiMap;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

/**
 * Leverages link.discordsrv.com to perform lookups of player/user links.
 */
@ParametersAreNonnullByDefault
public class UplinkedPlayerUserLinker implements PlayerUserLinker {

    private final BidiMap<UUID, String> playerCache = new DualLinkedHashBidiMap<>();
    private final PlayerUserLookup lookup;

    /**
     * Main constructor for the UplinkedPlayerUserLinker type.
     *
     * @param lookup
     *         The lookup service.
     */
    @Configured
    public UplinkedPlayerUserLinker(final @Val("lookup") PlayerUserLookup lookup) {
        this.lookup = lookup;
    }

    @Override
    public void translate(final MinecraftPlayer player, final FutureCallback<User> callback) {
        player.getUniqueIdentifier(uuid -> {
            String result = playerCache.get(uuid);
            if (result == null) {
                try {
                    result = lookup(uuid);
                    if (result != null) {
                        lookup.lookupUser(result, callback);
                        return;
                    }
                } catch (Throwable t) {
                    callback.onFailure(t);
                    return;
                }
            } else {
                lookup.lookupUser(result, callback);
                return;
            }
            callback.onSuccess(null);
        });
    }

    @Override
    public void translate(final User user, final FutureCallback<MinecraftPlayer> callback) {
        UUID result = playerCache.getKey(user.getId());
        if (result == null) {
            try {
                result = lookup(user.getId());
                if (result != null) {
                    lookup.lookupPlayer(result, callback);
                    return;
                }
            } catch (Throwable t) {
                callback.onFailure(t);
                return;
            }
        } else {
            lookup.lookupPlayer(result, callback);
            return;
        }
        callback.onSuccess(null);
    }

    /**
     * Uncaches a player from the local cache. This is necessary for player rejoins (in case the player has unlinked
     * their account).
     *
     * @param player
     *         The player to uncache.
     */
    public void uncache(final MinecraftPlayer player) {
        player.getUniqueIdentifier(playerCache::remove);
    }

    private String lookup(UUID uuid) throws IOException {
        URL url = new URL("http://link.discordsrv.com/lookup?" + uuid);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try {
                byte[] read = IOUtil.readFully(con.getInputStream());
                String resp = new String(read).trim();
                playerCache.put(uuid, resp);
                return resp;
            } catch (Throwable t) {
                return null;
            }
        } else {
            return null;
        }
    }

    private UUID lookup(String snowflake) throws IOException {
        URL url = new URL("http://link.discordsrv.com/lookup?" + snowflake);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try {
                byte[] read = IOUtil.readFully(con.getInputStream());
                UUID resp = UUID.fromString(new String(read).trim());
                playerCache.put(resp, snowflake);
                return resp;
            } catch (Throwable t) {
                return null;
            }
        } else {
            return null;
        }
    }

}
