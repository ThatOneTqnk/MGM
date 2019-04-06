package me.tqnk.bw.util;

import org.bukkit.World;

import java.io.File;

public class ManageWorld {
    public static boolean deleteWorld(File path) {
        if(path.exists()) {
            File files[] = path.listFiles();
            for(int i = 0; i < files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteWorld(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return(path.delete());
    }
    public static void setDefaultRules(World world) {
        world.setGameRuleValue("keepInventory", "true");
        world.setGameRuleValue("doMobSpawning", "false");
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setGameRuleValue("commandBlockOutput", "false");
        world.setGameRuleValue("logAdminCommands", "false");
        world.setGameRuleValue("doWeatherCycle", "false");
        world.setGameRuleValue("disableElytraMovementCheck", "true");
        world.setGameRuleValue("announceAdvancements", "false");
        world.setGameRuleValue("showDeathMessages", "false");
    }
    public static void deleteAllMatchWorlds() {
        File folder = new File("matches");
        if(folder.isDirectory()) {
            File[] matches = folder.listFiles();
            if(matches == null) return;
            for(File match : matches) {
                if(match.isDirectory()) deleteWorld(match);
            }
        }
    }
}
