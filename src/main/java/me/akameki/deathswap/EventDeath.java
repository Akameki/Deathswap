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

public class EventDeath implements Listener {
    private JavaPlugin pl;
    public EventDeath(JavaPlugin pl) {
        this.pl = pl;
    }
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        //Change gamemode
        Player player = e.getEntity();
        player.setGameMode(GameMode.SPECTATOR);
        //Declare winner, turn stuff off
        List<Player> alive = pl.getServer().getOnlinePlayers().stream().filter(p -> p.getGameMode()==GameMode.SURVIVAL).collect(Collectors.toList());
        if (alive.size() <= 1) {
            Main.setOn(false);
            Player winner = alive.get(0);
            pl.getServer().broadcastMessage(ChatColor.GOLD + winner.getDisplayName() + " has won!");
            PlayerDeathEvent.getHandlerList().unregister(pl);
        }
    }
}
