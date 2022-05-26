package ru.hse.germandilio.tetris.client.model;

import ru.hse.germandilio.tetris.client.controllers.IReset;

public class UserStats implements IReset {
    private long gameSessionDuration = 0;
    private int bricksPlaced = 0;
    private String name;

    private String partnerName;
    private long maxSessionTime;

    public UserStats() {
        reset();
    }

    public void brickPlaced() {
        ++bricksPlaced;
    }

    public int getBricksPlaced() {
        return bricksPlaced;
    }

    public void updateStopWatch() {
        ++gameSessionDuration;
    }

    public long getGameSessionDuration() {
        return gameSessionDuration;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public String getName() {
        return name;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void reset() {
        gameSessionDuration = 0;
        bricksPlaced = 0;
        maxSessionTime = 0L;
        partnerName = null;
    }
}
