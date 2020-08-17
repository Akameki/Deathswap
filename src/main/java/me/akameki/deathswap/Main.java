package me.akameki.deathswap;

import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    private static boolean on;

    public static boolean isOn() {
        return on;
    }

    public static void setOn(boolean on) {
        Main.on = on;
    }

    @Override
    public void onEnable() {
        on = false;
        getCommand("start").setExecutor(new CommandStart(this));
        getCommand("end").setExecutor(new CommandEnd(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
