package me.tqnk.bw.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemUtil {
    public static Color translateChatColorToColor(ChatColor chatColor) {
        switch (chatColor) {
            case AQUA:
                return Color.AQUA;
            case BLACK:
                return Color.BLACK;
            case BLUE:
                return Color.BLUE;
            case DARK_AQUA:
                return Color.BLUE;
            case DARK_BLUE:
                return Color.BLUE;
            case DARK_GRAY:
                return Color.GRAY;
            case DARK_GREEN:
                return Color.GREEN;
            case DARK_PURPLE:
                return Color.PURPLE;
            case DARK_RED:
                return Color.RED;
            case GOLD:
                return Color.YELLOW;
            case GRAY:
                return Color.GRAY;
            case GREEN:
                return Color.GREEN;
            case LIGHT_PURPLE:
                return Color.PURPLE;
            case RED:
                return Color.RED;
            case WHITE:
                return Color.WHITE;
            case YELLOW:
                return Color.YELLOW;
            default:
                break;
        }
        return null;
    }

    public static ItemStack createItem(JsonObject container) {
        Material medium = Material.valueOf(getTechnicalName(container.get("item").getAsString()));
        ItemStack item = new ItemStack(medium);
        if(container.has("count")) item.setAmount(container.get("count").getAsInt());
        ItemMeta meta = item.getItemMeta();
        if(container.has("display")) meta.setDisplayName(container.get("display").getAsString());
        if(container.has("lore")) {
            JsonArray rawLores = container.get("lore").getAsJsonArray();
            List<String> lores = new ArrayList<>();
            for(JsonElement elem : rawLores) lores.add(elem.getAsString());
            meta.setLore(lores);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItem(Material material) {
        ItemStack item = new ItemStack(material);
        return item;
    }

    public static ItemStack createItem(Material material, int amount) {
        ItemStack item = new ItemStack(material);
        item.setAmount(amount);
        return item;
    }

    public static ItemStack createItem(Material material, String name) {
        ItemStack item = createItem(material, 1);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack createItem(Material material, String name, int amount) {
        ItemStack item = createItem(material, name);
        item.setAmount(amount);
        return item;
    }

    public static ItemStack createItem(Material material, String name, List<String> lore, int amount) {
        ItemStack item = createItem(material, name);
        item.setAmount(amount);

        ItemMeta meta = item.getItemMeta();
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack createItem(Material material, String name, List<String> lore, int amount, byte data) {
        ItemStack item = new ItemStack(material, amount, data);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = createItem(material, name);

        ItemMeta meta = item.getItemMeta();
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack createPotion(PotionType potionType, int level, String name) {
        Potion potion = new Potion(potionType);
        potion.setLevel(level);

        ItemStack itemStack = potion.toItemStack(1);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public static ItemStack getPlayerSkull(Player p) {
        return getPlayerSkull(p.getUniqueId());
    }

    public static ItemStack getPlayerSkull(UUID uuid) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        skull.setItemMeta(meta);
        return skull;
    }

    public static void setUnbreakable(ItemStack stack){
        ItemMeta meta = stack.getItemMeta();
        meta.setUnbreakable(true);
        stack.setItemMeta(meta);
    }

    public static String getTechnicalName(String inItem) {
        return inItem.toUpperCase().replaceAll(" ", "_");
    }

}
