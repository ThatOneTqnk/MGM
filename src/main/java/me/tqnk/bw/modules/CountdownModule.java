package me.tqnk.bw.modules;

import me.tqnk.bw.events.MatchStartEvent;
import me.tqnk.bw.game.MatchModule;
import me.tqnk.bw.match.Match;
import me.tqnk.bw.modules.periodical.Periodical;
import me.tqnk.bw.status.GameStatus;
import me.tqnk.bw.util.MatchUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class CountdownModule extends MatchModule implements Periodical {
    private int time = -1;
    private Match match;
    @Override
    public void load(Match match) {
        this.match = match;
    }

    public void setupTimer() {
        this.time = match.getMatchInfo().getSeconds() * 20;
    }

    @Override
    public void tick() {
        if(time >= 0) {
            if(time % 20 == 0 && (((time / 20) % 5 == 0) || ((time / 20) <= 5))) broadcastSecond();
            time--;
        }
    }

    private void broadcastSecond() {
        if(time > 0) {
            MatchUtil.sendToQueued(this.match, ChatColor.GREEN + "Match " + ChatColor.GRAY + "starting in " + ChatColor.GOLD.toString() + (time / 20) + ChatColor.YELLOW.toString() + " second" + ((time > 20) ? "s" : "") + "!");
            if(time <= 100) {
                ChatColor[] colorFade = {ChatColor.RED, ChatColor.AQUA, ChatColor.YELLOW, ChatColor.GOLD, ChatColor.GREEN};
                int index = (time / 20) - 1;
                if(index >= 0) for(Player p : this.match.getMatchInfo().getQueuedPlayers()) p.sendTitle("", colorFade[index] + "" + (index + 1), 2, 15, 2);
            }
            MatchUtil.playToQueued(this.match, Sound.BLOCK_NOTE_HAT, 1F);
        } else {
            match.setStatus(GameStatus.MID);
            Bukkit.getPluginManager().callEvent(new MatchStartEvent(this.match));
        }
    }
}
