package me.akameki.deathswap;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public final class Main extends JavaPlugin {
    private static List<BukkitTask> currentTasks = new ArrayList<>();

    public static boolean isOn() {
        return !currentTasks.isEmpty();
    }

    public static boolean allowedToRun(BukkitTask bukkitTask){
        return currentTasks.contains(bukkitTask);
    }
    public static void killTasks(){
        currentTasks.clear();
    }

    @Override
    public void onEnable() {
        getCommand("start").setExecutor(new CommandStart(this));
        getCommand("end").setExecutor(new CommandEnd(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
