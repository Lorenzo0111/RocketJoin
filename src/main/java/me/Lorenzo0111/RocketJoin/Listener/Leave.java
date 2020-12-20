package me.Lorenzo0111.RocketJoin.Listener;

import me.Lorenzo0111.RocketJoin.CustomJoinMessage;
import me.Lorenzo0111.RocketJoin.Utilities.PluginLoader;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class Leave implements Listener {

    private final CustomJoinMessage plugin;
    private final PluginLoader loader;

    public Leave(CustomJoinMessage plugin, PluginLoader loader) {
        this.plugin = plugin;
        this.loader = loader;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        if (e.getPlayer().hasPermission("rocketjoin.vip")) {
            if (plugin.getConfig().getBoolean("enable_vip_features")) {
                if (plugin.getConfig().getBoolean("vip_leave")) {
                    String quitText = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("vip_leave_message").replace("{player}", p.getName()).replace("{DisplayPlayer}", p.getDisplayName()));
                    if (loader.placeholderapi) {
                        quitText = PlaceholderAPI.setPlaceholders(p, quitText);
                    }
                    e.setQuitMessage(quitText);
                    return;
                }
            }
        }

        if (plugin.getConfig().getBoolean("enable_leave_message")) {
            String quitText = ChatColor.translateAlternateColorCodes('&',plugin.getConfig().getString("leave_message").replace("{player}", p.getName()).replace("{DisplayPlayer}", p.getDisplayName()));
            if (loader.placeholderapi) {
                quitText = PlaceholderAPI.setPlaceholders(p, quitText);
            }
            e.setQuitMessage(quitText);
        } else {
            e.setQuitMessage(null);
        }
    }

}
