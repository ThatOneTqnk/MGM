package me.tqnk.bw.match;

public enum ModuleLoadTime {
    EARLIEST(0), EARLIER(1), EARLY(2), NORMAL(3), LATE(3), LATER(4),  LATEST(5);
    private final int priorityLevel;
    ModuleLoadTime(int priorityLevel) {
        this.priorityLevel = priorityLevel;
    }
}
