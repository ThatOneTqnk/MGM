package me.tqnk.bw.modules.bw;

import lombok.Getter;
import lombok.Setter;
import me.tqnk.bw.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.HashMap;
import java.util.Map;

@Getter
public class BWUserData {
    private Player player;
    @Setter private Color playerColor;
    @Setter private int respawnTimer;
    private BWUserLevel levelContainer;
    private HashMap<Integer, ItemStack> idealItems = new HashMap<>();
    private BWShopIndex bwShopIndex;

    public BWUserData(Player player, int respawnTimer, Color playerColor) {
        this.player = player;
        this.respawnTimer = respawnTimer;
        this.playerColor = playerColor;
        this.levelContainer = new BWUserLevel();
        this.bwShopIndex = new BWShopIndex(player, this.levelContainer);
        refreshIdealDefaults();
    }

    public void updateIdealItems(int num, ItemStack item) {
        idealItems.put(num, item);
    }

    public void refreshIdealDefaults() {
        this.idealItems.clear();
        staticIdealDefaults(this.idealItems);
        int swordLevel = levelContainer.getLevels().get(BWLevelable.BWLevel.SWORD);
        ItemStack swordReceive = ItemUtil.createItem(Material.WOOD_SWORD, ChatColor.GRAY + "Wooden Sword");
        if(swordLevel == 1) swordReceive = ItemUtil.createItem(Material.STONE_SWORD, ChatColor.GRAY + "Stone Sword");
        else if(swordLevel == 2) swordReceive = ItemUtil.createItem(Material.GOLD_SWORD, ChatColor.GRAY + "Golden Sword");
        else if(swordLevel == 3) swordReceive = ItemUtil.createItem(Material.IRON_SWORD, ChatColor.GRAY + "Iron Sword");
        this.idealItems.put(0, swordReceive);
        int armorLevel = levelContainer.getLevels().get(BWLevelable.BWLevel.ARMOR);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        if(armorLevel == 1) {
            chestplate = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
            leggings = new ItemStack(Material.CHAINMAIL_LEGGINGS);
            boots = new ItemStack(Material.CHAINMAIL_BOOTS);
        } else if(armorLevel == 2) {
            chestplate = new ItemStack(Material.IRON_CHESTPLATE);
            leggings = new ItemStack(Material.IRON_LEGGINGS);
            boots = new ItemStack(Material.IRON_BOOTS);
        } else if(armorLevel == 3) {
            chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
            leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
            boots = new ItemStack(Material.DIAMOND_BOOTS);
        } else if(armorLevel == 0) {
            LeatherArmorMeta meta2 = (LeatherArmorMeta) chestplate.getItemMeta();
            meta2.setColor(playerColor);
            chestplate.setItemMeta(meta2);

            LeatherArmorMeta meta3 = (LeatherArmorMeta) leggings.getItemMeta();
            meta3.setColor(playerColor);
            leggings.setItemMeta(meta3);

            LeatherArmorMeta meta4 = (LeatherArmorMeta) boots.getItemMeta();
            meta4.setColor(playerColor);
            boots.setItemMeta(meta4);
        }
        ItemStack leatherHelmet = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta meta = (LeatherArmorMeta) leatherHelmet.getItemMeta() ;
        meta.setColor(playerColor);
        leatherHelmet.setItemMeta(meta);
        this.idealItems.put(103, leatherHelmet);
        this.idealItems.put(102, chestplate);
        this.idealItems.put(101, leggings);
        this.idealItems.put(100, boots);
    }

    private static void staticIdealDefaults(HashMap<Integer, ItemStack> ideals) {}

    public static void applyIdealItems(BWUserData userData) {
        if(userData == null) return;
        Player hostPlayer = userData.getPlayer();
        for (Map.Entry<Integer, ItemStack> entry : userData.idealItems.entrySet()) {
            int slot = entry.getKey();
            ItemStack item = entry.getValue();
            ItemUtil.setUnbreakable(item);
            // Armor exceptions
            if(slot >= 100 && slot <= 103) {
                if(slot == 100) hostPlayer.getInventory().setBoots(item);
                else if(slot == 101) hostPlayer.getInventory().setLeggings(item);
                else if(slot == 102) hostPlayer.getInventory().setChestplate(item);
                else hostPlayer.getInventory().setHelmet(item);
                continue;
            }
            hostPlayer.getInventory().setItem(slot, item);
        }
    }
}
