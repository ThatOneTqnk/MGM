package me.tqnk.bw.modules.bw;

import lombok.Getter;
import me.tqnk.bw.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BWShopIndex {
    private int pickaxeLevel = 0;
    private int axeLevel = 0;
    private int armorTier = 0;

    private static HashMap<String, String> shopLores = new HashMap<String, String>() {{
        put("gold", ChatColor.GOLD + "Cost: % " + ChatColor.YELLOW + "gold");
        put("iron", ChatColor.WHITE + "Cost: % " + ChatColor.GRAY + "iron");
        put("emerald", ChatColor.DARK_GREEN + "Cost: % " + ChatColor.GREEN + "emerald#");
        put("diamond", ChatColor.DARK_AQUA + "Cost: % " + ChatColor.AQUA + "diamond#");
    }};
    @Getter private HashMap<Integer, BWShopItem> shopIndex = new HashMap<>();

    public BWShopIndex() {
        populateDefaults(this.shopIndex);
    }

    private static void populateDefaults(HashMap<Integer, BWShopItem> shopIndexMutate) {
        shopIndexMutate.put(20, new BWShopItem(ItemUtil.createItem(Material.WOOD, ChatColor.YELLOW + "Wood", Arrays.asList(ChatColor.GRAY + "@ wood"), 4), new ItemStack(Material.GOLD_INGOT, 8), shopLores.get("gold")));
    }

    public void applyIndexToInventory(Inventory epic, int currentPage) {
        int upperBound = 53 + (currentPage * 54);
        int lowerBound = currentPage * 54;
        for(Map.Entry<Integer, BWShopItem> entry : shopIndex.entrySet()) {
            int theKey = entry.getKey();
            if(theKey < lowerBound || theKey > upperBound) continue;
            epic.setItem(theKey % 54, entry.getValue().getItem());
        }
    }
}
