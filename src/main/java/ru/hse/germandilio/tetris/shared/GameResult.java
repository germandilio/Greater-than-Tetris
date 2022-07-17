package ru.hse.germandilio.tetris.shared;

import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Objects;

@Getter
public class GameResult {
    private final String playerName;
    private final LocalDateTime endGameTimeUTC0;

    private final int movesCount;
    private final LocalTime gameSessionDuration;

    public GameResult(String playerName, LocalDateTime endGameTimeUTC0, int movesCount, LocalTime gameSessionDuration) {
        this.playerName = playerName;
        this.endGameTimeUTC0 = endGameTimeUTC0;
        this.movesCount = movesCount;
        this.gameSessionDuration = gameSessionDuration;
    }

    public LocalDateTime getEndGameTimeUTC0() {
        // convert to UTC time zone
        return endGameTimeUTC0
                .toInstant(ZoneOffset.UTC)
                .atZone(ZoneOffset.UTC)
                .toLocalDateTime();
    }

    public LocalDateTime getEndGameTimeAtZone(ZoneId zoneId) {
        // convert to ZoneId time zone
        return endGameTimeUTC0
                .toInstant(ZoneOffset.UTC)
                .atZone(zoneId)
                .toLocalDateTime();
    }

    @Override
    public String toString() {
        return "GameResult{" +
                "playerName='" + playerName + '\'' +
                ", endGameTimeUTC0=" + endGameTimeUTC0 +
                ", movesCount=" + movesCount +
                ", gameSessionDuration=" + gameSessionDuration +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameResult that = (GameResult) o;
        return movesCount == that.movesCount && playerName.equals(that.playerName) && endGameTimeUTC0.equals(that.endGameTimeUTC0) && gameSessionDuration.equals(that.gameSessionDuration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerName, endGameTimeUTC0, movesCount, gameSessionDuration);
    }
}
