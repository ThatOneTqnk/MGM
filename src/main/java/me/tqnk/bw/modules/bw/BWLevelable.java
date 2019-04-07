package me.tqnk.bw.modules.bw;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class BWLevelable {
    private BWLevel levelType;
    private int magnitude;

    enum BWLevel {
        PICKAXE, AXE, ARMOR, SWORD;
    }
}
