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
import me.lorenzo0111.rocketjoin.command.RocketJoinSpongeCommand;
import me.lorenzo0111.rocketjoin.common.ChatUtils;
import me.lorenzo0111.rocketjoin.common.ConfigExtractor;
import me.lorenzo0111.rocketjoin.common.conditions.ConditionHandler;
import me.lorenzo0111.rocketjoin.common.config.IConfiguration;
import me.lorenzo0111.rocketjoin.common.config.file.FileConfiguration;
import me.lorenzo0111.rocketjoin.common.exception.LoadException;
import me.lorenzo0111.rocketjoin.common.hex.HexUtils;
import me.lorenzo0111.rocketjoin.listener.JoinListener;
import me.lorenzo0111.rocketjoin.listener.LeaveListener;
import me.lorenzo0111.rocketjoin.utilities.UpdateChecker;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

@Plugin(id = "rocketjoin", name = "RocketJoin", version = "@version@",
        description = "Custom Join Messages Plugin", authors = {"Lorenzo0111"})
public class RocketJoinSponge {
    @Inject private Logger logger;
    @ConfigDir(sharedRoot = false) @Inject private Path path;
    private IConfiguration conf;
    @Inject private Game game;
    private PluginContainer plugin;
    private UpdateChecker updater;
    private ConditionHandler handler;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        final Optional<PluginContainer> pluginContainer = game.getPluginManager().fromInstance(this);

        if (!pluginContainer.isPresent()) {
            try {
                throw new LoadException("Unable to get plugin container. Report code: CONTAINER");
            } catch (LoadException e) {
                this.getLogger().error(e.getMessage());
            }
            return;
        }

        this.plugin = pluginContainer.get();

        this.updater = new UpdateChecker(this,"rocketjoin", "https://bit.ly/RocketJoin");
        this.updater.fetch().thenAccept((available) -> this.updater.sendUpdateCheck(this.game.getServer().getConsole(),available));

        File config = new ConfigExtractor(this.getClass(),path.toFile(), "config.yml")
                .extract();

        this.conf = new FileConfiguration(config);
        try {
            this.handler = new ConditionHandler(conf);
        } catch (LoadException e) {
            this.getLogger().error(e.getMessage());
            return;
        }

        this.reloadConfig();

        game.getEventManager().registerListeners(this, new JoinListener(this));
        game.getEventManager().registerListeners(this, new LeaveListener(this));

        CommandSpec myCommandSpec = CommandSpec.builder()
                .description(Text.of("RocketJoin main command"))
                .arguments(
                        GenericArguments.optional(GenericArguments.string(Text.of("subcommand"))),
                        GenericArguments.optional(GenericArguments.string(Text.of("metrics")))
                        )
                .executor(new RocketJoinSpongeCommand(this))
                .build();

        this.getGame().getCommandManager().register(plugin, myCommandSpec, "rj", "rocketjoin", "rjs");

        logger.info("RocketJoin loaded!");
    }

    public void reloadConfig() {
        conf.reload();
        try {
            handler.init();
        } catch (LoadException e) {
            this.getLogger().error(e.getMessage());
        }
    }

    public IConfiguration getConfig() {
        return conf;
    }

    public Game getGame() {
        return game;
    }

    public Logger getLogger() {
        return logger;
    }

    public PluginContainer getPlugin() {
        return plugin;
    }

    public UpdateChecker getUpdater() {
        return updater;
    }

    public String getVersion() {
        final Optional<String> s = this.getPlugin().getVersion();
        if (!s.isPresent()) {
            try {
                throw new LoadException("Version cannot be null.");
            } catch (LoadException e) {
                this.getLogger().error(e.getMessage());
            }

            return null;
        }

        return s.get();
    }

    public Text parse(String string, CommandSource source) {
        String str = string.replace("{player}", source.getName());

        str = ChatUtils.colorize(str);
        str = HexUtils.translateHexColorCodes(str);
        return Text.of(str);
    }

    public ConditionHandler getHandler() {
        return handler;
    }
}
