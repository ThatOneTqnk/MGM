package me.tqnk.bw.util;

import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;

public class PlayerUtil {
    public static void generalReadyPlayer(Player p, GameMode gm) {
        p.setFlying(false);
        p.setGameMode(gm);
        p.setHealth(20);
        for(PotionEffect effect : p.getActivePotionEffects()) p.removePotionEffect(effect.getType());
        p.getInventory().clear();
    }

    public static void equipPlayerWith(Player p, String armor, Color color) {
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        if(armor.equalsIgnoreCase("diamond")) {
            chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
            leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
            boots = new ItemStack(Material.DIAMOND_BOOTS);
        } else if(armor.equalsIgnoreCase("iron")) {
            chestplate = new ItemStack(Material.IRON_CHESTPLATE);
            leggings = new ItemStack(Material.IRON_LEGGINGS);
            boots = new ItemStack(Material.IRON_BOOTS);
        } else if(armor.equalsIgnoreCase("chainmail")) {
            chestplate = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
            leggings = new ItemStack(Material.CHAINMAIL_LEGGINGS);
            boots = new ItemStack(Material.CHAINMAIL_BOOTS);
        } else if(armor.equalsIgnoreCase("leather")) {
            LeatherArmorMeta meta2 = (LeatherArmorMeta) chestplate.getItemMeta();
            meta2.setColor(color);
            chestplate.setItemMeta(meta2);

            LeatherArmorMeta meta3 = (LeatherArmorMeta) leggings.getItemMeta();
            meta3.setColor(color);
            leggings.setItemMeta(meta3);

            LeatherArmorMeta meta4 = (LeatherArmorMeta) boots.getItemMeta();
            meta4.setColor(color);
            boots.setItemMeta(meta4);
        }
        LeatherArmorMeta meta1 = (LeatherArmorMeta) helmet.getItemMeta();
        meta1.setColor(color);
        helmet.setItemMeta(meta1);

        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);
    }

    public static void playSound(Player p, Sound sound, float pitch) {
        p.playSound(p.getLocation().clone().add(0.0, 100.0, 0.0), sound, 1000, pitch);
    }

    public static int removeItems(Inventory inventory, Material type, int amount) {
        if(type == null || inventory == null)
            return -1;
        if (amount <= 0)
            return -1;

        if (amount == Integer.MAX_VALUE) {
            inventory.remove(type);
            return 0;
        }

        inventory.removeItem(new ItemStack(type,amount));
        return 0;
    }
}
