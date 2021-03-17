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

package me.lorenzo0111.rocketjoin.bungeecord.listener;

import me.lorenzo0111.rocketjoin.bungeecord.RocketJoinBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class LeaveListener implements Listener {

    private final RocketJoinBungee plugin;

    public LeaveListener(RocketJoinBungee plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent e) {
        ProxiedPlayer p = e.getPlayer();

        if (e.getPlayer().hasPermission("rocketjoin.vip")) {
            if (plugin.getConfig().getBoolean("enable_vip_features")) {
                if (plugin.getConfig().getBoolean("vip_leave")) {
                    String quitText = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("vip_leave_message").replace("{player}", p.getName()).replace("{DisplayPlayer}", p.getDisplayName()));
                    plugin.getProxy().broadcast(new TextComponent(quitText));
                    return;
                }
            }
        }

        if (plugin.getConfig().getBoolean("enable_leave_message")) {
            String quitText = ChatColor.translateAlternateColorCodes('&',plugin.getConfig().getString("leave_message").replace("{player}", p.getName()).replace("{DisplayPlayer}", p.getDisplayName()));
            plugin.getProxy().broadcast(new TextComponent(quitText));
        }
    }

}
