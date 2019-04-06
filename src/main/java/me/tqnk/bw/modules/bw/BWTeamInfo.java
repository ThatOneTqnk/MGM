package me.tqnk.bw.modules.bw;

import lombok.Getter;
import org.bukkit.Location;

@Getter
public class BWTeamInfo {
    private Location bedLocation;
    private BWTeamStatus bwTeamStatus;
    public BWTeamInfo(Location bedLocation, BWTeamStatus bwTeamStatus) {
        this.bedLocation = bedLocation;
        this.bwTeamStatus = bwTeamStatus;
    }
}
