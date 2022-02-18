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

package me.lorenzo0111.rocketjoin.command.subcommands;

import com.velocitypowered.api.command.CommandSource;
import me.lorenzo0111.rocketjoin.command.RocketJoinVelocityCommand;
import me.lorenzo0111.rocketjoin.command.SubCommand;
import me.lorenzo0111.rocketjoin.common.ChatUtils;

public class HelpCommand extends SubCommand {

    public HelpCommand(RocketJoinVelocityCommand command) {
        super(command);
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public void perform(CommandSource sender, String[] args) {
        sender.sendMessage(ChatUtils.colorize(this.getCommand().getPlugin().getConfig().prefix() + "&r &8/rocketjoin help » &7Show this message!"));
        sender.sendMessage(ChatUtils.colorize(this.getCommand().getPlugin().getConfig().prefix() + "&r &8/rocketjoin reload » &7Reload the plugin!"));
        sender.sendMessage(ChatUtils.colorize(this.getCommand().getPlugin().getConfig().prefix() + "&r &8/rocketjoin debug » &7Print debug message!"));
    }
}
