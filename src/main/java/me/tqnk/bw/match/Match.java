package me.tqnk.bw.match;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import me.tqnk.bw.MGM;
import me.tqnk.bw.game.GameInfo;
import me.tqnk.bw.game.GameType;
import me.tqnk.bw.game.MatchModule;
import me.tqnk.bw.map.MapContainer;
import me.tqnk.bw.modules.team.MatchTeam;
import me.tqnk.bw.status.GameStatus;
import me.tqnk.bw.util.Parser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.lang.reflect.InvocationTargetException;
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
    private List<MatchTeam> teams;
    private List<Player> queuedPlayers;
    private Location spawnArea;

    public Match(UUID identity, World hostWorld, MapContainer map) {
        this.identity = identity;
        this.hostWorld = hostWorld;
        this.map = map;
        this.status = GameStatus.PRE;
        this.teams = new ArrayList<>();
        this.queuedPlayers = new ArrayList<>();
        loadMatchJson();
        determineGameType();
        this.matchInfo.registerCoreModules();
        this.matchInfo.registerModules();
    }

    private void determineGameType() {
        JsonObject rawData = map.getMetadata().getRawJson().getAsJsonObject();
        GameType valid = null;
        if(rawData.has("game")) {
            JsonObject rawDataGame = rawData.get("game").getAsJsonObject();
            if(rawDataGame.has("gametype")) {
                String cand = rawDataGame.get("gametype").getAsString();
                for(GameType gameType : GameType.values()) {
                    if(gameType.getTechnicalName().equalsIgnoreCase(cand)) {
                        valid = gameType;
                        break;
                    }
                }
            }
        }
        // default to bedwars for now
        if(valid == null) {
            valid = GameType.BEDWARS;
            Bukkit.getLogger().info("No gametype was declared, defaulted to BEDWARS");
        }
        try {
            this.matchInfo = ((GameInfo) valid.getHostClass().getConstructors()[0].newInstance());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    // Loads Match scope JSON
    private void loadMatchJson() {
        JsonObject rawData = map.getMetadata().getRawJson().getAsJsonObject();
        if(rawData.has("teams")) {
            for(JsonElement teamElement : rawData.getAsJsonArray("teams")) {
                JsonObject teamJson = teamElement.getAsJsonObject();
                String teamId = teamJson.get("id").getAsString();
                String teamDisplayName = teamJson.get("name").getAsString();
                ChatColor teamChatColor = ChatColor.valueOf(teamJson.get("color").getAsString().toUpperCase().replace(" ", "_"));
                Location teamSpawnArea = Parser.convertLocation(hostWorld, teamJson.get("spawnarea"));
                teams.add(new MatchTeam(teamChatColor, teamSpawnArea, teamDisplayName, teamId));
            }
        }
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

    public void end() {
        this.status = GameStatus.POST;
    }

    public void unload() {

    }
    public void initWorldDependentContent() {
        this.spawnArea = Parser.convertLocation(hostWorld, map.getMetadata().getRawJson().getAsJsonObject().get("spawnarea"));
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
