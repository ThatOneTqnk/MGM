package me.tqnk.bw.status;

public enum GameStatus {
    PRE(0), QUEUE(1), MID(2), POST(3), DONE(4);
    private int state;
    GameStatus(int state) {
        this.state = state;
    }
}
