package me.tqnk.bw.game.gameschema;

import com.google.gson.JsonObject;
import lombok.Getter;
import me.tqnk.bw.models.BedwarsShop;

@Getter
public class BedwarsSchema extends GameSchema {
    private BedwarsShop shop;
    public BedwarsSchema(JsonObject whole, BedwarsShop shop) {
        super(whole);
        this.shop = shop;
    }
}
