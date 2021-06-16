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

package me.lorenzo0111.rocketjoin.spigot.command.subcommands;

import me.lorenzo0111.rocketjoin.spigot.command.RocketJoinCommand;
import me.lorenzo0111.rocketjoin.spigot.command.SubCommand;
import org.bukkit.command.CommandSender;

public class HelpCommand extends SubCommand {

    public HelpCommand(RocketJoinCommand command) {
        super(command);
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        sender.sendMessage(getCommand().getPlugin().parse(this.getCommand().getPlugin().getPrefix() + "&r &8/rocketjoin help » &7Show this message!"));
        sender.sendMessage(getCommand().getPlugin().parse(this.getCommand().getPlugin().getPrefix() + "&r &8/rocketjoin reload » &7Reload the plugin!"));
        sender.sendMessage(getCommand().getPlugin().parse(this.getCommand().getPlugin().getPrefix() + "&r &8/rocketjoin debug » &7Print debug message!"));
    }
}
