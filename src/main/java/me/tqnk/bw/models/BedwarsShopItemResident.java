package me.tqnk.bw.models;

import lombok.Getter;

@Getter
public class BedwarsShopItemResident {
    private int categoryIndex;
    private int shopSlotNumber;
    public BedwarsShopItemResident(int categoryIndex, int shopSlotNumber) {
        this.categoryIndex = categoryIndex - 1;
        this.shopSlotNumber = shopSlotNumber;
    }
}
