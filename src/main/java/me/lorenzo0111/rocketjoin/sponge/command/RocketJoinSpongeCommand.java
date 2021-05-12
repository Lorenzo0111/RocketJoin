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

package me.lorenzo0111.rocketjoin.sponge.command;

import me.lorenzo0111.rocketjoin.sponge.RocketJoinSponge;
import me.lorenzo0111.rocketjoin.sponge.command.subcommands.HelpCommand;
import me.lorenzo0111.rocketjoin.sponge.command.subcommands.MetricsCommand;
import me.lorenzo0111.rocketjoin.sponge.command.subcommands.ReloadCommand;
import me.lorenzo0111.rocketjoin.sponge.utilities.UpdateChecker;
import me.lorenzo0111.rocketjoin.velocity.utilities.ChatUtils;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.Optional;

public class RocketJoinSpongeCommand implements CommandExecutor {
    private final RocketJoinSponge plugin;
    private final UpdateChecker updater;
    private final ArrayList<SubCommand> subcommands = new ArrayList<>();

    public RocketJoinSpongeCommand(RocketJoinSponge plugin) {
        this.plugin = plugin;
        this.updater = plugin.getUpdater();

        this.subcommands.add(new HelpCommand(this));
        this.subcommands.add(new ReloadCommand(this));
        this.subcommands.add(new MetricsCommand(this));
    }

    @Override
    public @NotNull CommandResult execute(CommandSource sender, @NotNull CommandContext args) {
        sender.sendMessage(Text.of(ChatUtils.colorize(plugin.getConfig().node("prefix").getString() + "&r &7Running &eRocketJoin &ev" + plugin.getVersion() + " &7by &eLorenzo0111&7!")));

        if (!sender.hasPermission("rocketjoin.command")) {
            sender.sendMessage(Text.of(ChatUtils.colorize(plugin.getConfig().node("prefix").getString() + "&r &cYou do not have the permission to execute this command.")));
            return CommandResult.success();
        }

        Optional<String> subcommand = args.getOne("subcommand");

        if (subcommand.isPresent()){
            for (int i = 0; i < getSubcommands().size(); i++){
                if (subcommand.get().equalsIgnoreCase(getSubcommands().get(i).getName())){
                    getSubcommands().get(i).perform(sender, args);
                    return CommandResult.success();
                }
            }

        } else {
            sender.sendMessage(Text.of(ChatUtils.colorize(plugin.getConfig().node("prefix").getString() + "&r &7Use &8/rocketjoin help&7 for a command list")));
            return CommandResult.success();
        }

        sender.sendMessage(Text.of(ChatUtils.colorize(plugin.getConfig().node("prefix").getString() + "&r &7Command not found, use &8/rocketjoin help&7 for a command list")));

        return CommandResult.success();
    }

    public ArrayList<SubCommand> getSubcommands(){
        return subcommands;
    }

    public RocketJoinSponge getPlugin() {
        return plugin;
    }

    public UpdateChecker getUpdater() {
        return updater;
    }
}
