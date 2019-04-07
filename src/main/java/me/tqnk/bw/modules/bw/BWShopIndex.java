package me.tqnk.bw.modules.bw;

import lombok.Getter;
import me.tqnk.bw.MGM;
import me.tqnk.bw.modules.team.MatchTeam;
import me.tqnk.bw.user.PlayerContext;
import me.tqnk.bw.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BWShopIndex {
    private Player player;
    private BWUserLevel lvls;

    private static HashMap<String, String> shopLores = new HashMap<String, String>() {{
        put("gold", ChatColor.GOLD + "Cost: % " + ChatColor.YELLOW + "gold");
        put("iron", ChatColor.WHITE + "Cost: % " + ChatColor.GRAY + "iron");
        put("emerald", ChatColor.DARK_GREEN + "Cost: % " + ChatColor.GREEN + "emerald#");
        put("diamond", ChatColor.DARK_AQUA + "Cost: % " + ChatColor.AQUA + "diamond#");
    }};
    @Getter private HashMap<Integer, BWShopItem> shopIndex = new HashMap<>();

    public BWShopIndex(Player player, BWUserLevel lvls) {
        this.player = player;
        // create reference to user levels
        this.lvls = lvls;
        updateIndex();
    }

    public void updateIndex() {
        populateDefaults(this.shopIndex);
        PlayerContext p = MGM.get().getPlayerManager().getPlayerContext(player);
        if(p == null || p.getInTeam() == null) return;
        MatchTeam hostTeam = p.getInTeam();
        ItemStack someWool = ItemUtil.createItem(Material.WOOL, ChatColor.YELLOW + "Wool", Arrays.asList(ChatColor.GRAY + "@ wool"), 16, (byte) getColorIdByTeam(hostTeam));
        shopIndex.put(19, new BWShopItem(someWool, new ItemStack(Material.IRON_INGOT, 4), shopLores.get("iron")));
    }

    private static short getColorIdByTeam(MatchTeam team) {
        switch (team.getChatTeamColor()) {
            case GREEN:
                return 13;
            case RED:
                return 14;
            case BLUE:
                return 11;
            case YELLOW:
                return 4;
            default:
                return 0;
        }
    }

    private static void populateDefaults(HashMap<Integer, BWShopItem> shopIndexMutate) {
        BWShopItem stoneSword = new BWShopItem(ItemUtil.createItem(Material.STONE_SWORD, ChatColor.YELLOW + "Stone Sword", 1), new ItemStack(Material.IRON_INGOT, 10), shopLores.get("iron"));
        stoneSword.setBwLevelable(new BWLevelable(BWLevelable.BWLevel.SWORD, 1));
        shopIndexMutate.put(20, stoneSword);
        BWShopItem ironSword = new BWShopItem(ItemUtil.createItem(Material.IRON_SWORD, ChatColor.YELLOW + "Iron Sword", 1), new ItemStack(Material.GOLD_INGOT, 7), shopLores.get("gold"));
        ironSword.setBwLevelable(new BWLevelable(BWLevelable.BWLevel.SWORD, 3));
        shopIndexMutate.put(29, ironSword);
        BWShopItem chainArmor = new BWShopItem(ItemUtil.createItem(Material.CHAINMAIL_BOOTS, ChatColor.YELLOW + "Chainmail Armor", 1), new ItemStack(Material.IRON_INGOT, 24), shopLores.get("iron"));
        chainArmor.setBwLevelable(new BWLevelable(BWLevelable.BWLevel.ARMOR, 1));
        shopIndexMutate.put(21, chainArmor);
        BWShopItem ironArmor = new BWShopItem(ItemUtil.createItem(Material.IRON_BOOTS, ChatColor.YELLOW + "Iron Armor", 1), new ItemStack(Material.GOLD_INGOT, 12), shopLores.get("gold"));
        ironArmor.setBwLevelable(new BWLevelable(BWLevelable.BWLevel.ARMOR, 2));
        shopIndexMutate.put(30, ironArmor);
        shopIndexMutate.put(21, chainArmor);

        shopIndexMutate.put(28, new BWShopItem(ItemUtil.createItem(Material.WOOD, ChatColor.YELLOW + "Wood", Arrays.asList(ChatColor.GRAY + "@ wood"), 4), new ItemStack(Material.GOLD_INGOT, 8), shopLores.get("gold")));
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
