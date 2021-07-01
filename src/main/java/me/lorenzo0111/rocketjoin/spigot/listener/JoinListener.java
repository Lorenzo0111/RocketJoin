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
import me.lorenzo0111.rocketjoin.spigot.updater.UpdateChecker;
import me.lorenzo0111.rocketjoin.spigot.utilities.FireworkSpawner;
import me.lorenzo0111.rocketjoin.spigot.utilities.PluginLoader;
import me.lorenzo0111.rocketjoin.spigot.utilities.VanishUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP;

public class JoinListener implements Listener {
    private final RocketJoin plugin;
    private final FireworkSpawner fireworkSpawner = new FireworkSpawner();
    private final UpdateChecker updateChecker;

    public JoinListener(RocketJoin plugin, PluginLoader loader) {
        this.plugin = plugin;
        this.updateChecker = loader.getUpdater();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();

        String welcome = plugin.getConfiguration().node("welcome").getString("disable");
        if (!welcome.equalsIgnoreCase("disable")) {
            p.sendMessage(plugin.parse("welcome", p));
        }

        if (VanishUtils.isVanished(p)) {
            return;
        }

        if (this.handleFirstJoin(e)) {
            return;
        }

        this.handleUpdate(e);

        if (plugin.getConfiguration().node("enable-hide").getBoolean() && p.hasPermission(plugin.getConfiguration().node("hide-permission").getString("rocketjoin.silent"))) {
            e.setJoinMessage(null);
            return;
        }

        try {
            this.executeCommands(e.getPlayer().hasPermission("rocketjoin.vip"), e.getPlayer());
        } catch (SerializationException serializationException) {
            serializationException.printStackTrace();
        }

        if (plugin.getConfiguration().node("display_title").getBoolean()) {
            p.sendTitle(plugin.parse("join_title",p), plugin.parse("join_subtitle",p), 15, 40, 15);
        }

        if (e.getPlayer().hasPermission("rocketjoin.vip") && plugin.getConfiguration().node("enable_vip_features").getBoolean() && this.handleVipEvent(e,p)) {
            return;
        }

        if (plugin.getConfiguration().node("enable_join_message").getBoolean()) {
            String joinText = plugin.parse("join_message",p);
            e.setJoinMessage(joinText);
            return;
        }

        e.setJoinMessage(null);
    }

    private boolean handleFirstJoin(PlayerJoinEvent event) {
        if(!event.getPlayer().hasPlayedBefore() && plugin.getConfiguration().node("enable_fist_join").getBoolean()) {
            event.setJoinMessage(plugin.parse("first_join",event.getPlayer()));
            return true;
        }
        return false;
    }

    private void handleUpdate(PlayerJoinEvent event) {
        if (event.getPlayer().hasPermission("rocketjoin.update")) {
            if (!plugin.getConfiguration().node("update-message").getBoolean()) {
                return;
            }
            updateChecker.sendUpdateCheck(event.getPlayer());
        }
    }

    private boolean handleVipEvent(PlayerJoinEvent event, Player player) {
        if (plugin.getConfiguration().node("vip_firework").getBoolean()) {
            fireworkSpawner.spawnFireworks(player.getLocation(), plugin.getConfiguration().node("vip_firework_to_spawn").getInt());
        }
        if (plugin.getConfiguration().node("vip_sound").getBoolean()) {
            for (Player xplayer : Bukkit.getOnlinePlayers()) {
                xplayer.playSound(xplayer.getLocation(), ENTITY_EXPERIENCE_ORB_PICKUP, 60f, 1f);
            }
        }
        if (plugin.getConfiguration().node("vip_join").getBoolean()) {
            String joinText = plugin.parse("vip_join_message", event.getPlayer());
            event.setJoinMessage(joinText);
            return true;
        }

        return false;
    }

    private void executeCommands(boolean vip, Player player) throws SerializationException {
        final List<String> commands = plugin.getConfiguration().node(vip ? "vip-commands" : "commands").getList(String.class,new ArrayList<>());

        for (String command : commands) {
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.replace("{player}", player.getName()));
        }
    }

    public static String translateHexColorCodes(final String message) {
        final Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
        final char colorChar = ChatColor.COLOR_CHAR;

        final Matcher matcher = hexPattern.matcher(message);
        final StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);

        while (matcher.find()) {
            final String group = matcher.group(1);

            matcher.appendReplacement(buffer, colorChar + "x"
                    + colorChar + group.charAt(0) + colorChar + group.charAt(1)
                    + colorChar + group.charAt(2) + colorChar + group.charAt(3)
                    + colorChar + group.charAt(4) + colorChar + group.charAt(5));
        }

        return matcher.appendTail(buffer).toString();
    }

    public static boolean isCompatible() {
        String[] split = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
        String minorVer = split[1];
        return Integer.parseInt(minorVer) >= 16;
    }

}
