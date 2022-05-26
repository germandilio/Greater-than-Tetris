package ru.hse.germandilio.tetris.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.hse.germandilio.tetris.client.model.UserStats;

class UserStatsTest {
    private UserStats stats;

    @BeforeEach
    void createUserStats() {
        stats = new UserStats();
    }

    @Test
    void actionHappenedTest() {
        int expectedCounter = stats.getActionsCounter();
        stats.actionHappened();
        expectedCounter++;

        Assertions.assertEquals(expectedCounter, stats.getActionsCounter());
    }

    @Test
    void getActionsCounterTest() {
        int expectedCounter = 0;

        Assertions.assertEquals(expectedCounter, stats.getActionsCounter());
    }

    @Test
    void getSecondsSinceGameStartTest() {
        stats.updateStopWatch();
        long expected = 1L;

        Assertions.assertEquals(expected, stats.getSecondsSinceGameStart());
    }
}