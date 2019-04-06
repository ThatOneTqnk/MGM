package me.tqnk.bw.permissions;

public enum RankData {
    ADMIN(999),
    DEFAULT(0);

    private int priority;

    private RankData(int priority) {
        this.priority = priority;
    }
    public int getPriority() {
        return priority;
    }
}
