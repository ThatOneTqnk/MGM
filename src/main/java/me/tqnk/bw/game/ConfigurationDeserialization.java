package me.tqnk.bw.game;

import com.google.gson.*;
import me.tqnk.bw.game.gameschema.GameSchema;

import java.lang.reflect.Type;

public class ConfigurationDeserialization implements JsonDeserializer<MGMConfiguration> {
    @Override
    public MGMConfiguration deserialize(JsonElement elem, Type type, JsonDeserializationContext ctx) {
        JsonObject whole = elem.getAsJsonObject();
        if(whole.has("match_configs")) {
            JsonArray matchConfigArr = whole.getAsJsonArray("match_configs");
            for(JsonElement matchConfig : matchConfigArr) {
                JsonObject parsedMatchConfig = matchConfig.getAsJsonObject();
                String configId = parsedMatchConfig.get("id").getAsString();
                String gameType = parsedMatchConfig.get("gametype").getAsString();
                boolean isDefault = false;
                if(parsedMatchConfig.has("default")) isDefault = parsedMatchConfig.get("default").getAsBoolean();
                GameType result = null;
                for(GameType candidate : GameType.values()) if(candidate.getTechnicalName().equalsIgnoreCase(gameType)) result = candidate;
                if(result != null) {
                    GameSchema resultingSchema = result.getDeserializer().convertToGameSchema(parsedMatchConfig);
                    result.setSchema(resultingSchema);
                }
            }
        }
        return new MGMConfiguration(elem);
    }
}
