package me.tqnk.bw.util;

import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class PlayerUtil {
    public static void generalReadyPlayer(Player p, GameMode gm) {
        p.setFlying(false);
        p.setGameMode(gm);
        p.setHealth(20);
        for(PotionEffect effect : p.getActivePotionEffects()) p.removePotionEffect(effect.getType());
        p.getInventory().clear();
    }

    public static void playSound(Player p, Sound sound, float pitch) {
        p.playSound(p.getLocation().clone().add(0.0, 100.0, 0.0), sound, 1000, pitch);
    }
}
