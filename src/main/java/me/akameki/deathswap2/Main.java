package me.akameki.deathswap2;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.stream.Collectors;

//alternative Main combining listener and command classes
//change main to me.akameki.deathswap2.Main2 in plugin.yml
public class Main extends JavaPlugin implements Listener, CommandExecutor {
    private static boolean on;
    public static boolean isOn() {
        return on;
    }
    @Override
    public void onEnable() {
        on = false;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (!on) return;
        Player player = e.getEntity();
        player.setGameMode(GameMode.SPECTATOR);
        List<Player> alive;
        alive = this.getServer().getOnlinePlayers().stream().filter(p -> p.getGameMode()!=GameMode.SURVIVAL).collect(Collectors.toList());
        if (alive.size() <= 1) {
            on = false;
            Player winner = alive.get(0);
            this.getServer().broadcastMessage(ChatColor.GOLD + winner.getDisplayName() + " has won!");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("start")) {
            if (on) {
                sender.sendMessage(ChatColor.RED + "Deathswap running, use /stop first!");
                return true;
            }

            int period = 15*20;
            int variation = 0;

            //if sender gives period
            if (args.length>=1) {
                try {
                    period = Integer.valueOf(args[0])*20;
                    if (period < 10*20) throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Enter valid time between swaps in seconds (at least 10)");
                    return false;
                }
                //if sender also gives variation
                if (args.length == 2) {
                    if (args.length > 2) return false;
                    try {
                        variation = Integer.valueOf(args[1])*20;
                        if (variation < 0 || variation > period-10*20) throw new NumberFormatException();
                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.RED + "enter valid variation in seconds (at least 10 less than period");
                        return false;
                    }
                }
            }
            if (this.getServer().getOnlinePlayers().size() < 2) {
                sender.sendMessage(ChatColor.RED + "Need at least two players!");
                if (this.getServer().getOnlinePlayers().size() == 1) {
                    sender.sendMessage(ChatColor.YELLOW + "oh well, have fun debugging");
                } else { //no players?
                    return true;
                }
            }

            on = true;
            this.getServer().broadcastMessage(ChatColor.GOLD + "Swaps every " + period/20 + " seconds with " + variation/20 +" variation, no nether.");
            this.getServer().broadcastMessage(ChatColor.GOLD + "Good luck! >:D");
            //teleport players to non liquid block, resets their stuff
            for (Player player : this.getServer().getOnlinePlayers()) {
                player.setGameMode(GameMode.SURVIVAL);
                player.setHealth(20);
                player.setFoodLevel(20);
                player.setSaturation(20);
                player.getInventory().clear();
                player.getWorld().setTime(0);
                Location location;
                do {
                    location = new Location(player.getWorld(), Math.random() * 4000 - 2000, 60.0, Math.random() * 4000 - 2000);
                    location.setY(player.getWorld().getHighestBlockYAt(location));
                } while (location.getBlock().isLiquid());
                location.setY(location.getY() + 1);
                player.teleport(location);
            }

            //if no variation, run TaskSwap on loop
            //else run TaskSwap once after random delay, TaskSwap will detect if variation is not 0 and call itself random delays
            if (variation == 0) {
                BukkitTask swap = new TaskSwap(this).runTaskTimer(this, period - 10*20, period);
            } else {
                int randomVariation = (int)(Math.random()*variation*2 - variation) + 3; //+3 ticks to be safe, might not be needed
                BukkitTask swap = new TaskSwap(this, period, variation).runTaskLater(this, period+randomVariation - 10*20);
            }
            return true;
        }


        if (command.getName().equalsIgnoreCase("end")) {
            if (on) {
                on = false;
                PlayerDeathEvent.getHandlerList().unregister((Plugin) this);
                sender.sendMessage("Deathswap has been stopped!");
            } else {
                sender.sendMessage(ChatColor.RED + "Deathswap is not running");
            }
            return true;
        }
        return false;
    }
}