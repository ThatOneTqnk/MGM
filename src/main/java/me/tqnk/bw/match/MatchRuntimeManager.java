package me.tqnk.bw.match;

import lombok.Getter;
import me.tqnk.bw.game.BedwarsInfo;
import me.tqnk.bw.map.MapContainer;
import me.tqnk.bw.map.MapLibrary;
import me.tqnk.bw.map.NullChunkGenerator;
import me.tqnk.bw.util.ManageWorld;
import org.apache.commons.io.FileUtils;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MatchRuntimeManager {
    @Getter private List<Match> allMatches;
    @Getter private MapLibrary library;
    public MatchRuntimeManager() {
        allMatches = new ArrayList<>();
        library = new MapLibrary("maps");
    }
    public void initializeMatches(int count) {
        for(int x = 1; x <= count; x++) {
            allMatches.add(spawnNewMatch());
        }
    }
    private Match spawnNewMatch() {
        HashMap<String, Integer> maps = new HashMap<>();
        for(MapContainer container : library.getMapContainers()) {
            if(container != null) {
                String mapName = container.getMetadata().getName();
                if(!maps.containsKey(mapName)) maps.put(mapName, 0);
                maps.put(mapName, maps.get(mapName) + 1);
            }
        }
        String mapName = null;
        int lowestValue = 999;
        for (Map.Entry<String, Integer> entry : maps.entrySet()) {
            String key = entry.getKey();
            int value = entry.getValue();
            if(value < lowestValue) {
                mapName = key;
                lowestValue = value;
            }
        }
        if(mapName == null) return null;

        UUID matchUuid = UUID.randomUUID();

        try {
            FileUtils.copyDirectory(library.getMapContainerByName(mapName).getRawFolder(), new File("matches/" + matchUuid.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        WorldCreator worldCreator = new WorldCreator("matches/" + matchUuid.toString());
        worldCreator.generator(new NullChunkGenerator());
        World world = worldCreator.createWorld();
        world.setAutoSave(false);
        ManageWorld.setDefaultRules(world);
        Match product = new Match(matchUuid, world, library.getMapContainerByName(mapName));
        product.initWorldDependentContent();
        product.load();
        return product;
    }
    public Match getMatchById(int index) {
        index--;
        return ((index < 0 || index >= allMatches.size()) ? null : allMatches.get(index));
    }
    public int getIdByMatchUuid(UUID matchUuid) {
        int index = 1;
        for(Match match : allMatches) {
            if(match.getIdentity().equals(matchUuid)) return index;
            index++;
        }
        return -1;
    }

}
