package ru.hse.germandilio.tetris.server.database;

import ru.hse.germandilio.tetris.shared.GameResult;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class JdbcDataProvider implements DataProvider {
    private final String url;

    public JdbcDataProvider(String url) throws SQLException {
        this.url = url;

        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE GAME_RESULTS(
                        Id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                        Login VARCHAR(255) NOT NULL,
                        SessionEndTime TIMESTAMP NOT NULL,
                        MovesCount INTEGER CHECK (MovesCount >= 0),
                        GameTime TIME NOT NULL)
                    """);
        }
    }

    @Override
    public void save(GameResult gameResult) throws SQLException {
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO GAME_RESULTS (Login, SessionEndTime, MovesCount, GameTime) VALUES (?, ?, ?, ?)")) {
            // set top parameter
            statement.setString(1, gameResult.getPlayerName());
            statement.setTimestamp(2, Timestamp.from(gameResult.getEndGameTimeUTC0().toInstant(ZoneOffset.UTC)));
            statement.setInt(3, gameResult.getMovesCount());
            statement.setTime(4, Time.valueOf(gameResult.getGameSessionDuration()));

            statement.executeUpdate();
        }
    }

    @Override
    public List<GameResult> findTop(int topCount) throws SQLException {
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM GAME_RESULTS ORDER BY SessionEndTime ASC, GameTime DESC, MovesCount ASC FETCH FIRST ? ROWS ONLY")) {
            // set top parameter
            statement.setInt(1, topCount);

            try (ResultSet results = statement.executeQuery()) {
                List<GameResult> allMovies = new ArrayList<>();
                while (results.next()) {
                    // retrieve data from table
                    String login = results.getString("Login");
                    LocalDateTime endTime = results.getTimestamp("SessionEndTime").toInstant()
                            .atZone(ZoneOffset.UTC).toLocalDateTime();
                    int movesCount = results.getInt("MovesCount");
                    LocalTime gameDuration = results.getTime("GameTime").toLocalTime();

                    // create game session result
                    GameResult gameResult = new GameResult(login, endTime, movesCount, gameDuration);
                    allMovies.add(gameResult);
                }

                return allMovies;
            }
        }
    }
}
