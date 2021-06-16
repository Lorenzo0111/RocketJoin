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

package me.lorenzo0111.rocketjoin.bungeecord.listener;

import me.lorenzo0111.rocketjoin.bungeecord.RocketJoinBungee;
import me.lorenzo0111.rocketjoin.bungeecord.updater.UpdateChecker;
import me.lorenzo0111.rocketjoin.bungeecord.utilities.PluginLoader;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.List;

public class JoinListener implements Listener {
    private final RocketJoinBungee plugin;
    private final UpdateChecker updateChecker;

    public JoinListener(RocketJoinBungee plugin, PluginLoader loader) {
        this.plugin = plugin;
        this.updateChecker = loader.getUpdater();
    }

    @EventHandler
    public void onJoin(PostLoginEvent e) {

        ProxiedPlayer p = e.getPlayer();

        if (plugin.getConfiguration().node("enable-hide").getBoolean() && p.hasPermission(plugin.getConfiguration().node("hide-permission").getString()))
            return;

        try {
            this.executeCommands(e.getPlayer().hasPermission("rocketjoin.vip"), e.getPlayer());
        } catch (SerializationException serializationException) {
            serializationException.printStackTrace();
        }

        if (plugin.getConfiguration().node("display_title").getBoolean()) {
            p.sendTitle(plugin.getProxy().createTitle()
                    .title(new TextComponent(plugin.parse("join_title",e.getPlayer())))
                    .subTitle(new TextComponent(plugin.parse("join_subtitle",e.getPlayer())))
                    .fadeIn(15)
                    .stay(40)
                    .fadeOut(15));
        }

        if (e.getPlayer().hasPermission("rocketjoin.vip") && plugin.getConfiguration().node("enable_vip_features").getBoolean() && plugin.getConfiguration().node("vip_join").getBoolean()) {
            plugin.getProxy().broadcast(new TextComponent(plugin.parse("vip_join_message",e.getPlayer())));
            return;
        }

        if (plugin.getConfiguration().node("enable_join_message").getBoolean()) {
            plugin.getProxy().broadcast(new TextComponent(plugin.parse("join_message",e.getPlayer())));
        }

        if (e.getPlayer().hasPermission("rocketjoin.update") && plugin.getConfiguration().node("update-message").getBoolean()) {
            updateChecker.sendUpdateCheck(p);
        }
    }

    private void executeCommands(boolean vip, ProxiedPlayer player) throws SerializationException {
        final List<String> commands = plugin.getConfiguration().node(vip ? "vip-commands" : "commands").getList(String.class, new ArrayList<>());

        for (String command : commands) {
            plugin.getProxy().getPluginManager().dispatchCommand(plugin.getProxy().getConsole(), command.replace("{player}", player.getName()));
        }
    }

}
