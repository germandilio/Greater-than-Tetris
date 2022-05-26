package ru.hse.germandilio.tetris.client.model;

import ru.hse.germandilio.tetris.client.controllers.IReset;
import ru.hse.germandilio.tetris.commands.GameStatus;

public class UserStats implements IReset {
    private long secondsSinceGameStart = 0;
    private int userActionsCounter = 0;
    private GameStatus status;
    private String name;
    private String partnerName;

    public UserStats() {
        reset();
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void actionHappened() {
        ++userActionsCounter;
    }

    public int getActionsCounter() {
        return userActionsCounter;
    }

    public long getSecondsSinceGameStart() {
        return secondsSinceGameStart;
    }

    public void updateStopWatch() {
        ++secondsSinceGameStart;
    }

    public void reset() {
        secondsSinceGameStart = 0;
        userActionsCounter = 0;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
