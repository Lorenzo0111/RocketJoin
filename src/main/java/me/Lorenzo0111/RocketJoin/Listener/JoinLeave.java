package me.Lorenzo0111.RocketJoin.Listener;

import me.Lorenzo0111.RocketJoin.CustomJoinMessage;
import me.Lorenzo0111.RocketJoin.Updater.UpdateChecker;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP;

public class JoinLeave implements Listener {

    public final CustomJoinMessage plugin;

    public JoinLeave(CustomJoinMessage plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        if (e.getPlayer().hasPermission("rocketjoin.vip")) {
            if (plugin.getConfig().getBoolean("enable_vip_features")) {
                if (plugin.getConfig().getBoolean("vip_leave")) {
                    String quitText = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("vip_leave_message").replace("{player}", p.getName()).replace("{DisplayPlayer}", p.getDisplayName()));
                    if (CustomJoinMessage.placeholderapi) {
                        quitText = PlaceholderAPI.setPlaceholders(p, quitText);
                    }
                    e.setQuitMessage(quitText);
                    return;
                }
            }
        }

        if (plugin.getConfig().getBoolean("enable_leave_message")) {
            String quitText = ChatColor.translateAlternateColorCodes('&',plugin.getConfig().getString("leave_message").replace("{player}", p.getName()).replace("{DisplayPlayer}", p.getDisplayName()));
            if (CustomJoinMessage.placeholderapi) {
                quitText = PlaceholderAPI.setPlaceholders(p, quitText);
            }
            e.setQuitMessage(quitText);
        } else {
            e.setQuitMessage(null);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();

        if (plugin.getConfig().getBoolean("display_title")) {
            p.sendTitle(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("join_title").replace("{player}", p.getName()).replace("{DisplayPlayer}", p.getDisplayName())), ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("join_subtitle").replace("{player}", p.getName()).replace("{DisplayPlayer}", p.getDisplayName())), 15, 40, 15);
        }

        if(!e.getPlayer().hasPlayedBefore()) {
            if (plugin.getConfig().getBoolean("enable_fist_join")) {
                String joinText = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("first_join").replace("{player}", p.getName()).replace("{DisplayPlayer}", p.getDisplayName()));
                if (CustomJoinMessage.placeholderapi) {
                    joinText = PlaceholderAPI.setPlaceholders(p, joinText);
                }
                e.setJoinMessage(joinText);
                return;
            }
        }

        if (e.getPlayer().hasPermission("rocketjoin.vip")) {
            if (plugin.getConfig().getBoolean("enable_vip_features")) {
                if (plugin.getConfig().getBoolean("vip_firework")) {
                    CustomJoinMessage.spawnFireworks(e.getPlayer().getLocation(), plugin.getConfig().getInt("vip_firework_to_spawn"));
                }
                if (plugin.getConfig().getBoolean("vip_sound")) {
                    for (Player xplayer : Bukkit.getOnlinePlayers()) {
                        xplayer.playSound(xplayer.getLocation(), ENTITY_EXPERIENCE_ORB_PICKUP, 60f, 1f);
                    }
                }
                if (plugin.getConfig().getBoolean("vip_join")) {
                    String joinText = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("vip_join_message").replace("{player}", p.getName()).replace("{DisplayPlayer}", p.getDisplayName()));
                    if (CustomJoinMessage.placeholderapi) {
                        joinText = PlaceholderAPI.setPlaceholders(p, joinText);
                    }
                    e.setJoinMessage(joinText);
                    return;
                }
            }
        }

        if (plugin.getConfig().getBoolean("enable_join_message")) {
            String joinText = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("join_message").replace("{player}", p.getName()).replace("{DisplayPlayer}", p.getDisplayName()));
            if (CustomJoinMessage.placeholderapi) {
                joinText = PlaceholderAPI.setPlaceholders(p, joinText);
            }
            e.setJoinMessage(joinText);
        } else {
            e.setJoinMessage(null);
        }

        if (e.getPlayer().hasPermission("rocketjoin.update")) {
            if (!plugin.getConfig().getBoolean("update-message")) {
                return;
            }
            UpdateChecker.playerUpdateCheck(p);
        }

    }

}
