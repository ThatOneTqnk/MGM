package me.tqnk.bw.game;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.tqnk.bw.modules.bw.BedwarsModule;
import me.tqnk.bw.modules.death.DeathModule;
import org.bukkit.Location;

import java.util.List;

public class BedwarsInfo extends GameInfo {

    public BedwarsInfo() {
        this.gameType = GameType.BEDWARS;
        this.seconds = 5;
        this.minQueued = 1;
    }

    @Override
    public List<MatchModule> getModules() {
        return this.modules;
    }

    @Override
    public void registerModules() {
        this.modules.add(new DeathModule());
        this.modules.add(new BedwarsModule());
    }


}
