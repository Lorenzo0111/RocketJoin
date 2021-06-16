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

package me.lorenzo0111.rocketjoin.spigot.command;

import me.lorenzo0111.rocketjoin.spigot.RocketJoin;
import me.lorenzo0111.rocketjoin.spigot.command.subcommands.DebugCommand;
import me.lorenzo0111.rocketjoin.spigot.command.subcommands.HelpCommand;
import me.lorenzo0111.rocketjoin.spigot.command.subcommands.ReloadCommand;
import me.lorenzo0111.rocketjoin.spigot.updater.UpdateChecker;
import me.lorenzo0111.rocketjoin.spigot.utilities.Debugger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RocketJoinCommand implements CommandExecutor, TabCompleter {


    private final RocketJoin plugin;
    private final UpdateChecker updater;
    private final Debugger debugger;
    private final ArrayList<SubCommand> subcommands = new ArrayList<>();

    public RocketJoinCommand(RocketJoin plugin, UpdateChecker updater) {
        this.plugin = plugin;
        this.updater = updater;
        this.debugger = new Debugger(plugin);

        this.subcommands.add(new HelpCommand(this));
        this.subcommands.add(new ReloadCommand(this));
        this.subcommands.add(new DebugCommand(this));
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        sender.sendMessage(plugin.parse(plugin.getPrefix() + "&r &7Running &e" + plugin.getDescription().getName() + " &ev" + plugin.getDescription().getVersion() + " &7by &eLorenzo0111&7!"));

        if (!sender.hasPermission("rocketjoin.command")) {
            sender.sendMessage(plugin.parse(plugin.getPrefix() + "&r &cYou do not have the permission to execute this command."));
            return true;
        }

        if (args.length > 0){
            for (int i = 0; i < getSubcommands().size(); i++){
                if (args[0].equalsIgnoreCase(getSubcommands().get(i).getName())){
                    getSubcommands().get(i).perform(sender, args);
                    return true;
                }
            }

        } else {
            sender.sendMessage(plugin.parse(plugin.getPrefix() + "&r &7Use &8/rocketjoin help&7 for a command list"));
            return true;
        }

        sender.sendMessage(plugin.parse(plugin.getPrefix() + "&r &7Command not found, use &8/rocketjoin help&7 for a command list"));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        final List<String> strings = new ArrayList<>();

        for (int i = 0; i < getSubcommands().size(); i++){
            if (args[0].equalsIgnoreCase(getSubcommands().get(i).getName())){
                strings.add(getSubcommands().get(i).getName());
            }
        }

        return strings;
    }

    public ArrayList<SubCommand> getSubcommands(){
        return subcommands;
    }

    public RocketJoin getPlugin() {
        return plugin;
    }

    public Debugger getDebugger() {
        return debugger;
    }

    public UpdateChecker getUpdater() {
        return updater;
    }
}
