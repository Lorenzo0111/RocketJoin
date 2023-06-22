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

import me.lorenzo0111.rocketjoin.RocketJoinSponge;
import me.lorenzo0111.rocketjoin.audience.WrappedPlayer;
import me.lorenzo0111.rocketjoin.common.config.ConditionConfiguration;
import me.lorenzo0111.rocketjoin.common.config.IConfiguration;
import me.lorenzo0111.rocketjoin.utilities.FireworkSpawner;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.title.Title;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;
import org.spongepowered.configurate.serialize.SerializationException;

import java.time.Duration;
import java.util.List;

public class JoinListener {
    private final RocketJoinSponge plugin;

    public JoinListener(RocketJoinSponge plugin) {
        this.plugin = plugin;
    }

    @Listener
    public void onJoin(ServerSideConnectionEvent.Join e) {
        ServerPlayer p = e.player();

        IConfiguration configuration = plugin.getConfig();
        String welcome = configuration.welcome();
        if (!welcome.equalsIgnoreCase("disable")) {
            p.sendMessage(plugin.parse(welcome,p));
        }

        this.handleUpdate(e);

        if (configuration.hide() && p.hasPermission(configuration.hidePermission())) {
            e.setMessageCancelled(true);
            return;
        }

        String condition = plugin.getHandler().getCondition(WrappedPlayer.wrap(p));

        try {
            this.executeCommands(condition, p);
        } catch (SerializationException | CommandException ex) {
            ex.printStackTrace();
        }

        if (condition == null) {
            boolean join = configuration.join().enabled();
            String message = configuration.join().message();
            if (join && message != null) {
                e.setMessage(plugin.parse(message, p));
            }
            if (configuration.join().enableTitle()) {
                Title title = Title.title(plugin.parse(configuration.join().title(),p),
                                plugin.parse(configuration.join().subTitle(),p), Title.Times.of(Duration.ofMillis(500), Duration.ofSeconds(2), Duration.ofMillis(500)));

                p.showTitle(title);
            }
            return;
        }

        e.setMessage(plugin.parse(configuration.join(condition),p));

        ConditionConfiguration section = configuration.condition(condition);

        if (section.sound()) {
            Sound sound = section.soundType();
            for (Player player : Sponge.server().onlinePlayers())
                player.playSound(sound);
        }

        if (section.fireworks()) {
            FireworkSpawner.spawnFireworks(p.location(), section.fireworksAmount());
        }
    }

    private void handleUpdate(ServerSideConnectionEvent.Join event) {
        if (event.player().hasPermission("rocketjoin.update") && plugin.getConfig().update()) {
            plugin.getUpdater().sendUpdateCheck(event.player());
        }
    }

    private void executeCommands(String condition, Player player) throws SerializationException, CommandException {
        List<String> commands = condition == null ? plugin.getConfig().commands() : plugin.getConfig().commands(condition);

        for (String command : commands) {
            plugin.getGame().server().commandManager().process(plugin.getGame().systemSubject(), command.replace("{player}", player.name()));
        }
    }

}
