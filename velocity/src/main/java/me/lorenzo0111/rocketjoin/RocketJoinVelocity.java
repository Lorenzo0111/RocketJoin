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
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import me.lorenzo0111.rocketjoin.command.RocketJoinVelocityCommand;
import me.lorenzo0111.rocketjoin.common.ChatUtils;
import me.lorenzo0111.rocketjoin.common.ConfigExtractor;
import me.lorenzo0111.rocketjoin.common.config.IConfiguration;
import me.lorenzo0111.rocketjoin.common.config.file.FileConfiguration;
import me.lorenzo0111.rocketjoin.conditions.ConditionHandler;
import me.lorenzo0111.rocketjoin.listener.JoinListener;
import me.lorenzo0111.rocketjoin.listener.LeaveListener;
import me.lorenzo0111.rocketjoin.listener.SwitchListener;
import me.lorenzo0111.rocketjoin.utilities.UpdateChecker;
import net.kyori.adventure.text.Component;
import org.bstats.charts.SimplePie;
import org.bstats.velocity.Metrics;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

@Plugin(id = "rocketjoin", name = "RocketJoin", version = "@version@",
        description = "Custom Join Messages Plugin", authors = {"Lorenzo0111"})
public class RocketJoinVelocity {
    private final Logger logger;
    private final Path path;
    private final ProxyServer server;
    private final Metrics.Factory metricsFactory;

    private UpdateChecker updater;
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
        this.updater = new UpdateChecker(this,82520, "https://bit.ly/RocketJoin");
        this.updater.fetch().thenAccept((available) -> this.updater.sendUpdateCheck(this.server.getConsoleCommandSource(),available));

        File conf = new ConfigExtractor(this.getClass(),path.toFile(), "config.yml")
                .extract();

        Objects.requireNonNull(conf);

        this.config = new FileConfiguration(conf);
        this.handler = new ConditionHandler(this);

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

        server.getCommandManager().register(meta,new RocketJoinVelocityCommand(this));

        logger.info("RocketJoin loaded!");
    }

    public Logger getLogger() {
        return logger;
    }

    public ProxyServer getServer() {
        return server;
    }

    public UpdateChecker getUpdater() {
        return updater;
    }

    public IConfiguration getConfig() {
        return config;
    }

    public String getVersion() {
        return "@version@";
    }

    public Component parse(@Nullable String string, Player player) {
        String str = string == null ? "" : string.replace("{player}", player.getUsername());
        str = ChatUtils.colorize(str);
        return Component.text(str);
    }

    public void reloadConfig() {
        config.reload();
        handler.init();
    }

    public ConditionHandler getHandler() {
        return handler;
    }
}
