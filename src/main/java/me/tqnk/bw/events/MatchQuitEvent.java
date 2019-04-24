package me.tqnk.bw.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.tqnk.bw.user.PlayerContext;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@AllArgsConstructor @Getter
public class MatchQuitEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private PlayerContext leaver;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
