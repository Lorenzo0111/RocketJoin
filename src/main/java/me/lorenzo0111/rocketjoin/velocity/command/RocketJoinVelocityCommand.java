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

package me.lorenzo0111.rocketjoin.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import me.lorenzo0111.rocketjoin.velocity.RocketJoinVelocity;
import me.lorenzo0111.rocketjoin.velocity.command.subcommands.HelpCommand;
import me.lorenzo0111.rocketjoin.velocity.command.subcommands.ReloadCommand;
import me.lorenzo0111.rocketjoin.velocity.utilities.ChatUtils;
import me.lorenzo0111.rocketjoin.velocity.utilities.UpdateChecker;

import java.util.ArrayList;
import java.util.List;

public class RocketJoinVelocityCommand implements SimpleCommand {
    private final RocketJoinVelocity plugin;
    private final UpdateChecker updater;
    private final ArrayList<SubCommand> subcommands = new ArrayList<>();
    private final List<String> commandsName = new ArrayList<>();

    public RocketJoinVelocityCommand(RocketJoinVelocity plugin) {
        this.plugin = plugin;
        this.updater = plugin.getUpdater();

        this.subcommands.add(new HelpCommand(this));
        this.subcommands.add(new ReloadCommand(this));

        this.commandsName.add("help");
        this.commandsName.add("reload");
    }

    @Override
    public void execute(Invocation invocation) {
        final CommandSource sender = invocation.source();
        final String[] args = invocation.arguments();

        sender.sendMessage(ChatUtils.colorize(plugin.getConfig().node("prefix").getString() + "&r &7Running &eRocketJoin &ev" + plugin.getVersion() + " &7by &eLorenzo0111&7!", null));

        if (!sender.hasPermission("rocketjoin.command")) {
            sender.sendMessage(ChatUtils.colorize(plugin.getConfig().node("prefix").getString() + "&r &cYou do not have the permission to execute this command.", null));
            return;
        }

        if (args.length > 0){
            for (int i = 0; i < getSubcommands().size(); i++){
                if (args[0].equalsIgnoreCase(getSubcommands().get(i).getName())){
                    getSubcommands().get(i).perform(sender, args);
                    return;
                }
            }

        } else {
            sender.sendMessage(ChatUtils.colorize(plugin.getConfig().node("prefix").getString() + "&r &7Use &8/rocketjoin help&7 for a command list", null));
            return;
        }

        sender.sendMessage(ChatUtils.colorize(plugin.getConfig().node("prefix").getString() + "&r &7Command not found, use &8/rocketjoin help&7 for a command list", null));
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return commandsName;
    }

    public ArrayList<SubCommand> getSubcommands(){
        return subcommands;
    }

    public RocketJoinVelocity getPlugin() {
        return plugin;
    }

    public UpdateChecker getUpdater() {
        return updater;
    }
}
