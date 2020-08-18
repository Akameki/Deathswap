package me.akameki.deathswap;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public final class Main extends JavaPlugin {
    private static final List<BukkitRunnable> currentTasks = new ArrayList<>();

    public static boolean isOn() {
        return !currentTasks.isEmpty();
    }
    public static void setOff(){
        currentTasks.clear();
    }
    public static void addTask(BukkitRunnable bukkitRunnable) {
        currentTasks.add(bukkitRunnable);
    }
    public static boolean notAllowedToRun(BukkitRunnable bukkitRunnable){
        return !currentTasks.contains(bukkitRunnable);
    }

    @Override
    public void onEnable() {
        Commands commands = new Commands(this);
        getCommand("start").setExecutor(commands);
        getCommand("end").setExecutor(commands);
    }

    @Override
    public void onDisable() {
        // :)
    }
}
