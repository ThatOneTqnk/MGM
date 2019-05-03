package me.tqnk.bw.game;

import me.tqnk.bw.game.gameparse.BedwarsGameDeserializer;
import me.tqnk.bw.game.gameparse.GameDeserializer;
import me.tqnk.bw.game.gameschema.GameSchema;

public enum GameType {
    BEDWARS("Bedwars", "bedwars", new BedwarsGameDeserializer(), BedwarsInfo.class);
    private String formattedName;
    private String technicalName;
    private Class hostClass;
    private GameSchema schema = null;
    private GameDeserializer deserializer;
    GameType(String formattedName, String technicalName, GameDeserializer deserializer, Class hostClass) {
        this.formattedName = formattedName;
        this.technicalName = technicalName;
        this.hostClass = hostClass;
        this.deserializer = deserializer;
    }

    public String getFormattedName() {
        return this.formattedName;
    }
    public String getTechnicalName() { return this.technicalName; }
    public Class getHostClass() { return this.hostClass; }
    public GameDeserializer getDeserializer() { return this.deserializer; }
    public GameSchema getSchema() { return this.schema; }
    public void setSchema(GameSchema schema) { this.schema = schema; }
}
