package me.tqnk.bw.modules.bw;

public enum BWTeamStatus {
    DEAD(0), NORESPAWN(1), ALIVE(2);
    private int aliveOrder;
    BWTeamStatus(int aliveOrder) {
        this.aliveOrder = aliveOrder;
    }
    public int getAliveOrder() { return aliveOrder; }
}
