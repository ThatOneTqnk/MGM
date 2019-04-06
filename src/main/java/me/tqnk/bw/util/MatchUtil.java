package me.tqnk.bw.util;

import me.tqnk.bw.MGM;
import me.tqnk.bw.match.Match;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class MatchUtil {
    public static void sendToQueued(Match match, String msg) {
        for(Player player : match.getMatchInfo().getQueuedPlayers()) {
            if(player.isOnline()) {
                player.sendMessage(msg);
            }
        }
    }
    public static void playToQueued(Match match, Sound sound, float pitch) {
        for(Player player : match.getMatchInfo().getQueuedPlayers()) {
            if(player.isOnline()) {
                player.playSound(player.getLocation().clone().add(0.0, 100.0, 0.0), sound, 1000, pitch);
            }
        }
    }

    public static boolean determineMatchCorrespondence(Player p, Match match) {
        return (MGM.get().getPlayerManager().getPlayerContext(p).getInGame() == match);
    }

}
