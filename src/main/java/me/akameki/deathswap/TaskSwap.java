package me.akameki.deathswap;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TaskSwap extends BukkitRunnable {
    private final JavaPlugin pl;
    private int period;
    private int variation;
    private int count = 10;

    public TaskSwap(JavaPlugin pl) {
        this.pl = pl;
    }
    public TaskSwap(JavaPlugin pl, int period, int variation) {
        this.pl = pl;
        this.period = period;
        this.variation = variation;
    }

    @Override
    public void run() {
        if (Main.notAllowedToRun(this)) {
            this.cancel();
            return;
        }

        //creates a timer task that counts down and swaps players on 0
        BukkitRunnable timer = new BukkitRunnable() {
            @Override
            public void run() {
                if (Main.notAllowedToRun(this)) {
                    this.cancel();
                    return;
                }
                if (count>0) {
                    pl.getServer().broadcastMessage(ChatColor.YELLOW + "Swapping in "+count);
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
                        players.get(i).teleport(i==players.size()-1 ? locations.get(0) : locations.get(i+1));
                    }

                    //if has variation, recall TaskSwap with random delay
                    if (variation != 0) {
                        int randomVariation = (int) (Math.random() * variation * 2 - variation)+3;
                        BukkitRunnable task = new TaskSwap(pl, period, variation);
                        Main.addTask(task);
                        task.runTaskLater(pl, period+randomVariation - 10*20);
                    }
                }
            }
        };
        Main.addTask(timer);
        timer.runTaskTimer(pl, 0, 20);
    }
}
