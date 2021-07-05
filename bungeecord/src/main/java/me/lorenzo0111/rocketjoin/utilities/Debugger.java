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

package me.lorenzo0111.rocketjoin.utilities;

import me.lorenzo0111.rocketjoin.RocketJoinBungee;

import java.util.logging.Logger;

public class Debugger {

    private final RocketJoinBungee plugin;
    private final Logger logger;

    public Debugger(RocketJoinBungee plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    public void debug() {
        this.log("-----------[ RocketPlugins Debugger ]-----------");

        this.log("Server Information:");
        this.logData("Server Version", plugin.getProxy().getVersion());

        this.log("");

        this.log("Plugin Information");
        this.logData("Plugin Name", plugin.getDescription().getName());
        this.logData("Plugin Version", plugin.getDescription().getVersion());

        this.log("-----------[ RocketPlugins Debugger ]-----------");
    }

    private void logData(String prefix, String message) {
        this.log(prefix + ": " + message);
    }

    private void log(String message) {
        logger.info(message);
    }
}

