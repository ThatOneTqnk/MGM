package me.tqnk.bw;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import me.tqnk.bw.command.BwCommand;
import me.tqnk.bw.command.CommandHandler;
import me.tqnk.bw.flow.FlowManager;
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

public class MGM extends JavaPlugin {
    @Getter private Gson gson;
    @Getter private MatchRuntimeManager matchRuntimeManager;
    @Getter private CommandHandler commandHandler;
    @Getter private TrafficManager trafficManager;
    @Getter private PlayerManager playerManager;
    @Getter private FlowManager flowManager;
    @Getter private ProtocolManager protocolManager;

    private static MGM instance;

    public static MGM get() {
        return instance;
    }



    @Override
    public void onEnable() {
        instance = this;
        FileConfiguration fileConfiguration = getConfig();
        saveDefaultConfig();

        ConfigurationSection mapPart = fileConfiguration.getConfigurationSection("maps");

        // custom deserializer for mapinfo
        gson = new GsonBuilder().registerTypeAdapter(MapInfo.class, new MapInfoDeserializer()).create();
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

    public static void registerEvents(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, MGM.get());
    }

    public static void unregisterEvents(Listener listener) {
        HandlerList.unregisterAll(listener);
    }
}