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

package me.lorenzo0111.rocketjoin.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.Player;
import me.lorenzo0111.rocketjoin.velocity.RocketJoinVelocity;
import me.lorenzo0111.rocketjoin.velocity.utilities.ChatUtils;
import me.lorenzo0111.rocketjoin.velocity.utilities.UpdateChecker;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.spongepowered.configurate.serialize.SerializationException;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class JoinListener {
    private final RocketJoinVelocity plugin;
    private final UpdateChecker updateChecker;

    public JoinListener(RocketJoinVelocity plugin) {
        this.plugin = plugin;
        this.updateChecker = plugin.getUpdater();
    }

    @Subscribe
    public void onJoin(PostLoginEvent e) {

        Player p = e.getPlayer();

        if (plugin.getConfig().node("enable-hide").getBoolean() && p.hasPermission(plugin.getConfig().node("hide-permission").getString("rocketjoin.silent"))) {
            return;
        }

        if (e.getPlayer().hasPermission("rocketjoin.update")) {
            if (!plugin.getConfig().node("update-message").getBoolean()) {
                return;
            }
            updateChecker.sendUpdateCheck(p);
        }

        try {
            this.executeCommands(e.getPlayer().hasPermission("rocketjoin.vip"), e.getPlayer());
        } catch (SerializationException serializationException) {
            serializationException.printStackTrace();
        }

        if (plugin.getConfig().node("display_title").getBoolean()) {
            final Title.Times times = Title.Times.of(Ticks.duration(15), Duration.ofMillis(3000), Ticks.duration(20));
            final Title title = Title.title(Component.text(ChatUtils.colorize(Objects.requireNonNull(plugin.getConfig().node("join_title").getString()).replace("{player}", p.getUsername()))), Component.text(ChatUtils.colorize(Objects.requireNonNull(plugin.getConfig().node("join_subtitle").getString()).replace("{player}", p.getUsername()))), times);

            p.showTitle(title);
        }

        if (e.getPlayer().hasPermission("rocketjoin.vip") && plugin.getConfig().node("enable_vip_features").getBoolean() && plugin.getConfig().node("vip_join").getBoolean()) {
            Component joinText = Component.text(ChatUtils.colorize(Objects.requireNonNull(plugin.getConfig().node("vip_join_message").getString()).replace("{player}", p.getUsername())));

            plugin.getServer().getScheduler().buildTask(plugin, () -> {
                for (Audience audience : plugin.getServer().getAllPlayers()) {
                    audience.sendMessage(joinText);
                }
            }).schedule();

            return;
        }

        if (plugin.getConfig().node("enable_join_message").getBoolean()) {
            Component joinText = Component.text(ChatUtils.colorize(Objects.requireNonNull(plugin.getConfig().node("join_message").getString()).replace("{player}", p.getUsername())));

            plugin.getServer().getScheduler().buildTask(plugin, () -> {
                for (Audience audience : plugin.getServer().getAllPlayers()) {
                    audience.sendMessage(joinText);
                }
            }).schedule();
        }
    }

    private void executeCommands(boolean vip, Player player) throws SerializationException {
        final List<String> commands = plugin.getConfig().node(vip ? "vip-commands" : "commands").getList(String.class);

        Objects.requireNonNull(commands);

        for (String command : commands) {
            plugin.getServer().getCommandManager().executeAsync(plugin.getServer().getConsoleCommandSource(), command.replace("{player}", player.getUsername()));
        }
    }

}
