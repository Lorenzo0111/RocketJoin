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

import me.clip.placeholderapi.PlaceholderAPI;
import me.lorenzo0111.rocketjoin.spigot.RocketJoin;
import me.lorenzo0111.rocketjoin.spigot.utilities.PluginLoader;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LeaveListener implements Listener {

    private final RocketJoin plugin;
    private final PluginLoader loader;

    public LeaveListener(RocketJoin plugin, PluginLoader loader) {
        this.plugin = plugin;
        this.loader = loader;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        if (e.getPlayer().hasPermission("rocketjoin.vip") && plugin.getConfig().getBoolean("enable_vip_features") && plugin.getConfig().getBoolean("vip_leave")) {
            String quitText = JoinListener.translate(plugin.getConfig().getString("vip_leave_message", "").replace("{player}", p.getName()).replace("{DisplayPlayer}", p.getDisplayName()));
            if (loader.isPlaceholderapi()) {
                quitText = PlaceholderAPI.setPlaceholders(p, quitText);
            }
            e.setQuitMessage(quitText);
            return;
        }

        if (plugin.getConfig().getBoolean("enable_leave_message")) {
            String quitText = JoinListener.translate(plugin.getConfig().getString("leave_message", "").replace("{player}", p.getName()).replace("{DisplayPlayer}", p.getDisplayName()));
            if (loader.isPlaceholderapi()) {
                quitText = PlaceholderAPI.setPlaceholders(p, quitText);
            }
            e.setQuitMessage(quitText);
        } else {
            e.setQuitMessage(null);
        }
    }

}
