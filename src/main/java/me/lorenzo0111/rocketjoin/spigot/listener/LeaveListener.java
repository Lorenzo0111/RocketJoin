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

package me.lorenzo0111.rocketjoin.spigot.listener;

import me.lorenzo0111.rocketjoin.spigot.RocketJoin;
import me.lorenzo0111.rocketjoin.spigot.utilities.PluginLoader;
import me.lorenzo0111.rocketjoin.spigot.utilities.VanishUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LeaveListener implements Listener {

    private final RocketJoin plugin;

    public LeaveListener(RocketJoin plugin, PluginLoader loader) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        if (VanishUtils.isVanished(p)) {
            return;
        }

        if (plugin.getConfiguration().node("enable-hide").getBoolean() && p.hasPermission(plugin.getConfiguration().node("hide-permission").getString(""))) {
            e.setQuitMessage(null);
            return;
        }

        if (e.getPlayer().hasPermission("rocketjoin.vip") && plugin.getConfiguration().node("enable_vip_features").getBoolean() && plugin.getConfiguration().node("vip_leave").getBoolean()) {
            e.setQuitMessage(plugin.parse("vip_leave_message",e.getPlayer()));
            return;
        }

        if (plugin.getConfiguration().node("enable_leave_message").getBoolean()) {
            e.setQuitMessage(plugin.parse("leave_message",e.getPlayer()));
        } else {
            e.setQuitMessage(null);
        }
    }

}
