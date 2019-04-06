package me.tqnk.bw.flow;

import me.tqnk.bw.MGM;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class FlowManager implements Listener {
    public FlowManager() {
        MGM.registerEvents(this);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        MGM.get().getPlayerManager().addPlayerContext(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        MGM.get().getPlayerManager().removePlayerContext(event.getPlayer());
    }
}
