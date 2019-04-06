package me.tqnk.bw.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.tqnk.bw.game.GameType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@AllArgsConstructor @Getter
public class MatchQueueRequestEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Player joiner;
    private GameType gameType;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
