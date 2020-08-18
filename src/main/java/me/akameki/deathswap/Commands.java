package me.akameki.deathswap;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Commands implements CommandExecutor {
    private final JavaPlugin pl;

    public Commands(JavaPlugin pl) {
        this.pl = pl;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //start
        if (command.getName().equalsIgnoreCase("start")) {
            if (Main.isOn()) {
                sender.sendMessage(ChatColor.RED + "Deathswap running, use /stop first!");
                return true;
            }
            int period = 60*5*20;
            int variation = 0;
            //if sender gives period
            if (args.length>=1) {
                try {
                    period = Integer.parseInt(args[0])*20;
                    if (period < 10*20) throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Enter valid time between swaps in seconds (at least 10)");
                    return false;
                }
                //if sender also gives variation
                if (args.length >= 2) {
                    if (args.length > 2) return false;
                    try {
                        variation = Integer.parseInt(args[1])*20;
                        if (variation < 0 || variation > period-10*20) throw new NumberFormatException();
                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.RED + "enter valid variation in seconds (at least 10 less than period)");
                        return false;
                    }
                }
            }
            if (pl.getServer().getOnlinePlayers().size() < 2) {
                sender.sendMessage(ChatColor.RED + "Need at least two players!");
                if (pl.getServer().getOnlinePlayers().size() == 1) {
                    sender.sendMessage(ChatColor.YELLOW + "oh well, have fun debugging");
                } else { //no players?
                    return true;
                }
            }

            pl.getServer().broadcastMessage(ChatColor.GOLD + "Swap every " + period/20 + " seconds with " + variation/20 +" variation, no nether.");
            pl.getServer().broadcastMessage(ChatColor.GOLD + "Starting Deathswap...");
            //teleport players to non liquid block, resets their stuff
            for (Player player : pl.getServer().getOnlinePlayers()) {
                Location location;
                do {
                    location = new Location(player.getWorld(), Math.random()*4000 - 2000, 60.0, Math.random()*4000 - 2000);
                    location.setY(player.getWorld().getHighestBlockYAt(location)+1);
                } while (location.getBlock().isLiquid());
                player.teleport(location);
                player.setGameMode(GameMode.SURVIVAL);
                player.setHealth(20);
                player.setFoodLevel(20);
                player.setSaturation(20);
                player.getInventory().clear();
                player.getWorld().setTime(0);
            }
            //if no variation, run TaskSwap on loop
            //else run TaskSwap once after random delay, TaskSwap will detect if variation!=0 and call itself with random delays
            BukkitRunnable task;
            if (variation == 0) {
                task = new TaskSwap(pl);
                task.runTaskTimer(pl, period - 10*20, period);
            } else {
                int randomVariation = (int)(Math.random()*variation*2 - variation) + 3; //+3 ticks to be safe, might not be needed
                task = new TaskSwap(pl, period, variation);
                task.runTaskLater(pl, period+randomVariation - 10*20);
            }
            Main.addTask(task);

            pl.getServer().getPluginManager().registerEvents(new Events(pl), pl);
            pl.getServer().broadcastMessage(ChatColor.GREEN +""+ ChatColor.BOLD + "Good luck! >:D");
            return true;
        }

        //end
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
