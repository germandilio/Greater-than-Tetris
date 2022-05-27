package ru.hse.germandilio.tetris.client.model;

import java.time.LocalDateTime;

public class GameResult {
    private final String playerName;
    private final LocalDateTime endGameTimeUTC0;

    private final int movesCount;
    private final long gameSessionDuration;

    public GameResult(String playerName, LocalDateTime endGameTimeUTC0, int movesCount, long gameSessionDuration) {
        this.playerName = playerName;
        this.endGameTimeUTC0 = endGameTimeUTC0;
        this.movesCount = movesCount;
        this.gameSessionDuration = gameSessionDuration;
    }

    public String getPlayerName() {
        return playerName;
    }

    public LocalDateTime getEndGameTime() {
        // convert to current local time zone
        return endGameTimeUTC0;
    }

    public int getMovesCount() {
        return movesCount;
    }

    public long getGameSessionDuration() {
        return gameSessionDuration;
    }
}
