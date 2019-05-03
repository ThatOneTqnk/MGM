package me.tqnk.bw.game.gameparse;

import com.google.gson.JsonObject;
import me.tqnk.bw.game.gameschema.GameSchema;

public interface GameDeserializer {
    GameSchema convertToGameSchema(JsonObject whole);
}
