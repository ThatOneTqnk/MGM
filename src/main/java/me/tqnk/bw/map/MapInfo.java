package me.tqnk.bw.map;

import com.google.gson.JsonElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.List;

@AllArgsConstructor @Getter
public class MapInfo {
    private String name;
    private List<String> authors;
    @Setter private Location spawnArea;
    private JsonElement rawJson;
}
