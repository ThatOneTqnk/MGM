package me.tqnk.bw.map;

import lombok.Getter;
import me.tqnk.bw.MGM;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MapRotater {
    @Getter private List<MapContainer> activeMaps;
    private MapLibrary library;
    private int counter;
    public MapRotater(MapLibrary library) {
        this.library = library;
        counter = 0;
        activeMaps = new ArrayList<>();
        initializeActiveMaps();
    }

    private void initializeActiveMaps() {
        String rotationFileLoc = MGM.get().getConfig().getConfigurationSection("maps").getString("rotation");
        File rotationFile = new File(rotationFileLoc);
        Scanner reader = null;
        try {
            reader = new Scanner(rotationFile);
        } catch(FileNotFoundException e) {
            Bukkit.getLogger().info("Rotation file not found");
        }
        if(reader == null) return;
        while(reader.hasNextLine()) {
            MapContainer container = library.getMapContainerByName(reader.nextLine());
            if(container != null) activeMaps.add(container);
        }
    }

    public MapContainer grabNextUp() {
        if(counter >= activeMaps.size()) counter = 0;
        MapContainer nextUp = activeMaps.get(counter);
        counter++;
        return nextUp;
    }
}
