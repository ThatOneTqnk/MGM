package me.tqnk.bw.modules.bw;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

@Getter @Setter
public class BWTeamInfo {
    private Location bedLocation;
    private BWTeamStatus bwTeamStatus;
    public BWTeamInfo(Location bedLocation) {
        this(bedLocation, null);
    }
    public BWTeamInfo(Location bedLocation, BWTeamStatus bwTeamStatus) {
        this.bedLocation = bedLocation;
        this.bwTeamStatus = bwTeamStatus;
    }
}
