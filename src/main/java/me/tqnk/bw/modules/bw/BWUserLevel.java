package me.tqnk.bw.modules.bw;

import lombok.Getter;

import java.util.HashMap;

@Getter
public class BWUserLevel {
    private HashMap<BWLevelable.BWLevel, Integer> levels = new HashMap<>();
    public BWUserLevel() {
        for(BWLevelable.BWLevel lvl : BWLevelable.BWLevel.values()) levels.put(lvl, 0);
    }

}
