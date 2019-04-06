package me.tqnk.bw.modules.periodical;

import me.tqnk.bw.MGM;
import me.tqnk.bw.game.MatchModule;
import me.tqnk.bw.match.Match;
import org.bukkit.Bukkit;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PeriodicalModule extends MatchModule {
    private Collection<MatchModule> periodicalModules = new ConcurrentLinkedQueue<>();
    private int runnableId;

    @Override
    public void load(Match match) {
        Bukkit.getLogger().info("loaded periodical module");
        match.getMatchInfo().getModules().stream().filter(module -> module instanceof Periodical).forEach(module -> periodicalModules.add(module));

        runnableId = Bukkit.getScheduler().runTaskTimer(MGM.get(), () -> {
            for (MatchModule matchModule : periodicalModules) {
                if (matchModule instanceof Periodical) {
                    ((Periodical) matchModule).tick();
                }
            }
        }, 1L, 1L).getTaskId();

    }

    @Override
    public void unload(Match match) {
        Bukkit.getScheduler().cancelTask(runnableId);
        periodicalModules.clear();
    }
}
