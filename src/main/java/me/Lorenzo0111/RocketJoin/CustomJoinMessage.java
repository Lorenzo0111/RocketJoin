package me.Lorenzo0111.RocketJoin;

import me.Lorenzo0111.RocketJoin.Command.MainCommand;
import me.Lorenzo0111.RocketJoin.Listener.JoinLeave;
import me.Lorenzo0111.RocketJoin.Updater.UpdateChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.Callable;

public class CustomJoinMessage extends JavaPlugin implements Listener {

    /*

    Plugin by Lorenzo0111 - https://github.com/Lorenzo0111

     */

    public static boolean placeholderapi = true;

    public void onEnable() {
        new UpdateChecker(this, 82520).getVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                getLogger().info("There is not a new update available.");
            } else {
                getLogger().info("There is a new update available. Download it from: https://bit.ly/RocketJoin");
            }
        });

        saveDefaultConfig();
        Bukkit.getServer().getPluginManager().registerEvents(new JoinLeave(this), this);
        this.getCommand("rocketjoin").setExecutor(new MainCommand(this));
        this.getCommand("rocketjoin").setTabCompleter(new MainCommand(this));

        Metrics metrics = new Metrics(this, 9382);
        metrics.addCustomChart(new Metrics.SimplePie("vip_features", () -> {
            if (getConfig().getBoolean("enable_vip_features")) {
                return "Yes";
            }
            return "No";
        }));

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            getLogger().info("PlaceholderAPI hooked!");
            placeholderapi = true;
            getLogger().info(this.getDescription().getName() + " v" + this.getDescription().getVersion() + " by Lorenzo0111 is now enabled!");
            return;
        }

        placeholderapi = false;
        getLogger().warning("Could not find PlaceholderAPI! Whitout PlaceholderAPI you can't use placeholders.");
        getLogger().info("Loaded internal placeholers: {Player} and {DisplayPlayer}");
        getLogger().info(this.getDescription().getName() + " v" + this.getDescription().getVersion() + " by Lorenzo0111 is now enabled!");
    }

    public void onDisable() {
        getLogger().info("Plugin disabled!");
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