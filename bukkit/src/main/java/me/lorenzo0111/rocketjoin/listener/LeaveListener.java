/*
 *  This file is part of RocketJoin, licensed under the MIT License.
 *
 *  Copyright (c) Lorenzo0111
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.lorenzo0111.rocketjoin.listener;

import me.lorenzo0111.rocketjoin.RocketJoinBukkit;
import me.lorenzo0111.rocketjoin.audience.WrappedPlayer;
import me.lorenzo0111.rocketjoin.common.ChatUtils;
import me.lorenzo0111.rocketjoin.utilities.VanishUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LeaveListener implements Listener {

    private final RocketJoinBukkit plugin;

    public LeaveListener(RocketJoinBukkit plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        e.setQuitMessage(null);

        if (VanishUtils.isVanished(p)) {
            return;
        }

        if (plugin.getConfiguration().hide() && p.hasPermission(plugin.getConfiguration().hidePermission())) {
            return;
        }

        String condition = plugin.getHandler().getCondition(WrappedPlayer.wrap(p));
        if (condition == null) {
            if (plugin.getConfiguration().leave().enabled())
                e.setQuitMessage(ChatUtils.serializer().serialize(plugin.parse(plugin.getConfiguration().leave().message(),p)));
            return;
        }

        e.setQuitMessage(ChatUtils.serializer().serialize(plugin.parse(plugin.getConfiguration().leave(condition),p)));
    }

}
