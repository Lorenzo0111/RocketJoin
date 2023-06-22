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

package me.lorenzo0111.rocketjoin;

import me.clip.placeholderapi.PlaceholderAPI;
import me.lorenzo0111.pluginslib.audience.BukkitAudienceManager;
import me.lorenzo0111.rocketjoin.common.ChatUtils;
import me.lorenzo0111.rocketjoin.common.ConfigExtractor;
import me.lorenzo0111.rocketjoin.common.RocketJoin;
import me.lorenzo0111.rocketjoin.common.conditions.ConditionHandler;
import me.lorenzo0111.rocketjoin.common.config.IConfiguration;
import me.lorenzo0111.rocketjoin.common.config.file.FileConfiguration;
import me.lorenzo0111.rocketjoin.common.exception.LoadException;
import me.lorenzo0111.rocketjoin.common.utils.IScheduler;
import me.lorenzo0111.rocketjoin.utilities.PluginLoader;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class RocketJoinBukkit extends JavaPlugin implements RocketJoin {
    private IScheduler scheduler;
    private IConfiguration config;
    private PluginLoader loader;
    private ConditionHandler handler;

    @Override
    public void onEnable() {
        File file = new ConfigExtractor(this.getClass(),this.getDataFolder(), "config.yml")
                .extract();

        this.scheduler = new IScheduler() {
            @Override
            public void async(Runnable runnable) {
                Bukkit.getScheduler().runTaskAsynchronously(RocketJoinBukkit.this, runnable);
            }

            @Override
            public void sync(Runnable runnable) {
                Bukkit.getScheduler().runTask(RocketJoinBukkit.this, runnable);
            }
        };
        this.config = new FileConfiguration(file);
        try {
            this.handler = new ConditionHandler(config);
        } catch (LoadException e) {
            this.getLogger().severe(e.getMessage());
            this.setEnabled(false);
            return;
        }

        this.reloadConfig();

        BukkitAudienceManager.init(this);

        // Load the plugin
        this.loader = new PluginLoader(this);
        this.loader.loadMetrics();
        this.loader.placeholderHook();
        this.loader.registerEvents();
    }

    public void onDisable() {
        BukkitAudienceManager.shutdown();
    }


    @Override
    public IScheduler getScheduler() {
        return scheduler;
    }

    @Override
    public IConfiguration getConfiguration() {
        return config;
    }

    @Override
    public String getVersion() {
        return this.getDescription().getVersion();
    }

    @Override
    public void reloadConfig() {
        config.reload();
        try {
            handler.init();
        } catch (LoadException e) {
            this.getLogger().severe(e.getMessage());
        }

        if (config.version() == null) {
            this.getLogger().severe("You are using an old configuration, consider deleting the config.yml and restarting the server. If you need help you can also join in our support server.");
        }
    }

    public Component parse(@Nullable String string) {
        return ChatUtils.colorize(string);
    }

    public Component parse(@Nullable String string, Player player) {
        if (string == null) return null;
        String str = string.replace("{player}", player.getName()).replace("{DisplayPlayer}", player.getDisplayName());
        if (loader.isPlaceholderapi()) {
            str = PlaceholderAPI.setPlaceholders(player,str);
        }

        return ChatUtils.colorize(str);
    }

    public static RocketJoinBukkit instance() {
        return RocketJoinBukkit.getPlugin(RocketJoinBukkit.class);
    }

    public ConditionHandler getHandler() {
        return handler;
    }

    public String getPrefix() {
        return this.getConfiguration().prefix();
    }
}