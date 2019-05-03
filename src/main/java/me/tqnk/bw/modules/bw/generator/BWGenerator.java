package me.tqnk.bw.modules.bw.generator;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

@Getter
public class BWGenerator {
    private ItemStack genItem;
    private Location area;
    private int rate;
    @Setter private int current;
    public BWGenerator(ItemStack genItem, Location area, int rate) {
        this.genItem = genItem;
        area.setY(area.getY() + 1.5);
        area.setX(area.getX() + 0.5);
        area.setZ(area.getZ() + 0.5);
        this.area = area;
        this.rate = rate;
        this.current = rate;
    }
    public void decrementCurrent() { current--; }
}
