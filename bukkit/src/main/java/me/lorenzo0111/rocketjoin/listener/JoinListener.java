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
import me.lorenzo0111.rocketjoin.RocketJoinBukkit;
import me.lorenzo0111.rocketjoin.audience.WrappedPlayer;
import me.lorenzo0111.rocketjoin.common.ChatUtils;
import me.lorenzo0111.rocketjoin.common.config.ConditionConfiguration;
import me.lorenzo0111.rocketjoin.common.config.IConfiguration;
import me.lorenzo0111.rocketjoin.utilities.FireworkSpawner;
import me.lorenzo0111.rocketjoin.utilities.PluginLoader;
import me.lorenzo0111.rocketjoin.utilities.VanishUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.serialize.SerializationException;

import java.time.Duration;
import java.util.List;

public class JoinListener implements Listener {
    private final RocketJoinBukkit plugin;
    private final FireworkSpawner fireworkSpawner = new FireworkSpawner();
    private final UpdateChecker updateChecker;

    public JoinListener(RocketJoinBukkit plugin, @NotNull PluginLoader loader) {
        this.plugin = plugin;
        this.updateChecker = loader.getUpdater();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(@NotNull PlayerJoinEvent e) {

        Player p = e.getPlayer();

        IConfiguration configuration = plugin.getConfiguration();
        String welcome = configuration.welcome();
        if (!welcome.equalsIgnoreCase("disable")) {
            BukkitAudienceManager.audience(e.getPlayer()).sendMessage(plugin.parse(welcome, p));
        }

        e.setJoinMessage(null);

        if (VanishUtils.isVanished(p)) {
            return;
        }

        this.handleUpdate(e);

        if (configuration.hide() && p.hasPermission(configuration.hidePermission())) {
            return;
        }

        String condition = plugin.getHandler().getCondition(WrappedPlayer.wrap(p));

        try {
            this.executeCommands(condition, e.getPlayer());
        } catch (SerializationException serializationException) {
            serializationException.printStackTrace();
        }

        if (condition == null) {
            boolean join = configuration.join().enabled();
            if (join) {
                e.setJoinMessage(
                        ChatColor.translateAlternateColorCodes('&',
                                ChatUtils.serializer().serialize(plugin.parse(configuration.join().message(), p)).replace("&", "§")
                        ));
            }
            if (configuration.join().enableTitle()) {
                Audience audience = BukkitAudienceManager.audience(p);
                audience.showTitle(Title.title(plugin.parse(configuration.join().title(),p),
                        plugin.parse(configuration.join().subTitle(),p),
                        Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(2), Duration.ofMillis(500))));
            }
            return;
        }

        e.setJoinMessage(
                ChatColor.translateAlternateColorCodes('&',
                        ChatUtils.serializer().serialize(plugin.parse(configuration.join(condition),p)))
        );

        ConditionConfiguration section = configuration.condition(condition);

        if (section.sound()) {
            Sound sound = section.soundType();

            for (Player player : Bukkit.getOnlinePlayers())
                BukkitAudienceManager.audience(player).playSound(sound);
        }

        if (section.fireworks()) {
            fireworkSpawner.spawnFireworks(p.getLocation(), section.fireworksAmount());
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
