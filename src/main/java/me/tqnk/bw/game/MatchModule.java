package me.tqnk.bw.game;

import me.tqnk.bw.match.Match;

public abstract class MatchModule {
    public void load(Match match) {}
    public void unload(Match match) {}
    public void start() {}
}
