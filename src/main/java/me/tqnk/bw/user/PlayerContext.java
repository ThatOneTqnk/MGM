package me.tqnk.bw.user;

import lombok.Getter;
import lombok.Setter;
import me.tqnk.bw.match.Match;
import me.tqnk.bw.modules.team.MatchTeam;
import org.bukkit.entity.Player;

public class PlayerContext {
    @Getter private Player host;
    @Getter @Setter private Match inGame;
    @Getter @Setter private MatchTeam inTeam;

    public PlayerContext(Player p) {
        host = p;
        inGame = null;
        inTeam = null;
    }
}
