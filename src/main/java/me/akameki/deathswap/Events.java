package me.akameki.deathswap;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.stream.Collectors;

public class Events implements Listener {
    private final JavaPlugin pl;
    public Events(JavaPlugin pl) {
        this.pl = pl;
    }
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        player.setGameMode(GameMode.SPECTATOR);
        //Declare winner, turn stuff off
        List<Player> alive = pl.getServer().getOnlinePlayers().stream().filter(p -> p.getGameMode()==GameMode.SURVIVAL).collect(Collectors.toList());
        if (alive.size() <= 1) {
            pl.getServer().broadcastMessage(ChatColor.RED +""+ ChatColor.BOLD + "Game over!");
            Main.setOff();
            PlayerDeathEvent.getHandlerList().unregister(pl);
            if (!alive.isEmpty()) { //should only be empty if started with one player
                Player winner = alive.get(0);
                pl.getServer().broadcastMessage(ChatColor.GOLD +""+ ChatColor.BOLD + winner.getDisplayName() + ChatColor.RESET + " has won!");
            }
        }
    }
}
