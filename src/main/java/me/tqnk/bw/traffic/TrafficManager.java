package me.tqnk.bw.traffic;

import lombok.Getter;
import me.tqnk.bw.MGM;
import me.tqnk.bw.events.MatchQueueRequestEvent;
import me.tqnk.bw.events.MatchQuitEvent;
import me.tqnk.bw.events.MatchStartEvent;
import me.tqnk.bw.game.GameType;
import me.tqnk.bw.game.MatchModule;
import me.tqnk.bw.match.Match;
import me.tqnk.bw.modules.CountdownModule;
import me.tqnk.bw.status.GameStatus;
import me.tqnk.bw.user.PlayerContext;
import me.tqnk.bw.util.PacketAPI;
import me.tqnk.bw.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.List;

public class TrafficManager implements Listener {
    /*
        TrafficManager handles events on behalf of multiple matches
        and emits custom events
     */
    @Getter private int runnableId = -1;

    public TrafficManager() {
        MGM.registerEvents(this);
        registerTimers();
    }
    private void registerTimers() {
        runnableId = Bukkit.getScheduler().scheduleSyncRepeatingTask(MGM.get(), () -> {
            for(Player ply : Bukkit.getOnlinePlayers()) handleTabDetermination(ply);
        }, 10L, 10L);
    }
    private void handleTabDetermination(Player p) {
        String aggregate = ChatColor.GOLD + "\nCougarMC\n" + ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "---------------\n";
        PlayerContext ctx = MGM.get().getPlayerManager().getPlayerContext(p);
        String addon = ChatColor.GRAY + "Welcome " + ChatColor.GREEN + p.getDisplayName() + ChatColor.GREEN + "!";
        if(ctx != null && ctx.getInGame() != null) addon = ChatColor.GREEN.toString()
                + ctx.getInGame().getMatchInfo().getGameType().getFormattedName() + " " + ChatColor.DARK_GRAY +
                "- " + ChatColor.GOLD + "Match " + ChatColor.YELLOW.toString()
                + MGM.get().getMatchRuntimeManager().getIdByMatchUuid(ctx.getInGame().getIdentity());
        aggregate = aggregate + addon + "\n";
        PacketAPI.sendTablistHeaderFooter(p, aggregate, "\n:)\n");
    }

    @EventHandler
    public void onMatchRequest(MatchQueueRequestEvent event) {
        Player target = event.getJoiner();
        if(target == null) return;
        PlayerContext ctx = MGM.get().getPlayerManager().getPlayerContext(target);
        if(ctx == null) return;
        if(ctx.getInGame() != null) {
            target.sendMessage(ChatColor.RED + "You cannot join a game if you are already in one!");
            return;
        }
        Match favorable = determineFavorableMatch(event.getGameType());
        if(favorable == null) {
            target.sendMessage(ChatColor.RED + "No open " + ChatColor.GOLD + event.getGameType().getFormattedName() + ChatColor.RED + " matches!");
            return;
        }
        target.sendMessage(ChatColor.GRAY + "Sending you to " + ChatColor.GREEN + event.getGameType().getFormattedName() + ChatColor.GOLD + " Match " + MGM.get().getMatchRuntimeManager().getIdByMatchUuid(favorable.getIdentity()));
        addPlayerToQueue(favorable, target);
    }

    @EventHandler
    public void onMatchStart(MatchStartEvent event) {
        for(MatchModule module : event.getHostMatch().getMatchInfo().getModules()) module.start();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        PlayerContext ctx = MGM.get().getPlayerManager().getPlayerContext(event.getPlayer());
        if(ctx == null) return;
        Bukkit.getPluginManager().callEvent(new MatchQuitEvent(ctx));
    }

    private static void addPlayerToQueue(Match match, Player target) {
        MGM.get().getPlayerManager().getPlayerContext(target).setInGame(match);
        match.getQueuedPlayers().add(target);
        target.teleport(match.getSpawnArea(), PlayerTeleportEvent.TeleportCause.PLUGIN);
        PlayerUtil.generalReadyPlayer(target, GameMode.ADVENTURE);
        pingQueueCountdown(match);
    }

    private static void pingQueueCountdown(Match match) {
        if(match.getQueuedPlayers().size() >= match.getMatchInfo().getMinQueued() && match.getStatus() == GameStatus.PRE) {
            match.setStatus(GameStatus.QUEUE);
            match.getModule(CountdownModule.class).setupTimer();
        }
    }

    private static Match determineFavorableMatch(GameType gameType) {
        List<Match> matches = MGM.get().getMatchRuntimeManager().getAllMatches();
        int highest = -1;
        Match candidate = null;
        for(Match match : matches) {
            if(match == null) continue;
            int matchQueueSize = match.getQueuedPlayers().size();
            if(matchQueueSize > highest && (match.getStatus() == GameStatus.PRE || match.getStatus() == GameStatus.QUEUE) && match.getMatchInfo().getGameType() == gameType) {
                highest = matchQueueSize;
                candidate = match;
            }
        }
        return candidate;
    }

}
