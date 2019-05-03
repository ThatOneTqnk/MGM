package me.tqnk.bw.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@AllArgsConstructor @Getter
public class BedwarsShop {
    private String shopTitle;
    private List<ItemStack> categories;
    private List<BedwarsShopItem> shopItems;
}
