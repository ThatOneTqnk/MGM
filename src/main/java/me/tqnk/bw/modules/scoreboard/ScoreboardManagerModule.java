package me.tqnk.bw.modules.scoreboard;

import lombok.Getter;
import me.tqnk.bw.game.MatchModule;
import me.tqnk.bw.match.Match;
import me.tqnk.bw.match.ModuleData;
import me.tqnk.bw.match.ModuleLoadTime;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

@ModuleData(load = ModuleLoadTime.EARLIEST)
public class ScoreboardManagerModule extends MatchModule {
    @Getter private Scoreboard newSB;
    @Override
    public void load(Match match) {
        newSB = Bukkit.getScoreboardManager().getNewScoreboard();
    }

    public void addNewTeam(String teamName, ChatColor teamColor, boolean defaultRules) {
        newSB.registerNewTeam(teamName);
        Team candidate = newSB.getTeam(teamName);
        candidate.setPrefix(teamColor.toString());
        if(defaultRules) {
            candidate.setAllowFriendlyFire(false);
            candidate.setCanSeeFriendlyInvisibles(false);
            candidate.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        }
    }

    public void removeTeam(String teamName) {
        Team candidate = newSB.getTeam(teamName);
        if(candidate == null) return;
        candidate.unregister();
    }

    public void addToTeam(Player p, String teamName) {
        Team candidate = newSB.getTeam(teamName);
        if(candidate == null) return;
        candidate.addEntry(p.getName());
    }

    public void removeFromTeam(Player p, String teamName) {
        Team candidate = newSB.getTeam(teamName);
        if(candidate == null) return;
        for(String playerName : candidate.getEntries()) if(playerName.equalsIgnoreCase(p.getName())) {
            candidate.removeEntry(playerName);
            return;
        }
    }
}
