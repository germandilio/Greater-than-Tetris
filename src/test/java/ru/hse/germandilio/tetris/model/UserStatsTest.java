package ru.hse.germandilio.tetris.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.hse.germandilio.tetris.client.model.GameSessionStats;

class UserStatsTest {
    private GameSessionStats stats;

    @BeforeEach
    void createUserStats() {
        stats = new GameSessionStats();
    }

    @Test
    void actionHappenedTest() {
        int expectedCounter = stats.getBricksPlaced();
        stats.brickPlaced();
        expectedCounter++;

        Assertions.assertEquals(expectedCounter, stats.getBricksPlaced());
    }

    @Test
    void getActionsCounterTest() {
        int expectedCounter = 0;

        Assertions.assertEquals(expectedCounter, stats.getBricksPlaced());
    }

    @Test
    void getSecondsSinceGameStartTest() {
        stats.updateStopWatch();
        long expected = 1L;

        Assertions.assertEquals(expected, stats.getGameSessionDuration());
    }
}