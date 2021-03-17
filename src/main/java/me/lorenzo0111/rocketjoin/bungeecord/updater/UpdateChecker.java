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

package me.lorenzo0111.rocketjoin.bungeecord.updater;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class UpdateChecker {

    /*

    Plugin by Lorenzo0111 - https://github.com/Lorenzo0111

     */

    private boolean updateAvailable;
    private final Plugin plugin;
    private final int resourceId;

    public UpdateChecker(Plugin plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;

        this.fetch();
    }

    public void fetch() {
        plugin.getProxy().getScheduler().runAsync(plugin, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    String version = scanner.next();

                    this.updateAvailable = !this.plugin.getDescription().getVersion().equalsIgnoreCase(version);
                }
            } catch (IOException exception) {
                this.plugin.getLogger().info("Cannot look for updates: " + exception.getMessage());
            }
        });
    }

    public void sendUpdateCheck(CommandSender player) {
        if (updateAvailable) {
            player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&e&l&m---------------------------------------")));
            player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&c&lRocket&e&lJoin &f&l» &7There is a new update available.")));
            player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&c&lRocket&e&lJoin &f&l» &7Download it from: &ehttps://bit.ly/RocketJoin")));
            player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&e&l&m---------------------------------------")));
        }
    }

    public void updateCheck() {
        if (updateAvailable) {
            this.plugin.getLogger().info("There is a new update available. Download it from: https://bit.ly/RocketJoin");
        }
    }
}
