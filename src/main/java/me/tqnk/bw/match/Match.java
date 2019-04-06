package me.tqnk.bw.match;

import lombok.Getter;
import lombok.Setter;
import me.tqnk.bw.MGM;
import me.tqnk.bw.game.GameInfo;
import me.tqnk.bw.game.MatchModule;
import me.tqnk.bw.map.MapContainer;
import me.tqnk.bw.status.GameStatus;
import me.tqnk.bw.util.Parser;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Match {
    private UUID identity;
    private World hostWorld;
    private MapContainer map;
    private GameInfo matchInfo;
    @Setter private GameStatus status;

    public Match(UUID identity, World hostWorld, MapContainer map, GameInfo matchInfo) {
        this.identity = identity;
        this.hostWorld = hostWorld;
        this.map = map;
        this.matchInfo = matchInfo;
        this.status = GameStatus.PRE;
        matchInfo.additionalCoreParsing(map.getMetadata().getRawJson(), hostWorld);
        matchInfo.registerCoreModules();
        matchInfo.registerModules();
    }

    public void load() {
        int listenerCount = 0;
        for (ModuleLoadTime moduleLoadTime : ModuleLoadTime.values()) {
            for (MatchModule matchModule : getModules(moduleLoadTime)) {
                try {
                    matchModule.load(this);
                } catch (Exception e) {
                    e.printStackTrace();
                    Bukkit.getLogger().warning("[JSON] Failed to parse module: " + matchModule.getClass().getSimpleName());
                    try {
                        matchModule.unload(this);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                if (matchModule instanceof Listener) {
                    MGM.registerEvents((Listener) matchModule);
                    listenerCount++;
                }
            }
        }

        Bukkit.getLogger().info("Loaded " + this.getMatchInfo().getModules().size() + " modules (" + listenerCount + " listeners)");
    }
    public void unload() {

    }
    public void initWorldDependentContent() {
        matchInfo.setSpawnArea(Parser.convertLocation(hostWorld, map.getMetadata().getRawJson().getAsJsonObject().get("spawnarea")));
    }

    @SuppressWarnings("unchecked")
    public <T extends MatchModule> T getModule(Class<T> clazz) {
        for (MatchModule module : matchInfo.getModules()) {
            if (clazz.isInstance(module)) return ((T) module);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T extends MatchModule> List<T> getModules(Class<T> clazz) {
        List<T> results = new ArrayList<>();
        for (MatchModule module : matchInfo.getModules()) {
            if (clazz.isInstance(module)) results.add((T) module);
        }
        return results;
    }

    public List<MatchModule> getModules(ModuleLoadTime moduleLoadTime) {
        List<MatchModule> selected = new ArrayList<>();
        for (MatchModule matchModule : this.getMatchInfo().getModules()) {
            if (matchModule.getClass().isAnnotationPresent(ModuleData.class)) {
                if (matchModule.getClass().getAnnotation(ModuleData.class).load() == moduleLoadTime) {
                    selected.add(matchModule);
                }
            } else if (moduleLoadTime == ModuleLoadTime.NORMAL) {
                selected.add(matchModule);
            }
        }
        return selected;
    }
}
