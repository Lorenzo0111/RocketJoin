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

package me.lorenzo0111.rocketjoin.command;

import me.lorenzo0111.rocketjoin.RocketJoinSponge;
import me.lorenzo0111.rocketjoin.command.subcommands.HelpCommand;
import me.lorenzo0111.rocketjoin.command.subcommands.ReloadCommand;
import me.lorenzo0111.rocketjoin.common.ChatUtils;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.service.permission.Subject;

import java.util.ArrayList;
import java.util.Optional;

public class RocketJoinSpongeCommand implements CommandExecutor {
    private final RocketJoinSponge plugin;
    private final Parameter.Value<String> subcommand;
    private final ArrayList<SubCommand> subcommands = new ArrayList<>();

    public RocketJoinSpongeCommand(RocketJoinSponge plugin, Parameter.Value<String> subcommand) {
        this.plugin = plugin;
        this.subcommand = subcommand;

        this.subcommands.add(new HelpCommand(this));
        this.subcommands.add(new ReloadCommand(this));
    }

    @Override
    public @NotNull CommandResult execute(CommandContext args) {
        Audience audience = args.cause().audience();
        Subject sender = args.cause().subject();

        audience.sendMessage(ChatUtils.colorize(plugin.getConfig().prefix() + "&r &7Running &eRocketJoin &ev" + plugin.getVersion() + " &7by &eLorenzo0111&7!"));

        if (!sender.hasPermission("rocketjoin.command")) {
            audience.sendMessage(ChatUtils.colorize(plugin.getConfig().prefix() + "&r &cYou do not have the permission to execute this command."));
            return CommandResult.success();
        }

        Optional<String> subcommand = args.one(this.subcommand);

        if (subcommand.isPresent()){
            for (int i = 0; i < getSubcommands().size(); i++){
                if (subcommand.get().equalsIgnoreCase(getSubcommands().get(i).getName())){
                    getSubcommands().get(i).perform(args);
                    return CommandResult.success();
                }
            }

        } else {
            audience.sendMessage(ChatUtils.colorize(plugin.getConfig().prefix() + "&r &7Use &8/rocketjoin help&7 for a command list"));
            return CommandResult.success();
        }

        audience.sendMessage(ChatUtils.colorize(plugin.getConfig().prefix() + "&r &7Command not found, use &8/rocketjoin help&7 for a command list"));

        return CommandResult.success();
    }

    public ArrayList<SubCommand> getSubcommands(){
        return subcommands;
    }

    public RocketJoinSponge getPlugin() {
        return plugin;
    }
}
