package me.tqnk.bw.map;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MapInfoDeserializer implements JsonDeserializer<MapInfo> {
    @Override
    public MapInfo deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject rawData = jsonElement.getAsJsonObject();
        String name = rawData.get("name").getAsString();
        List<String> authors = new ArrayList<>();
        for(JsonElement authorJson : rawData.getAsJsonArray("authors")) authors.add(authorJson.getAsString());
        return new MapInfo(name, authors, null, jsonElement);
    }
}
