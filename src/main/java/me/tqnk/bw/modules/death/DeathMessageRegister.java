package me.tqnk.bw.modules.death;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

@AllArgsConstructor @Getter
public class DeathMessageRegister {
    private EntityDamageEvent.DamageCause cause;
    private List<String> deathMessages;
    private boolean hasKiller;
}
