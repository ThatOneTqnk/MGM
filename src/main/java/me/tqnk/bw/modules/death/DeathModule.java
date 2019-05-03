package me.tqnk.bw.modules.death;

import me.tqnk.bw.MGM;
import me.tqnk.bw.events.MGMDeathEvent;
import me.tqnk.bw.game.MatchModule;
import me.tqnk.bw.match.Match;
import me.tqnk.bw.util.MatchUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;

public class DeathModule extends MatchModule implements Listener {
    private Match match;
    private HashMap<Player, DeathInfo> deathPackages = new HashMap<>();

    @Override
    public void load(Match match) {
        this.match = match;
    }

    @EventHandler
    public void onNaturalDamage(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        Player p = (Player) event.getEntity();
        if(!MatchUtil.determineMatchCorrespondence(p, this.match)) return;
        DeathInfo info = getDeathInfoOfPlayer(p);
        if(info == null) return;
        info.clear();
        info.setCause(event.getCause());
    }

    @EventHandler
    public void onInflictedDamage(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        Player p = (Player) event.getEntity();
        if(!MatchUtil.determineMatchCorrespondence(p, this.match)) return;
        DeathInfo info = getDeathInfoOfPlayer(p);
        if(info == null) return;
        info.clear();
        info.setCause(event.getCause());
        if(!(event.getDamager() instanceof Player)) return;
        Player killa = (Player) event.getDamager();
        info.setKiller(MGM.get().getPlayerManager().getPlayerContext(killa));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if(!MatchUtil.determineMatchCorrespondence(event.getEntity(), this.match)) return;
        DeathInfo info = getDeathInfoOfPlayer(event.getEntity());
        if(info == null) return;
        Bukkit.getPluginManager().callEvent(new MGMDeathEvent(info, event.getEntity()));
        Bukkit.getScheduler().runTaskLater(MGM.get(), () -> event.getEntity().spigot().respawn(), 1L);
    }

    /**
     * @param p Player to retrieve DeathInfo of
     * @return HashMap Value DeathInfo Reference
     */
    private DeathInfo getDeathInfoOfPlayer(Player p) {
        if(!MatchUtil.determineMatchCorrespondence(p, this.match)) return null;
        DeathInfo info = deathPackages.get(p);
        if(info == null) {
            info = new DeathInfo();
            deathPackages.put(p, info);
        }
        return info;
    }
}
