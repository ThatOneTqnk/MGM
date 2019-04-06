package me.tqnk.bw.user;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PlayerManager {
    private List<PlayerContext> playerContexts;
    public PlayerManager() {
        playerContexts = new ArrayList<>();
    }
    public PlayerContext getPlayerContext(Player p) {
        for(PlayerContext ctx : playerContexts) {
            if(ctx.getHost().equals(p)) return ctx;
        }
        return null;
    }
    public void addPlayerContext(Player p) {
        if(!playerContextExists(p)) playerContexts.add(new PlayerContext(p));
    }
    public void removePlayerContext(Player p) {
        Iterator<PlayerContext> iter = playerContexts.iterator();
        while(iter.hasNext()) {
            PlayerContext ctx = iter.next();
            if(ctx.getHost().equals(p)) iter.remove();
        }
    }
    private boolean playerContextExists(Player p) {
        for(PlayerContext ctx : playerContexts) if(ctx.getHost().equals(p)) return true;
        return false;
    }
}
