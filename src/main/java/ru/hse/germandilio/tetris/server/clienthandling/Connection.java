package ru.hse.germandilio.tetris.server.clienthandling;

import ru.hse.germandilio.tetris.client.controllers.IReset;

public class Connection implements IReset {
    private final int id;

    // game session properties

    private String name;
    private long gameSessionDuration;
    private int brickPlaced;

    private boolean hasEndedGame = false;

    public Connection(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public long getGameSessionDuration() {
        return gameSessionDuration;
    }

    public int getBrickPlaced() {
        return brickPlaced;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGameSessionDuration(long gameSessionDuration) {
        hasEndedGame = true;
        this.gameSessionDuration = gameSessionDuration;
    }

    public void setBrickPlaced(int brickPlaced) {
        hasEndedGame = true;
        this.brickPlaced = brickPlaced;
    }

    public boolean startedGame() {
        return name != null;
    }

    public boolean endedGame() {
        return hasEndedGame;
    }

    @Override
    public void reset() {
        name = null;
        gameSessionDuration = 0L;
        brickPlaced = 0;
        hasEndedGame = false;
    }
}
