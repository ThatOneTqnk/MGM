package me.tqnk.bw.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.tqnk.bw.match.Match;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@AllArgsConstructor @Getter
public class MatchStartEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Match hostMatch;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
