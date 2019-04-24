package me.tqnk.bw.game;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.tqnk.bw.modules.CountdownModule;
import me.tqnk.bw.modules.periodical.PeriodicalModule;
import me.tqnk.bw.modules.scoreboard.ScoreboardManagerModule;
import me.tqnk.bw.modules.team.MatchTeam;
import me.tqnk.bw.modules.team.TeamManagerModule;
import me.tqnk.bw.util.Parser;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class GameInfo {
    // default countdown timer
    int seconds = 20;
    int minQueued = 2;

    Location spawnArea;
    List<MatchModule> modules = new ArrayList<>();
    GameType gameType;
    // TODO move these into match container lol
    List<Player> queuedPlayers = new ArrayList<>();
    List<MatchTeam> teams = new ArrayList<>();

    public GameInfo(Location spawnArea) {
        this.spawnArea = spawnArea;
    }

    public GameType getGameType() {
        return gameType;
    }
    public abstract Location getSpawnArea();
    public abstract void setSpawnArea(Location spawnArea);
    public abstract List<MatchModule> getModules();
    public abstract void registerModules();
    public List<Player> getQueuedPlayers() { return queuedPlayers; }
    public List<MatchTeam> getTeams() { return teams; }
    public int getMinQueued() { return minQueued; }
    public void addPlayerToQueue(Player p) {
        queuedPlayers.add(p);
    }
    public void registerCoreModules() {
        modules.add(new PeriodicalModule());
        modules.add(new ScoreboardManagerModule());
        modules.add(new TeamManagerModule());
        modules.add(new CountdownModule());
    }
    public void additionalCoreParsing(JsonElement elem, World world) {
        // TODO move this into the match container, teams shouldn't be part of GameInfo...
        JsonObject rawData = elem.getAsJsonObject();
        if(rawData.has("teams")) {
            for(JsonElement teamElement : rawData.getAsJsonArray("teams")) {
                JsonObject teamJson = teamElement.getAsJsonObject();
                String teamId = teamJson.get("id").getAsString();
                String teamDisplayName = teamJson.get("name").getAsString();
                ChatColor teamChatColor = ChatColor.valueOf(teamJson.get("color").getAsString().toUpperCase().replace(" ", "_"));
                Location teamSpawnArea = Parser.convertLocation(world, teamJson.get("spawnarea"));
                teams.add(new MatchTeam(teamChatColor, teamSpawnArea, teamDisplayName, teamId));
            }
        }
    }

    public int getSeconds() {return seconds;}
}