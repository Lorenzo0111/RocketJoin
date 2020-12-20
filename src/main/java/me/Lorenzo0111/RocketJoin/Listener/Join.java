package me.Lorenzo0111.RocketJoin.Listener;

import me.Lorenzo0111.RocketJoin.CustomJoinMessage;
import me.Lorenzo0111.RocketJoin.Updater.UpdateChecker;
import me.Lorenzo0111.RocketJoin.Utilities.FireworkSpawner;
import me.Lorenzo0111.RocketJoin.Utilities.PluginLoader;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP;

public class Join implements Listener {

    private final CustomJoinMessage plugin;
    private final PluginLoader loader;
    private final FireworkSpawner fireworkSpawner = new FireworkSpawner();
    private final UpdateChecker updateChecker;

    public Join(CustomJoinMessage plugin, PluginLoader loader) {
        this.plugin = plugin;
        this.loader = loader;
        this.updateChecker = new UpdateChecker(this.plugin, 82520);
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
                if (loader.placeholderapi) {
                    joinText = PlaceholderAPI.setPlaceholders(p, joinText);
                }
                e.setJoinMessage(joinText);
                return;
            }
        }

        if (e.getPlayer().hasPermission("rocketjoin.vip")) {
            if (plugin.getConfig().getBoolean("enable_vip_features")) {
                if (plugin.getConfig().getBoolean("vip_firework")) {
                    fireworkSpawner.spawnFireworks(e.getPlayer().getLocation(), plugin.getConfig().getInt("vip_firework_to_spawn"));
                }
                if (plugin.getConfig().getBoolean("vip_sound")) {
                    for (Player xplayer : Bukkit.getOnlinePlayers()) {
                        xplayer.playSound(xplayer.getLocation(), ENTITY_EXPERIENCE_ORB_PICKUP, 60f, 1f);
                    }
                }
                if (plugin.getConfig().getBoolean("vip_join")) {
                    String joinText = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("vip_join_message").replace("{player}", p.getName()).replace("{DisplayPlayer}", p.getDisplayName()));
                    if (loader.placeholderapi) {
                        joinText = PlaceholderAPI.setPlaceholders(p, joinText);
                    }
                    e.setJoinMessage(joinText);
                    return;
                }
            }
        }

        if (plugin.getConfig().getBoolean("enable_join_message")) {
            String joinText = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("join_message").replace("{player}", p.getName()).replace("{DisplayPlayer}", p.getDisplayName()));
            if (loader.placeholderapi) {
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
            updateChecker.playerUpdateCheck(p);
        }

    }

}
