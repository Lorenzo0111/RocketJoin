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
import me.lorenzo0111.rocketjoin.common.ChatUtils;
import me.lorenzo0111.rocketjoin.common.IConfiguration;
import me.lorenzo0111.rocketjoin.utilities.FireworkSpawner;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.util.generator.dummy.DummyObjectProvider;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.List;

public class JoinListener {
    private final RocketJoinSponge plugin;

    public JoinListener(RocketJoinSponge plugin) {
        this.plugin = plugin;
    }

    @Listener
    public void onJoin(ClientConnectionEvent.Join e) {
        Player p = e.getTargetEntity();

        this.handleMetrics(p);

        IConfiguration configuration = plugin.getConfig();
        String welcome = configuration.welcome();
        if (!welcome.equalsIgnoreCase("disable")) {
            p.sendMessage(Text.of(welcome));
        }

        this.handleUpdate(e);

        if (configuration.hide() && p.hasPermission(configuration.hidePermission())) {
            e.setMessageCancelled(true);
            return;
        }

        String condition = plugin.getHandler().getCondition(p);

        try {
            this.executeCommands(condition, p);
        } catch (SerializationException serializationException) {
            serializationException.printStackTrace();
        }

        if (condition == null) {
            boolean join = configuration.join().node("enabled").getBoolean();
            String message = configuration.join().node("message").getString();
            if (join && message != null) {
                e.setMessage(Text.of(message));
            }
            if (configuration.join().node("enable-title").getBoolean()) {
                Title title = Title.builder()
                        .title(plugin.parse(p,"join","title"))
                        .subtitle(plugin.parse(p,"join","subtitle"))
                        .fadeIn(15)
                        .stay(40)
                        .fadeOut(15)
                        .build();

                p.sendTitle(title);
            }
            return;
        }

        e.setMessage(Text.of(configuration.join(condition)));

        ConfigurationNode section = configuration.condition(condition);

        if (section.node("sound").getBoolean()) {
            SoundType type = DummyObjectProvider.createFor(SoundType.class, section.node("sound-type").getString("ENTITY_EXPERIENCE_ORB_PICKUP"));

            for (Player player : Sponge.getServer().getOnlinePlayers())
                player.playSound(type,player.getPosition(), 60f);
        }

        if (section.node("fireworks").getBoolean()) {
            FireworkSpawner.spawnFireworks(p.getLocation(), section.node("fireworks-amount").getInt(3));
        }
    }

    private void handleUpdate(ClientConnectionEvent.Join event) {
        if (event.getTargetEntity().hasPermission("rocketjoin.update") && plugin.getConfig().update()) {
            plugin.getUpdater().sendUpdateCheck(event.getTargetEntity());
        }
    }

    private void executeCommands(String condition, Player player) throws SerializationException {
        List<String> commands = condition == null ? plugin.getConfig().commands() : plugin.getConfig().commands(condition);

        for (String command : commands) {
            plugin.getGame().getCommandManager().process(plugin.getGame().getServer().getConsole(), command.replace("{player}", player.getName()));
        }
    }

    public void handleMetrics(Player player) {
        if (plugin.getConfig().get("already-asked").getBoolean(false)) {
            return;
        }

        LiteralText accept = Text.builder("[Allow]")
                .color(TextColors.GREEN)
                .onClick(TextActions.runCommand("/rocketjoin metrics allow"))
                .build();

        LiteralText deny = Text.builder("[Deny]")
                .color(TextColors.RED)
                .onClick(TextActions.runCommand("/rocketjoin metrics deny"))
                .build();

        Text text = Text.builder(
                ChatUtils.colorize("&8[&eMetrics&8] "))
                .append(Text.of("Thanks for installing " + plugin.getPlugin().getName() + " If you want to support my work please allow me to collect anonymous statistics from your server."))
                .color(TextColors.GRAY)
                .append(Text.of("\nThis will allow me to update the plugin and give you a better experience.", TextColors.GRAY))
                .color(TextColors.GRAY)
                .append(accept)
                .append(Text.of( " "))
                .append(deny)
                .build();

        player.sendMessage(text);
    }

}
