package me.tqnk.bw.modules.team;

import lombok.Getter;
import me.tqnk.bw.MGM;
import me.tqnk.bw.game.MatchModule;
import me.tqnk.bw.match.Match;
import me.tqnk.bw.match.ModuleData;
import me.tqnk.bw.match.ModuleLoadTime;
import me.tqnk.bw.modules.scoreboard.ScoreboardManagerModule;
import me.tqnk.bw.user.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Collection;
import java.util.List;
import java.util.Random;

@ModuleData(load = ModuleLoadTime.EARLIER)
public class TeamManagerModule extends MatchModule {
    @Getter private List<MatchTeam> allTeams;
    private ScoreboardManagerModule sbManager;
    private Match match;
    private PlayerManager playerManager;

    @Override
    public void load(Match match) {
        // allTeams reference links to MatchTeam List in Match
        allTeams = match.getTeams();
        this.match = match;
        sbManager = match.getModule(ScoreboardManagerModule.class);
        playerManager = MGM.get().getPlayerManager();
        parseAndRegisterTeams();
    }

    private void parseAndRegisterTeams() {
        for(MatchTeam team : allTeams) registerTeam(team);
    }

    public void registerTeam(MatchTeam team) {
        Bukkit.getLogger().info("added a team: " + team.getTeamID());
        if(!allTeams.contains(team)) allTeams.add(team);
        String randID = team.getTeamID() + "-" + (new Random().nextInt(50) + 10);
        team.setSbTeamHookID(randID);
        sbManager.addNewTeam(team.getSbTeamHookID(), team.getChatTeamColor(),true);
    }

    public void removeTeam(MatchTeam team) {
        allTeams.remove(team);
        sbManager.removeTeam(team.getSbTeamHookID());
    }

    public MatchTeam getMatchTeamById(String id) {
        for(MatchTeam cteam : allTeams) if(cteam.getTeamID().equalsIgnoreCase(id)) return cteam;
        return null;
    }

    public void addToTeam(Player player, MatchTeam team) {
        team.add(player);
        playerManager.getPlayerContext(player).setInTeam(team);
        sbManager.addToTeam(player, team.getSbTeamHookID());
    }

    public void removeFromTeam(Player player, MatchTeam team) {
        team.remove(player);
        playerManager.getPlayerContext(player).setInTeam(null);
        sbManager.removeFromTeam(player, team.getSbTeamHookID());
    }

    public void distributePlayersToTeams(Collection<Player> players) {
        for(Player player : players) {
            MatchTeam into = findLeastPopulatedTeam();
            if(into == null) return;
            addToTeam(player, into);
        }
    }

    private MatchTeam findLeastPopulatedTeam() {
        int lowest = 999;
        MatchTeam candidate = null;
        for(MatchTeam team : allTeams) {
            int fighter = team.getPlayers().size();
            if(fighter < lowest) {
                lowest = fighter;
                candidate = team;
            }
        }
        return candidate;
    }

    public void sendAllTeamsToTheirSpawn() {
        for(MatchTeam team : allTeams) sendTeamToTheirSpawn(team);
    }

    public void sendTeamToTheirSpawn(MatchTeam team) {
        for(Player ply : team.getPlayers()) {
            ply.teleport(team.getSpawnArea(), PlayerTeleportEvent.TeleportCause.PLUGIN);
        }
    }


}
