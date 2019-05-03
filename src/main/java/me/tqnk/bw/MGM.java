package me.tqnk.bw;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import lombok.Getter;
import me.tqnk.bw.command.BwCommand;
import me.tqnk.bw.command.CommandHandler;
import me.tqnk.bw.flow.FlowManager;
import me.tqnk.bw.game.ConfigurationDeserialization;
import me.tqnk.bw.game.MGMConfiguration;
import me.tqnk.bw.map.MapInfo;
import me.tqnk.bw.map.MapInfoDeserializer;
import me.tqnk.bw.match.MatchRuntimeManager;
import me.tqnk.bw.traffic.TrafficManager;
import me.tqnk.bw.user.PlayerManager;
import me.tqnk.bw.util.ManageWorld;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class MGM extends JavaPlugin {
    @Getter private Gson gson;
    @Getter private MatchRuntimeManager matchRuntimeManager;
    @Getter private CommandHandler commandHandler;
    @Getter private TrafficManager trafficManager;
    @Getter private PlayerManager playerManager;
    @Getter private FlowManager flowManager;
    @Getter private ProtocolManager protocolManager;
    @Getter private MGMConfiguration customConfiguration;

    private static MGM instance;

    public static MGM get() {
        return instance;
    }



    @Override
    public void onEnable() {
        instance = this;

        // custom deserializer for MapInfo and MGMConfiguration
        gson = new GsonBuilder().registerTypeAdapter(MapInfo.class, new MapInfoDeserializer()).registerTypeAdapter(MGMConfiguration.class, new ConfigurationDeserialization()).create();

        obtainConfig();
        ManageWorld.deleteAllMatchWorlds();

        trafficManager = new TrafficManager();
        playerManager = new PlayerManager();
        protocolManager = ProtocolLibrary.getProtocolManager();
        flowManager = new FlowManager();

        commandHandler = new CommandHandler(this);
        commandHandler.add(new BwCommand());

        matchRuntimeManager = new MatchRuntimeManager();
        matchRuntimeManager.initializeMatches(3);
    }

    private void obtainConfig() {
        File configuration = new File("plugins/" + getName() + "config.json");
        if(configuration.exists()) {
            try {
                JsonReader reader = new JsonReader(new FileReader(configuration));
                MGMConfiguration parsedConfig = gson.fromJson(reader, MGMConfiguration.class);
                if(parsedConfig != null) this.customConfiguration = parsedConfig;
            } catch (FileNotFoundException e) {
                Bukkit.getLogger().warning("No configuration file found");
            }
        } else Bukkit.getLogger().warning("No configuration file found");
    }

    public static void registerEvents(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, MGM.get());
    }

    public static void unregisterEvents(Listener listener) {
        HandlerList.unregisterAll(listener);
    }
}