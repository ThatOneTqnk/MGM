package me.tqnk.bw.modules.bw;

import lombok.Getter;
import lombok.Setter;
import me.tqnk.bw.MGM;
import me.tqnk.bw.modules.team.MatchTeam;
import me.tqnk.bw.user.PlayerContext;
import me.tqnk.bw.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BWShopIndex {
    private Player player;
    private BWUserLevel lvls;
    @Getter @Setter private int currentPage;

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
        int picklevel = lvls.getLevels().get(BWLevelable.BWLevel.PICKAXE);
        ItemStack pickBuy = ItemUtil.createItem(Material.WOOD_PICKAXE, ChatColor.YELLOW + "Wooden Pickaxe");
        ItemStack pickCost = new ItemStack(Material.IRON_INGOT, 10);
        String costType = "iron";
        if(picklevel == 1) {
            pickBuy = ItemUtil.createItem(Material.STONE_PICKAXE, ChatColor.YELLOW + "Stone Pickaxe");
        } else if(picklevel == 2) {
            pickBuy = ItemUtil.createItem(Material.IRON_PICKAXE, ChatColor.YELLOW + "Iron Pickaxe");
            pickCost = new ItemStack(Material.GOLD_INGOT, 5);
            costType = "gold";
        } else if(picklevel >= 3) {
            pickBuy = ItemUtil.createItem(Material.DIAMOND_PICKAXE, ChatColor.YELLOW + "Diamond Pickaxe");
            pickCost = new ItemStack(Material.GOLD_INGOT, 10);
            costType = "gold";
        }
        pickBuy.addEnchantment(Enchantment.DIG_SPEED, 1);

        BWShopItem finalPickaxe = new BWShopItem(pickBuy, pickCost, shopLores.get(costType));
        finalPickaxe.setBwLevelable(new BWLevelable(BWLevelable.BWLevel.PICKAXE, (picklevel + 1 >= 5) ? 4 : picklevel + 1));
        shopIndex.put(235, finalPickaxe);

        int axeLevel = lvls.getLevels().get(BWLevelable.BWLevel.AXE);
        ItemStack axeBuy = ItemUtil.createItem(Material.WOOD_AXE, ChatColor.YELLOW + "Wooden Axe");
        ItemStack axeCost = new ItemStack(Material.IRON_INGOT, 10);
        String axeCostType = "iron";
        if(axeLevel == 1) {
            axeBuy = ItemUtil.createItem(Material.STONE_AXE, ChatColor.YELLOW + "Stone Axe");
        } else if(axeLevel == 2) {
            axeBuy = ItemUtil.createItem(Material.IRON_AXE, ChatColor.YELLOW + "Iron Axe");
            axeCost = new ItemStack(Material.GOLD_INGOT, 5);
            axeCostType = "gold";
        } else if(axeLevel >= 3) {
            axeBuy = ItemUtil.createItem(Material.DIAMOND_AXE, ChatColor.YELLOW + "Diamond Axe");
            axeCost = new ItemStack(Material.GOLD_INGOT, 10);
            axeCostType = "gold";
        }
        axeBuy.addEnchantment(Enchantment.DIG_SPEED, 1);
        BWShopItem finalAxe = new BWShopItem(axeBuy, axeCost, shopLores.get(axeCostType));
        finalAxe.setBwLevelable(new BWLevelable(BWLevelable.BWLevel.AXE, (axeLevel + 1 >= 5) ? 4 : axeLevel + 1));
        shopIndex.put(236, finalAxe);
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
        BWShopItem diamondArmor = new BWShopItem(ItemUtil.createItem(Material.DIAMOND_BOOTS, ChatColor.YELLOW + "Diamond Armor", 1), new ItemStack(Material.EMERALD, 6), shopLores.get("emerald"));
        diamondArmor.setBwLevelable(new BWLevelable(BWLevelable.BWLevel.ARMOR, 3));

        shopIndexMutate.put(30, ironArmor);
        shopIndexMutate.put(21, chainArmor);
        shopIndexMutate.put(183, diamondArmor);
        shopIndexMutate.put(182, ironArmor);
        shopIndexMutate.put(181, chainArmor);

        BWShopItem shears = new BWShopItem(ItemUtil.createItem(Material.SHEARS, ChatColor.WHITE + "Permanent Shears"), new ItemStack(Material.IRON_INGOT, 20), shopLores.get("iron"));
        shears.setBwLevelable(new BWLevelable(BWLevelable.BWLevel.SHEARS, 1));
        shopIndexMutate.put(31, shears);

        shopIndexMutate.put(28, new BWShopItem(ItemUtil.createItem(Material.WOOD, ChatColor.YELLOW + "Wood", Arrays.asList(ChatColor.GRAY + "@ wood"), 4), new ItemStack(Material.GOLD_INGOT, 8), shopLores.get("gold")));
    }

    public void applyIndexToInventory(Inventory epic, int currentPage) {
        for(int x = 18; x <= 53; x++) epic.setItem(x, new ItemStack(Material.AIR));
        int upperBound = 53 + (currentPage * 54);
        int lowerBound = currentPage * 54;
        for(Map.Entry<Integer, BWShopItem> entry : shopIndex.entrySet()) {
            int theKey = entry.getKey();
            if(theKey < lowerBound || theKey > upperBound) continue;
            epic.setItem(theKey % 54, entry.getValue().getItem());
        }
    }
}
