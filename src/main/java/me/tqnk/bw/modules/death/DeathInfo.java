package me.tqnk.bw.modules.death;

import lombok.Getter;
import lombok.Setter;
import me.tqnk.bw.user.PlayerContext;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

@Getter @Setter
public class DeathInfo {
    private EntityDamageEvent.DamageCause cause;
    private PlayerContext killer;
    public DeathInfo() {}
    public void clear() {
        this.cause = null;
        this.killer = null;
    }
}
