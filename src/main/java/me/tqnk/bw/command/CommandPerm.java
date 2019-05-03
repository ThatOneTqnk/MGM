package me.tqnk.bw.command;

import lombok.Getter;
import me.tqnk.bw.permissions.RankData;

@Getter
public class CommandPerm {
    private String command;
    private RankData requirement;
    CommandPerm(String command, RankData requirement) {
        this.command = command;
        this.requirement = requirement;
    }
}
