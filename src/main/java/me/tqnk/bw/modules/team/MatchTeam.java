package me.tqnk.bw.modules.team;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MatchTeam {
    @Getter private ChatColor chatTeamColor;
    @Getter private List<Player> players;
    @Getter private Location spawnArea;
    @Getter private String displayName;
    @Getter private String teamID;
    @Getter @Setter private String sbTeamHookID;
    public MatchTeam(ChatColor chatTeamColor, Location spawnArea, String displayName, String teamID) {
        players = new ArrayList<>();
        this.chatTeamColor = chatTeamColor;
        this.spawnArea = spawnArea;
        this.displayName = displayName;
        this.teamID = teamID;
    }
    public void add(Player p) {
        if(!players.contains(p)) players.add(p);
    }

    public void remove(Player p) {
        if(players.contains(p)) players.remove(p);
    }

}
