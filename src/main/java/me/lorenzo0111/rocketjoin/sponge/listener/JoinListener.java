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

package me.lorenzo0111.rocketjoin.sponge.listener;

import me.lorenzo0111.rocketjoin.sponge.RocketJoinSponge;
import me.lorenzo0111.rocketjoin.sponge.utilities.FireworkSpawner;
import me.lorenzo0111.rocketjoin.velocity.utilities.ChatUtils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.spongepowered.api.effect.sound.SoundTypes.ENTITY_EXPERIENCE_ORB_PICKUP;

public class JoinListener {
    private final RocketJoinSponge plugin;

    public JoinListener(RocketJoinSponge plugin) {
        this.plugin = plugin;
    }

    @Listener
    public void onJoin(ClientConnectionEvent.Join e) {
        final Player player = e.getTargetEntity();

        if (player.hasPermission("rocketjoin.command")) {
            this.handleMetrics(player);
        }

        this.handleUpdate(e);

        if (plugin.getConfig().node("enable-hide").getBoolean() && player.hasPermission(plugin.getConfig().node("hide-permission").getString("rocketjoin.silent"))) {
            e.setMessageCancelled(true);
            return;
        }

        if (this.handleFirstJoin(e)) {
            return;
        }

        this.executeCommands(player.hasPermission("rocketjoin.vip"), player);

        if (plugin.getConfig().node("display_title").getBoolean()) {
            Title title = Title.builder()
                    .title(Text.of(ChatUtils.colorize(Objects.requireNonNull(Objects.requireNonNull(plugin.getConfig().node("join_title")).getString()).replace("{player}", player.getName()))))
                    .subtitle(Text.of(ChatUtils.colorize(Objects.requireNonNull(Objects.requireNonNull(plugin.getConfig().node("join_subtitle")).getString()).replace("{player}", player.getName()))))
                    .fadeIn(15)
                    .stay(40)
                    .fadeOut(15)
                    .build();

            player.sendTitle(title);
        }

        if (player.hasPermission("rocketjoin.vip") && plugin.getConfig().node("enable_vip_features").getBoolean() && this.handleVipEvent(e,player)) {
            return;
        }

        if (plugin.getConfig().node("enable_join_message").getBoolean()) {
            String joinText = ChatUtils.colorize(Objects.requireNonNull(plugin.getConfig().node("join_message").getString()).replace("{player}", player.getName()));
            e.setMessage(Text.of(joinText));
            return;
        }

        e.setMessageCancelled(true);
    }

    private boolean handleFirstJoin(ClientConnectionEvent.Join event) {
        if(!event.getTargetEntity().hasPlayedBefore() && plugin.getConfig().node("enable_fist_join").getBoolean()) {
            String joinText = ChatUtils.colorize(Objects.requireNonNull(plugin.getConfig().node("first_join").getString()).replace("{player}", event.getTargetEntity().getName()));
            event.setMessage(Text.of(joinText));
            return true;
        }
        return false;
    }

    private void handleUpdate(ClientConnectionEvent.Join event) {
        if (event.getTargetEntity().hasPermission("rocketjoin.update")) {
            if (!plugin.getConfig().node("update-message").getBoolean()) {
                return;
            }
            plugin.getUpdater().sendUpdateCheck(event.getTargetEntity());
        }
    }

    private boolean handleVipEvent(ClientConnectionEvent.Join event, Player player) {
        if (plugin.getConfig().node("vip_firework").getBoolean()) {
            FireworkSpawner.spawnFireworks(player.getLocation(), plugin.getConfig().node("vip_firework_to_spawn").getInt());
        }
        if (plugin.getConfig().node("vip_sound").getBoolean()) {
            for (Player xplayer : plugin.getGame().getServer().getOnlinePlayers()) {
                xplayer.playSound(ENTITY_EXPERIENCE_ORB_PICKUP,xplayer.getLocation().getPosition(), 60f, 1f);
            }
        }
        if (plugin.getConfig().node("vip_join").getBoolean()) {
            String joinText = ChatUtils.colorize(Objects.requireNonNull(plugin.getConfig().node("vip_join_message").getString()).replace("{player}", player.getName()));
            event.setMessage(Text.of(joinText));
            return true;
        }

        return false;
    }

    private void executeCommands(boolean vip, Player player) {
        List<String> commands = new ArrayList<>();

        try {
            commands = plugin.getConfig().node(vip ? "vip-commands" : "commands").getList(String.class);
        } catch (SerializationException e) {
            e.printStackTrace();
        }

        Objects.requireNonNull(commands);

        for (String command : commands) {
            plugin.getGame().getCommandManager().process(plugin.getGame().getServer().getConsole(), command.replace("{player}", player.getName()));
        }
    }

    public void handleMetrics(Player player) {
        if (plugin.getConfig().node("already-asked").getBoolean(false)) {
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
