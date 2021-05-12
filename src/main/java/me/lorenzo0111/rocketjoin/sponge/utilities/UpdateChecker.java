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

package me.lorenzo0111.rocketjoin.sponge.utilities;

import me.lorenzo0111.rocketjoin.sponge.RocketJoinSponge;
import me.lorenzo0111.rocketjoin.velocity.utilities.ChatUtils;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class UpdateChecker {

    private boolean updateAvailable;
    private final RocketJoinSponge plugin;
    private final int resourceId;
    private final String url;

    public UpdateChecker(RocketJoinSponge plugin, int resourceId, String url) {
        this.plugin = plugin;
        this.resourceId = resourceId;
        this.url = url;

        if (this.plugin.getVersion().endsWith("-SNAPSHOT") || this.plugin.getVersion().endsWith("-BETA")) {
            this.plugin.getLogger().info("Running a SNAPSHOT or BETA version, the updater may be bugged here.");
        }

        this.fetch();
    }

    public CompletableFuture<Boolean> fetch() {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();

        plugin.getGame().getScheduler().createTaskBuilder()
                .async()
                .execute(() -> {
                        try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream(); Scanner scanner = new Scanner(inputStream)) {
                            if (scanner.hasNext()) {
                                String version = scanner.next();

                                this.updateAvailable = !this.plugin.getVersion().equalsIgnoreCase(version);
                            }

                            completableFuture.complete(this.updateAvailable);
                        } catch (IOException exception) {
                            this.plugin.getLogger().info("Cannot look for updates: " + exception.getMessage());
                        }
                })
                .submit(plugin);

        return completableFuture;
    }

    public void sendUpdateCheck(CommandSource player) {
        this.sendUpdateCheck(player,this.updateAvailable);
    }

    public void sendUpdateCheck(CommandSource player, boolean available) {
        if (available) {
            player.sendMessage(Text.of(ChatUtils.colorize(String.format("&8[&eRocketUpdater&8] &7An update of %s is available. Download it from %s",plugin.getPlugin().getName(),url))));
        }
    }
}