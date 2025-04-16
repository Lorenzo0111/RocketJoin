package me.lorenzo0111.rocketjoin.utilities;

import me.lorenzo0111.rocketjoin.RocketJoinBungee;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

public class BungeeUtilities {

    public static void broadcast(@NotNull RocketJoinBungee plugin, String message, ProxiedPlayer p) {
        Component text = plugin.parse(message, p);

        ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> {
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                plugin.sendMessage(player, text);
            }
        });
    }

    public static void broadcast(@NotNull RocketJoinBungee plugin, Component text, ProxiedPlayer p) {
        ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> {
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                plugin.sendMessage(player, text);
            }
        });
    }

    public static void broadcastFor(@NotNull RocketJoinBungee plugin, String server, Component text) {
        ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> {
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                if (player.getServer().getInfo().getName().equalsIgnoreCase(server))
                    plugin.sendMessage(player, text);
            }
        });
    }
}
