package me.tqnk.bw.modules.bw.generator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter @AllArgsConstructor
public class BWGenerator {
    private BWGenType bwGenType;
    private Location area;

    public enum BWGenType {
        IRON(1, new ItemStack(Material.IRON_INGOT)),
        DIAMOND((double) 1/60, new ItemStack(Material.DIAMOND)),
        EMERALD((double) 1/60, new ItemStack(Material.EMERALD)),
        GOLD((double) 1/5, new ItemStack(Material.GOLD_INGOT));

        private double rate;
        private ItemStack itemStack;
        BWGenType(double rate, ItemStack itemStack) {
            this.rate = rate;
            this.itemStack = itemStack;
        }

        public double getRate() {
            return rate;
        }

        public void setRate(double rate) {
            this.rate = rate;
        }
    }
}
