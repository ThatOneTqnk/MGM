package me.tqnk.bw.game.gameparse;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.tqnk.bw.game.gameschema.BedwarsSchema;
import me.tqnk.bw.game.gameschema.GameSchema;
import me.tqnk.bw.models.BedwarsShop;
import me.tqnk.bw.models.BedwarsShopItem;
import me.tqnk.bw.models.BedwarsShopItemResident;
import me.tqnk.bw.util.ItemUtil;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BedwarsGameDeserializer implements GameDeserializer {
    @Override
    public GameSchema convertToGameSchema(JsonObject whole) {
        BedwarsShop theShop = null;
        if(whole.has("shop")) {
            JsonObject wholeShop = whole.getAsJsonObject("shop");
            String shopTitle = "";
            if(wholeShop.has("title")) shopTitle = wholeShop.get("title").getAsString();

            JsonArray categories = wholeShop.get("categories").getAsJsonArray();
            List<ItemStack> catItems = new ArrayList<>();
            for(JsonElement elem : categories) {
                JsonObject parsedCategoryItem = elem.getAsJsonObject();
                ItemStack parsedCatItemStack = ItemUtil.createItem(parsedCategoryItem);
                catItems.add(parsedCatItemStack);
            }
            JsonArray shopItems = wholeShop.get("shopitems").getAsJsonArray();
            List<BedwarsShopItem> bedwarsShopItems = new ArrayList<>();
            for(JsonElement elem : shopItems) {
                JsonObject containerShopObj = elem.getAsJsonObject();
                JsonObject realItemObj = containerShopObj.get("item").getAsJsonObject();
                ItemStack realItem = ItemUtil.createItem(realItemObj);
                ItemStack itemCost = null;
                if(containerShopObj.has("costtype")) itemCost = ItemUtil.createItem(containerShopObj.get("costtype").getAsJsonObject());
                List<BedwarsShopItemResident> bedwarsShopItemLocs = new ArrayList<>();
                if(containerShopObj.has("resides")) {
                    JsonArray residents = containerShopObj.get("resides").getAsJsonArray();
                    for(JsonElement elem2 : residents) bedwarsShopItemLocs.add(new BedwarsShopItemResident(elem2.getAsJsonObject().get("category").getAsInt(), elem2.getAsJsonObject().get("slot").getAsInt()));
                }
                bedwarsShopItems.add(new BedwarsShopItem(realItem, itemCost, bedwarsShopItemLocs));
            }
            theShop = new BedwarsShop(shopTitle, catItems, bedwarsShopItems);
        }
        return new BedwarsSchema(whole, theShop);
    }
}
