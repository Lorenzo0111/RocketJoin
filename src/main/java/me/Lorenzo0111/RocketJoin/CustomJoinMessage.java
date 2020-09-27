package me.Lorenzo0111.RocketJoin;

import me.Lorenzo0111.RocketJoin.Updater.UpdateChecker;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

import static org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP;

public class CustomJoinMessage extends JavaPlugin implements Listener {

    /*

    Plugin by Lorenzo0111

     */



    // On Enable

    public void onEnable() {
        Logger logger = this.getLogger();
        new UpdateChecker(this, 82520).getVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                logger.info("There is not a new update available.");
            } else {
                logger.info("There is a new update available. Download it from: https://bit.ly/RocketJoin");
            }
        });
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            logger.info("Plugin enabled!");
            Bukkit.getServer().getPluginManager().registerEvents(this, this);
            saveDefaultConfig();
        } else {
            logger.warning("Could not find PlaceholderAPI! This plugin is required.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    public void onDisable() {
        getLogger().info("Plugin disabled!");
    }

    //  Command(Info and reload)
    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("rocketjoin")) {
            if (sender instanceof Player) {
                if (sender.hasPermission("rocketjoin.command")) {
                    if (args.length == 0) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "%prefix%&r &7Plugin by &eLorenzo0111&7!".replace("%prefix%", getConfig().getString("prefix"))));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "%prefix%&r &7Use &8/rocketjoin reload &7to reload the plugin!".replace("%prefix%", getConfig().getString("prefix"))));
                    } else if (args.length == 1) {
                        if (args[0].equalsIgnoreCase("reload")) {
                            reloadConfig();
                            Logger logger = this.getLogger();
                            new UpdateChecker(this, 82520).getVersion(version -> {
                                if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                                    logger.info("There is not a new update available.");
                                } else {
                                    logger.info("There is a new update available. Download it from: https://bit.ly/RocketJoin");
                                }
                            });
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "%prefix%&r &7Plugin reloaded!".replace("%prefix%", getConfig().getString("prefix"))));
                        } else {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "%prefix%&r &7Use &8/rocketjoin reload &7to reload the plugin!".replace("%prefix%", getConfig().getString("prefix"))));
                        }
                    } else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "%prefix%&r &7Plugin by &eLorenzo0111&7!".replace("%prefix%", getConfig().getString("prefix"))));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "%prefix%&r &7Use &8/rocketjoin reload &7to reload the plugin!".replace("%prefix%", getConfig().getString("prefix"))));
                    }
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix") + " " + getConfig().getString("no_permission")));
                }
            } else {
                if (args.length == 0) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "%prefix%&r &7Plugin by &eLorenzo0111&7!".replace("%prefix%", getConfig().getString("prefix"))));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "%prefix%&r &7Use &8/rocketjoin reload &7to reload the plugin!".replace("%prefix%", getConfig().getString("prefix"))));
                } else if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("reload")) {
                        reloadConfig();
                        Logger logger = this.getLogger();
                        new UpdateChecker(this, 82520).getVersion(version -> {
                            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                                logger.info("There is not a new update available.");
                            } else {
                                logger.info("There is a new update available. Download it from: https://bit.ly/RocketJoin");
                            }
                        });
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "%prefix%&r &7Plugin reloaded!".replace("%prefix%", getConfig().getString("prefix"))));
                    } else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "%prefix%&r &7Use &8/rocketjoin reload &7to reload the plugin!".replace("%prefix%", getConfig().getString("prefix"))));
                    }
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "%prefix%&r &7Plugin by &eLorenzo0111&7!".replace("%prefix%", getConfig().getString("prefix"))));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "%prefix%&r &7Use &8/rocketjoin reload &7to reload the plugin!".replace("%prefix%", getConfig().getString("prefix"))));
                }
            }
        } return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();

        if (e.getPlayer().hasPermission("rocketjoin.vip")) {
            if (getConfig().getBoolean("enable_vip_features")) {
                if (getConfig().getBoolean("vip_firework")) {
                    spawnFireworks(e.getPlayer().getLocation(), getConfig().getInt("vip_firework_to_spawn"));
                }
                if (getConfig().getBoolean("vip_sound")) {
                    for (Player xplayer : Bukkit.getOnlinePlayers()) {
                        xplayer.playSound(xplayer.getLocation(), ENTITY_EXPERIENCE_ORB_PICKUP, 60f, 1f);
                    }
                }
                if (getConfig().getBoolean("vip_join")) {
                    String joinText = ChatColor.translateAlternateColorCodes('&', getConfig().getString("vip_join_message").replace("{player}", p.getName()).replace("{DisplayPlayer}", p.getDisplayName()));
                    joinText = PlaceholderAPI.setPlaceholders(p, joinText);
                    e.setJoinMessage(joinText);
                    return;
                }
            }
        }

        if (getConfig().getBoolean("enable_join_message")) {


            String joinText = ChatColor.translateAlternateColorCodes('&', getConfig().getString("join_message").replace("{player}", p.getName()).replace("{DisplayPlayer}", p.getDisplayName()));
            joinText = PlaceholderAPI.setPlaceholders(p, joinText);
            e.setJoinMessage(joinText);
        } else {
            e.setJoinMessage(null);
        }

        if (e.getPlayer().hasPermission("rocketjoin.update")) {
            new UpdateChecker(this, 82520).getVersion(version -> {
                if (!this.getDescription().getVersion().equalsIgnoreCase(version)) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&l&m---------------------------------------"));
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lRocket&e&lJoin &f&l» &7There is a new update available."));
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lRocket&e&lJoin &f&l» &7Download it from: &ehttps://bit.ly/RocketJoin"));
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&l&m---------------------------------------"));
                }
            });
        }

        /* TODO: if (getConfig().getBoolean("display_title")) {
         p.sendTitle(getConfig().getString("join_title").replace("&", "§").replace("{player}", p.getName()).replace("{DisplayPlayer}", p.getDisplayName()), getConfig().getString("join_subtitle").replace("&", "§").replace("{player}", p.getName()).replace("{DisplayPlayer}", p.getDisplayName()), 1, 20, 1);
         Bukkit.broadcastMessage("true");
         } */

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        if (e.getPlayer().hasPermission("rocketjoin.vip")) {
            if (getConfig().getBoolean("enable_vip_features")) {
                if (getConfig().getBoolean("vip_leave")) {
                    String quitText = ChatColor.translateAlternateColorCodes('&', getConfig().getString("vip_leave_message").replace("{player}", p.getName()).replace("{DisplayPlayer}", p.getDisplayName()));
                    quitText = PlaceholderAPI.setPlaceholders(p, quitText);
                    e.setQuitMessage(quitText);
                    return;
                }
            }
        }

        if (getConfig().getBoolean("enable_leave_message")) {



            String quitText = ChatColor.translateAlternateColorCodes('&',getConfig().getString("leave_message").replace("{player}", p.getName()).replace("{DisplayPlayer}", p.getDisplayName()));
            quitText = PlaceholderAPI.setPlaceholders(p, quitText);
            e.setQuitMessage(quitText);
        } else {
            e.setQuitMessage(null);
        }
    }


    public static void spawnFireworks(Location location, int amount){
        Firework fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();

        fwm.setPower(2);
        fwm.addEffect(FireworkEffect.builder().withColor(Color.ORANGE).flicker(true).build());

        fw.setFireworkMeta(fwm);
        fw.detonate();

        for(int i = 0;i<amount; i++){
            Firework fw2 = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
            fw2.setFireworkMeta(fwm);
        }
    }
}