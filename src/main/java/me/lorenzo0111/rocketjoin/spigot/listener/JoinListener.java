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
import me.lorenzo0111.rocketjoin.spigot.updater.UpdateChecker;
import me.lorenzo0111.rocketjoin.spigot.utilities.FireworkSpawner;
import me.lorenzo0111.rocketjoin.spigot.utilities.PluginLoader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;
import java.util.Objects;

import static org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP;

public class JoinListener implements Listener {

    private final RocketJoin plugin;
    private final PluginLoader loader;
    private final FireworkSpawner fireworkSpawner = new FireworkSpawner();
    private final UpdateChecker updateChecker;

    public JoinListener(RocketJoin plugin, PluginLoader loader) {
        this.plugin = plugin;
        this.loader = loader;
        this.updateChecker = loader.getUpdater();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();

        if (this.handleFirstJoin(e)) {
            return;
        }

        this.handleUpdate(e);

        this.executeCommands(e.getPlayer().hasPermission("rocketjoin.vip"), e.getPlayer());

        if (plugin.getConfig().getBoolean("display_title")) {
            p.sendTitle(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("join_title")).replace("{player}", p.getName()).replace("{DisplayPlayer}", p.getDisplayName())), ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("join_subtitle")).replace("{player}", p.getName()).replace("{DisplayPlayer}", p.getDisplayName())), 15, 40, 15);
        }

        if (e.getPlayer().hasPermission("rocketjoin.vip") && plugin.getConfig().getBoolean("enable_vip_features") && this.handleVipEvent(e,p)) {
            return;
        }

        if (plugin.getConfig().getBoolean("enable_join_message")) {
            String joinText = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("join_message")).replace("{player}", p.getName()).replace("{DisplayPlayer}", p.getDisplayName()));
            if (loader.isPlaceholderapi()) {
                joinText = PlaceholderAPI.setPlaceholders(p, joinText);
            }
            e.setJoinMessage(joinText);
            return;
        }

        e.setJoinMessage(null);
    }

    private boolean handleFirstJoin(PlayerJoinEvent event) {
        if(!event.getPlayer().hasPlayedBefore() && plugin.getConfig().getBoolean("enable_fist_join")) {
            String joinText = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("first_join")).replace("{player}", event.getPlayer().getName()).replace("{DisplayPlayer}", event.getPlayer().getDisplayName()));
            if (loader.isPlaceholderapi()) {
                joinText = PlaceholderAPI.setPlaceholders(event.getPlayer(), joinText);
            }
            event.setJoinMessage(joinText);
            return true;
        }
        return false;
    }

    private void handleUpdate(PlayerJoinEvent event) {
        if (event.getPlayer().hasPermission("rocketjoin.update")) {
            if (!plugin.getConfig().getBoolean("update-message")) {
                return;
            }
            updateChecker.sendUpdateCheck(event.getPlayer());
        }
    }

    private boolean handleVipEvent(PlayerJoinEvent event, Player player) {
        if (plugin.getConfig().getBoolean("vip_firework")) {
            fireworkSpawner.spawnFireworks(player.getLocation(), plugin.getConfig().getInt("vip_firework_to_spawn"));
        }
        if (plugin.getConfig().getBoolean("vip_sound")) {
            for (Player xplayer : Bukkit.getOnlinePlayers()) {
                xplayer.playSound(xplayer.getLocation(), ENTITY_EXPERIENCE_ORB_PICKUP, 60f, 1f);
            }
        }
        if (plugin.getConfig().getBoolean("vip_join")) {
            String joinText = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("vip_join_message")).replace("{player}", player.getName()).replace("{DisplayPlayer}", player.getDisplayName()));
            if (loader.isPlaceholderapi()) {
                joinText = PlaceholderAPI.setPlaceholders(player, joinText);
            }
            event.setJoinMessage(joinText);
            return true;
        }

        return false;
    }

    private void executeCommands(boolean vip, Player player) {
        final List<String> commands = plugin.getConfig().getStringList(vip ? "vip-commands" : "commands");

        for (String command : commands) {
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.replace("{player}", player.getName()));
        }
    }

}
