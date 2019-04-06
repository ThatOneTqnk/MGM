package me.tqnk.bw.modules.bw;

import lombok.Getter;
import lombok.Setter;
import me.tqnk.bw.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@Getter
public class BWUserData {
    private Player player;
    @Setter private Color playerColor;
    @Setter private int respawnTimer;
    private HashMap<Integer, ItemStack> idealItems;
    private BWShopIndex bwShopIndex;

    public BWUserData(Player player, int respawnTimer, Color playerColor) {
        this.player = player;
        this.respawnTimer = respawnTimer;
        this.playerColor = playerColor;
        this.bwShopIndex = new BWShopIndex();
        this.idealItems = new HashMap<>(idealDefaults());
    }

    public void updateIdealItems(int num, ItemStack item) {
        idealItems.put(num, item);
    }

    private static HashMap<Integer, ItemStack> idealDefaults() {
        HashMap<Integer, ItemStack> ideals = new HashMap<>();
        ideals.put(0, ItemUtil.createItem(Material.WOOD_SWORD, ChatColor.GRAY + "Wooden Sword"));
        return ideals;
    }

    public static void applyIdealItems(BWUserData userData) {
        if(userData == null) return;
        Player hostPlayer = userData.getPlayer();
        for (Map.Entry<Integer, ItemStack> entry : userData.idealItems.entrySet()) {
            int slot = entry.getKey();
            ItemStack item = entry.getValue();
            ItemUtil.setUnbreakable(item);
            hostPlayer.getInventory().setItem(slot, item);
        }
    }
}
