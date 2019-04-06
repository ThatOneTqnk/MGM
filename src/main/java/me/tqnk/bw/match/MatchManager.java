package me.tqnk.bw.match;

import lombok.Getter;
import me.tqnk.bw.MGM;
import me.tqnk.bw.map.*;
import me.tqnk.bw.util.ManageWorld;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Getter
public class MatchManager {
    private MapLibrary library;
    private MapLoader loader;
    private MapRotater rotater;
    private String nextMap = "";
    private Match currentMatch = null;
    public MatchManager() {
        library = new MapLibrary("maps");
        rotater = new MapRotater(library);
    }

    public void cycleNextMatch() {
        MapContainer nextMapContainer = null;
        nextMapContainer = (nextMap.isEmpty() ? rotater.grabNextUp() : library.getMapContainerByName(nextMap));
        if(nextMapContainer == null) return;
        loadNewMatch(nextMapContainer);
    }

    private void loadNewMatch(MapContainer mapContainer) {
        UUID matchUuid = UUID.randomUUID();

        try {
            FileUtils.copyDirectory(mapContainer.getRawFolder(), new File("matches/" + matchUuid.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        WorldCreator worldCreator = new WorldCreator("matches/" + matchUuid.toString());
        worldCreator.generator(new NullChunkGenerator());
        World world = worldCreator.createWorld();
        world.setAutoSave(false);
        ManageWorld.setDefaultRules(world);

        if(currentMatch != null) currentMatch.unload();


        Match match = new Match(matchUuid, world, mapContainer, null);
        Match oldMatch = currentMatch;
        currentMatch = match;

        currentMatch.initWorldDependentContent();
        currentMatch.load();

        Location playerGoesTo = (currentMatch.getMap().getMetadata().getSpawnArea() == null ? world.getSpawnLocation() : currentMatch.getMap().getMetadata().getSpawnArea());
        match.getHostWorld().setSpawnLocation(playerGoesTo);

        Bukkit.getOnlinePlayers().forEach(player -> player.teleport(playerGoesTo, PlayerTeleportEvent.TeleportCause.PLUGIN));

        if(oldMatch != null) {
            MGM.get().getLogger().info("Attempting to unload and delete match " + oldMatch.getIdentity().toString());
            Bukkit.unloadWorld(oldMatch.getHostWorld(), false);
            ManageWorld.deleteWorld(oldMatch.getHostWorld().getWorldFolder());
        }
    }
}
