package ru.hse.germandilio.tetris.server.database;

import ru.hse.germandilio.tetris.server.clienthandling.Connection;
import ru.hse.germandilio.tetris.shared.GameResult;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;

public class GameSessionsDatabase {
    private final String databaseUrl = "jdbc:derby:memory:game-session-results;create=true";

    private final DataProvider gameSessionsDataProvider;

    public GameSessionsDatabase() {
        try {
            gameSessionsDataProvider = new JdbcDataProvider(databaseUrl);
        } catch (SQLException exception) {
            throw new RuntimeException("Cannot create table with Game sessions results.", exception);
        }
    }

    public void saveSession(Connection client) {
        // Creating game result object
        var endSessionDateTime = Instant.now().atZone(ZoneOffset.UTC).toLocalDateTime();
        var gameDuration = LocalTime.ofSecondOfDay(client.getGameSessionDuration());

        try {
            var sessionResult = new GameResult(client.getName(), endSessionDateTime, client.getBrickPlaced(), gameDuration);
            gameSessionsDataProvider.save(sessionResult);
        } catch (SQLException e) {
            throw new RuntimeException("Cannot save game session result", e);
        }
    }

    public List<GameResult> getTopResults(int topCount) {
        try {
            return gameSessionsDataProvider.findTop(topCount);
        } catch (SQLException e) {
            throw new RuntimeException("Cannot get top " + topCount + " sessions.", e);
        }
    }
}
