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
import me.lorenzo0111.rocketjoin.bungeecord.updater.UpdateChecker;
import me.lorenzo0111.rocketjoin.bungeecord.utilities.PluginLoader;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class JoinListener implements Listener {

    private final RocketJoinBungee plugin;
    private final UpdateChecker updateChecker;

    public JoinListener(RocketJoinBungee plugin, PluginLoader loader) {
        this.plugin = plugin;
        this.updateChecker = loader.getUpdater();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PostLoginEvent e) {

        ProxiedPlayer p = e.getPlayer();

        if (plugin.getConfig().getBoolean("display_title")) {
            p.sendTitle(plugin.getProxy().createTitle()
                    .title(new TextComponent(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("join_title").replace("{player}", p.getName()).replace("{DisplayPlayer}", p.getDisplayName()))))
                    .subTitle(new TextComponent(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("join_subtitle").replace("{player}", p.getName()).replace("{DisplayPlayer}", p.getDisplayName()))))
                    .fadeIn(15)
                    .stay(40)
                    .fadeOut(15));
        }

        if (e.getPlayer().hasPermission("rocketjoin.vip")) {
            if (plugin.getConfig().getBoolean("enable_vip_features")) {
                if (plugin.getConfig().getBoolean("vip_join")) {
                    String joinText = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("vip_join_message").replace("{player}", p.getName()).replace("{DisplayPlayer}", p.getDisplayName()));
                    plugin.getProxy().broadcast(new TextComponent(joinText));
                    return;
                }
            }
        }

        if (plugin.getConfig().getBoolean("enable_join_message")) {
            String joinText = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("join_message").replace("{player}", p.getName()).replace("{DisplayPlayer}", p.getDisplayName()));
            plugin.getProxy().broadcast(new TextComponent(joinText));
        }

        if (e.getPlayer().hasPermission("rocketjoin.update")) {
            if (!plugin.getConfig().getBoolean("update-message")) {
                return;
            }
            updateChecker.sendUpdateCheck(p);
        }

    }

}
