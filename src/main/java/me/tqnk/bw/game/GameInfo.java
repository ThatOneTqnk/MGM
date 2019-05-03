package me.tqnk.bw.game;

import com.google.gson.JsonElement;
import me.tqnk.bw.modules.CountdownModule;
import me.tqnk.bw.modules.periodical.PeriodicalModule;
import me.tqnk.bw.modules.scoreboard.ScoreboardManagerModule;
import me.tqnk.bw.modules.team.TeamManagerModule;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public abstract class GameInfo {
    // default countdown timer
    int seconds = 20;
    int minQueued = 2;

    List<MatchModule> modules = new ArrayList<>();
    GameType gameType;

    public GameInfo() {}

    public GameType getGameType() {
        return gameType;
    }
    public abstract List<MatchModule> getModules();
    public abstract void registerModules();
    public int getMinQueued() { return minQueued; }
    public void registerCoreModules() {
        modules.add(new PeriodicalModule());
        modules.add(new ScoreboardManagerModule());
        modules.add(new TeamManagerModule());
        modules.add(new CountdownModule());
    }

    public int getSeconds() {return seconds;}
}