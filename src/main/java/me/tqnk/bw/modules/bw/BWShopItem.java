package me.tqnk.bw.modules.bw;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

@Getter
public class BWShopItem {
    private ItemStack item;
    private ItemStack costMaterial;
    @Setter private BWLevelable bwLevelable = null;

    public BWShopItem(ItemStack item, ItemStack costMaterial) {
        this(item, costMaterial, null);
    }
    public BWShopItem(ItemStack item, ItemStack costMaterial, String costInsert) {
        if(costInsert == null) costInsert = ChatColor.YELLOW.toString() + costMaterial.getAmount() + " " + costMaterial.getType().toString();
        ItemMeta meta = item.getItemMeta();
        List<String> existingLore = meta.getLore();
        if(existingLore == null) existingLore = new ArrayList<>();
        existingLore.add(costInsert);
        for (final ListIterator<String> i = existingLore.listIterator(); i.hasNext(); ) {
            final String element = i.next();
            i.set(element.replaceAll("%", costMaterial.getAmount() + "").replaceAll("#", (costMaterial.getAmount() > 1) ? "s" : "").replaceAll("@", item.getAmount() + ""));
        }

        meta.setLore(existingLore);
        item.setItemMeta(meta);
        this.item = item;
        this.costMaterial = costMaterial;
    }
}
