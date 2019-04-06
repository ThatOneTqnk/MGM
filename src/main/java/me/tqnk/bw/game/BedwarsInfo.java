package me.tqnk.bw.game;

import me.tqnk.bw.modules.bw.BedwarsModule;
import org.bukkit.Location;

import java.util.List;

public class BedwarsInfo extends GameInfo {

    public BedwarsInfo(Location spawnArea) {
        super(spawnArea);
        this.gameType = GameType.BEDWARS;
        this.seconds = 5;
        this.minQueued = 1;
    }

    @Override
    public Location getSpawnArea() {
        return spawnArea;
    }

    @Override
    public void setSpawnArea(Location spawnArea) {
        this.spawnArea = spawnArea;
    }

    @Override
    public List<MatchModule> getModules() {
        return this.modules;
    }

    @Override
    public void registerModules() {
        this.modules.add(new BedwarsModule());
    }

}
