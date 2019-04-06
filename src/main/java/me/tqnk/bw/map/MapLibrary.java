package me.tqnk.bw.map;

import com.google.gson.stream.JsonReader;
import lombok.Getter;
import me.tqnk.bw.MGM;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class MapLibrary {
    private File sourceDest;
    @Getter private List<MapContainer> mapContainers;
    public MapLibrary(String sourceDest) {
        this.sourceDest = new File(sourceDest);
        mapContainers = refreshLibrary(this.sourceDest);
    }

    public List<MapContainer> refreshLibrary(File rawMapsFolder) {

        List<MapContainer> maps = new ArrayList<>();
        File[] rawMaps = rawMapsFolder.listFiles();
        if(rawMaps != null) {
            for (File rawMap : rawMaps) {
                if (rawMap.isDirectory()) {
                    if (containsProperData(rawMap)) {
                        File mapJsonFile = new File(rawMap, "map.json");
                        try {
                            JsonReader reader = new JsonReader(new FileReader(mapJsonFile));
                            MapInfo mapInfo = MGM.get().getGson().fromJson(reader, MapInfo.class);
                            maps.add(new MapContainer(rawMap, mapInfo));
                            MGM.get().getLogger().info("Loaded map " + rawMap.getName());
                        } catch (Exception e) {
                            MGM.get().getLogger().warning("Failed to load map " + rawMap.getName());
                            e.printStackTrace();
                        }
                    } else {
                        for (MapContainer mapContainer : refreshLibrary(rawMap)) {
                            maps.add(mapContainer);
                        }
                    }
                }
            }
            return maps;
        } else {
            Bukkit.getLogger().info("Source Destination is invalid");
            return null;
        }
    }

    private boolean containsProperData(File folder) {
        File[] files = folder.listFiles();
        if(files == null) return false;
        for(File file : files) if(file.getName().equals("map.json")) return true;
        return false;
    }

    public MapContainer getMapContainerByName(String mapName) {
        for(MapContainer map : mapContainers) if(map.getMetadata().getName().equalsIgnoreCase(mapName)) return map;
        return null;
    }
}
