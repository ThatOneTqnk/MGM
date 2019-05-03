package me.tqnk.bw.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Getter @AllArgsConstructor
public class BedwarsShopItem {
    private ItemStack mainItem;
    private ItemStack costItem;
    private List<BedwarsShopItemResident> bedwarsShopItemLocations;
}
