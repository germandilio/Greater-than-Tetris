package ru.hse.germandilio.tetris.server.clienthandling;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import ru.hse.germandilio.tetris.client.controllers.Reset;

import java.time.LocalDateTime;

@Getter
@Setter
public class Connection implements Reset {
    private final int id;

    // game session properties

    private String name;
    private long gameSessionDuration;
    private int brickPlaced;
    private LocalDateTime endTime;

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private boolean endedGame = false;

    public Connection(int id) {
        this.id = id;
    }

    public void setGameSessionDuration(long gameSessionDuration) {
        endedGame = true;
        this.gameSessionDuration = gameSessionDuration;
    }

    public void setBrickPlaced(int brickPlaced) {
        endedGame = true;
        this.brickPlaced = brickPlaced;
    }

    public boolean startedGame() {
        return name != null;
    }

    public boolean endedGame() {
        return endedGame;
    }

    @Override
    public void reset() {
        name = null;
        gameSessionDuration = 0L;
        brickPlaced = 0;
        endedGame = false;

        endTime = null;
    }
}
