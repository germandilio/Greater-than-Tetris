package ru.hse.germandilio.tetris.server.database;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.hse.germandilio.tetris.shared.GameResult;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;

class JdbcDataProviderTest {
    private final String databaseUrl = "jdbc:derby:memory:game-session-results;create=true";

    @Test
    void saveOneGameResult() {
        try {
            DataProvider jdbc = new JdbcDataProvider(databaseUrl);

            // prepare parameters
            String login = "greenkrug";
            var endSession = Instant.now().atZone(ZoneOffset.UTC).toLocalDateTime();
            int movesCount = 10;
            var gameDuration = LocalTime.of(0, 1, 0);

            // create game result object
            GameResult game = new GameResult(login, endSession, movesCount, gameDuration);

            // save
            jdbc.save(game);

            var results = jdbc.findTop(1);

            Assertions.assertEquals(game, results.get(0));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void findTop1() {

    }
}