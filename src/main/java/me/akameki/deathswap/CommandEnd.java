package me.akameki.deathswap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandEnd implements CommandExecutor {
    private JavaPlugin pl;

    public CommandEnd(JavaPlugin pl) {
        this.pl = pl;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("end")) {
            if (Main.isOn()) {
                Main.setOff();
                PlayerDeathEvent.getHandlerList().unregister(pl);
                pl.getServer().broadcastMessage("Deathswap has been stopped!");
            } else {
                sender.sendMessage(ChatColor.RED + "Deathswap is not running");
            }
            return true;
        }
        return false;
    }
}

