package ru.hse.germandilio.tetris.server;

import ru.hse.germandilio.tetris.commands.GameStatus;

public class UserInfo {
    private GameStatus status;
    private String name;
    private int movesCount;
    private long gameDuration;

    public UserInfo() {
        status = GameStatus.NON_INITIALIZED;
        name = null;
        movesCount = 0;
        gameDuration = 0;
    }

    public GameStatus getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public int getMovesCount() {
        return movesCount;
    }

    public long getGameDuration() {
        return gameDuration;
    }

    public void incrementMovesCount() {
        ++movesCount;
    }

    public void setGameDuration(long time) {
        gameDuration = time;
    }

    public void setName(String name) {
        if (name != null && !name.isEmpty()) {
            this.name = name;
        }
    }

    public void setStatus(GameStatus newStatus) {
        status = newStatus;
    }
}
