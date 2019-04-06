package me.tqnk.bw.game;

public enum GameType {
    BEDWARS("Bedwars");
    private String formattedName;
    GameType(String formattedName) {
        this.formattedName = formattedName;
    }

    public String getFormattedName() {
        return this.formattedName;
    }
}
