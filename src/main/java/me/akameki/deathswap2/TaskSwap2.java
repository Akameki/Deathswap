package me.akameki.deathswap2;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TaskSwap2 extends BukkitRunnable {
    private JavaPlugin pl;
    private int period;
    private int variation;
    private int count = 10;

    public TaskSwap2(JavaPlugin pl) {
        this.pl = pl;
    }
    public TaskSwap2(JavaPlugin pl, int period, int variation) {
        this.pl = pl;
        this.period = period;
        this.variation = variation;
    }

    @Override
    public void run() {
        if (!Main2.isOn()) {
            this.cancel();
            return;
        }
        //creates a timer task that counts down and swaps players on 0
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!Main2.isOn()) {
                    this.cancel();
                    return;
                }
                if (count>0) {
                    pl.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + "Swapping in "+count);
                    count--;
                } else {
                    this.cancel();
                    pl.getServer().broadcastMessage(ChatColor.RED + "Swap!");
                    count = 10;
                    //swap players in random circle
                    ArrayList<Player> players = new ArrayList<>();
                    List<Location> locations = new ArrayList<>();
                    for (Player player : pl.getServer().getOnlinePlayers()) {
                        if (player.getGameMode().equals(GameMode.SURVIVAL)) {
                            players.add(player);

                        }
                    }
                    Collections.shuffle(players);
                    for (Player player : players) {
                        locations.add(player.getLocation());
                    }
                    for (int i = 0; i < players.size(); i++) {
                        if (i == players.size() - 1) {
                            players.get(i).teleport(locations.get(0));
                        } else {
                            players.get(i).teleport(locations.get(i + 1));
                        }
                    }

                    //if has variation, recall TaskSwap with random delay
                    if (variation != 0) {
                        int randomVariation = (int) (Math.random() * variation * 2 - variation)+3;
                        BukkitTask swap = new me.akameki.deathswap.TaskSwap(pl, period, variation).runTaskLater(pl, period+randomVariation - 10*20);
                    }
                }
            }
        }.runTaskTimer(pl, 0, 20);

    }
}
