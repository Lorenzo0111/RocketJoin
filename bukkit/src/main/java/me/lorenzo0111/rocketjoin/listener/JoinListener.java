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

package me.lorenzo0111.rocketjoin.listener;

import me.lorenzo0111.pluginslib.audience.BukkitAudienceManager;
import me.lorenzo0111.pluginslib.updater.UpdateChecker;
import me.lorenzo0111.rocketjoin.RocketJoin;
import me.lorenzo0111.rocketjoin.common.IConfiguration;
import me.lorenzo0111.rocketjoin.utilities.FireworkSpawner;
import me.lorenzo0111.rocketjoin.utilities.PluginLoader;
import me.lorenzo0111.rocketjoin.utilities.VanishUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.List;

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

        IConfiguration configuration = plugin.getConfiguration();
        String welcome = configuration.welcome();
        if (!welcome.equalsIgnoreCase("disable")) {
            p.sendMessage(plugin.parse("welcome", p));
        }

        if (VanishUtils.isVanished(p)) {
            e.setJoinMessage(null);
            return;
        }

        this.handleUpdate(e);

        if (configuration.hide() && p.hasPermission(configuration.hidePermission())) {
            e.setJoinMessage(null);
            return;
        }

        String condition = plugin.getHandler().getCondition(p);

        try {
            this.executeCommands(condition, e.getPlayer());
        } catch (SerializationException serializationException) {
            serializationException.printStackTrace();
        }

        if (condition == null) {
            boolean join = configuration.join().node("enabled").getBoolean();
            if (join) {
                e.setJoinMessage(plugin.parse(p,"join","message"));
            }
            if (configuration.join().node("enable-title").getBoolean()) {
                p.sendTitle(plugin.parse(p,"join","title"), plugin.parse(p,"join","subtitle"), 15, 40, 15);
            }
            return;
        }

        e.setJoinMessage(configuration.join(condition));

        ConfigurationNode section = configuration.condition(condition);

        if (section.node("sound").getBoolean()) {
            Sound sound = Sound.valueOf(section.node("sound-type").getString("ENTITY_EXPERIENCE_ORB_PICKUP"));

            for (Player player : Bukkit.getOnlinePlayers())
                player.playSound(player.getLocation(), sound, 60f, 1f);
        }

        if (section.node("fireworks").getBoolean()) {
            fireworkSpawner.spawnFireworks(p.getLocation(), section.node("fireworks-amount").getInt(3));
        }
    }

    private void handleUpdate(PlayerJoinEvent event) {
        if (event.getPlayer().hasPermission("rocketjoin.update")) {
            if (!plugin.getConfiguration().update()) {
                return;
            }
            updateChecker.sendUpdateCheck(BukkitAudienceManager.audience(event.getPlayer()));
        }
    }

    private void executeCommands(String condition, Player player) throws SerializationException {
        List<String> commands = condition == null ? plugin.getConfiguration().commands() : plugin.getConfiguration().commands(condition);

        for (String command : commands) {
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.replace("{player}", player.getName()));
        }
    }

}
