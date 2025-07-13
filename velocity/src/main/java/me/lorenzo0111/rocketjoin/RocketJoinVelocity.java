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

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import me.lorenzo0111.rocketjoin.command.RocketJoinVelocityCommand;
import me.lorenzo0111.rocketjoin.common.ChatUtils;
import me.lorenzo0111.rocketjoin.common.ConfigExtractor;
import me.lorenzo0111.rocketjoin.common.RocketJoin;
import me.lorenzo0111.rocketjoin.common.conditions.ConditionHandler;
import me.lorenzo0111.rocketjoin.common.config.IConfiguration;
import me.lorenzo0111.rocketjoin.common.config.file.FileConfiguration;
import me.lorenzo0111.rocketjoin.common.database.PlayersDatabase;
import me.lorenzo0111.rocketjoin.common.exception.LoadException;
import me.lorenzo0111.rocketjoin.common.platform.hooks.PlaceholderProxyHook;
import me.lorenzo0111.rocketjoin.common.utils.IScheduler;
import me.lorenzo0111.rocketjoin.listener.JoinListener;
import me.lorenzo0111.rocketjoin.listener.LeaveListener;
import me.lorenzo0111.rocketjoin.listener.SwitchListener;
import net.kyori.adventure.text.Component;
import org.bstats.charts.SimplePie;
import org.bstats.velocity.Metrics;
import org.jetbrains.annotations.Nullable;
import org.sayandev.sayanvanish.api.SayanVanishAPI;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Plugin(id = "rocketjoin", name = "RocketJoin", version = "@version@",
        description = "Custom Join Messages Plugin", authors = {"Lorenzo0111"},
        dependencies = {
                @Dependency(id = "papiproxybridge", optional = true),
                @Dependency(id = "sayanvanish", optional = true)
        })
public class RocketJoinVelocity implements RocketJoin {
    private final Logger logger;
    private final Path path;
    private final ProxyServer server;
    private final Metrics.Factory metricsFactory;

    private IScheduler scheduler;
    private IConfiguration config;
    private ConditionHandler handler;

    @Inject
    public RocketJoinVelocity(Logger logger, @DataDirectory Path path, ProxyServer server, Metrics.Factory metricsFactory) {
        this.logger = logger;
        this.path = path;
        this.server = server;
        this.metricsFactory = metricsFactory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        this.scheduler = new IScheduler() {
            @Override
            public void async(Runnable runnable) {
                getServer().getScheduler().buildTask(RocketJoinVelocity.this, runnable).schedule();
            }

            @Override
            public void sync(Runnable runnable) {
                runnable.run();
            }
        };

        File conf = new ConfigExtractor(this.getClass(), path.toFile(), "config.yml")
                .extract();

        try {
            PlayersDatabase.init(path.toFile());
        } catch (LoadException e) {
            this.getLogger().error(e.getMessage());
            return;
        }

        Objects.requireNonNull(conf);

        this.config = new FileConfiguration(conf);
        try {
            this.handler = new ConditionHandler(config);
        } catch (LoadException e) {
            this.getLogger().error(e.getMessage());
            return;
        }

        this.reloadConfig();

        server.getEventManager().register(this, new JoinListener(this));
        server.getEventManager().register(this, new LeaveListener(this));
        server.getEventManager().register(this, new SwitchListener(this));

        Metrics metrics = metricsFactory.make(this, 11318);
        metrics.addCustomChart(new SimplePie("conditions", () -> String.valueOf(config.conditions().childrenList().size())));

        CommandMeta meta = server.getCommandManager()
                .metaBuilder("rocketjoinvelocity")
                .aliases("rjv")
                .build();

        server.getCommandManager().register(meta, new RocketJoinVelocityCommand(this));

        logger.info("RocketJoin loaded!");
    }

    public Logger getLogger() {
        return logger;
    }

    public ProxyServer getServer() {
        return server;
    }

    public IConfiguration getConfig() {
        return config;
    }

    public String getVersion() {
        return "@version@";
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
    public boolean isVanished(UUID player) {
        return server.getPluginManager().isLoaded("sayanvanish") &&
                SayanVanishAPI.getInstance().isVanished(player);
    }

    public CompletableFuture<Component> parse(@Nullable String string, Player player) {
        String str = string == null ? "" : string.replace("{player}", player.getUsername());

        if (server.getPluginManager().isLoaded("papiproxybridge"))
            return PlaceholderProxyHook.replacePlaceholders(str, player.getUniqueId())
                    .thenApply(ChatUtils::colorize);

        return CompletableFuture.completedFuture(ChatUtils.colorize(str));
    }

    public void reloadConfig() {
        config.reload();
        try {
            handler.init();
        } catch (LoadException e) {
            this.getLogger().error(e.getMessage());
        }
    }

    public ConditionHandler getHandler() {
        return handler;
    }
}
