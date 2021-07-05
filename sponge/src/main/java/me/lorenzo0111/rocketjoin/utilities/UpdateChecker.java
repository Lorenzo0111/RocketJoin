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

package me.lorenzo0111.rocketjoin.utilities;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.lorenzo0111.rocketjoin.RocketJoinSponge;
import me.lorenzo0111.rocketjoin.common.ChatUtils;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class UpdateChecker {

    private boolean updateAvailable;
    private final RocketJoinSponge plugin;
    private final String resourceId;
    private final String url;

    public UpdateChecker(RocketJoinSponge plugin, String resourceId, String url) {
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
                        try {
                            HttpsURLConnection connection = (HttpsURLConnection) new URL("https://ore.spongepowered.org/api/v1/projects/" + this.resourceId).openConnection();
                            connection.setRequestMethod("GET");
                            connection.setRequestProperty("Content-Type", "application/json; utf-8");
                            connection.setRequestProperty("Accept", "application/json");
                            connection.setRequestProperty("User-Agent", "RocketPlugins Update Checker");

                            BufferedReader br = new BufferedReader(
                                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));

                            StringBuilder response = new StringBuilder();
                            String responseLine;
                            while ((responseLine = br.readLine()) != null) {
                                response.append(responseLine.trim());
                            }

                            final JsonObject json = (JsonObject) JsonParser.parseString(response.toString());
                            String version = json.get("recommended").getAsJsonObject().get("name").getAsString();

                            this.updateAvailable = !this.plugin.getVersion().equalsIgnoreCase(version);
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