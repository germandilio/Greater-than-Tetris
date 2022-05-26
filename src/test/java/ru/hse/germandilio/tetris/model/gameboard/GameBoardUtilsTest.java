package ru.hse.germandilio.tetris.model.gameboard;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.hse.germandilio.tetris.client.model.gameboard.GameBoardUtils;

class GameBoardUtilsTest {

    @Test
    void intersects_ShouldBeTrue() {
        boolean[][] gameBoard = {
                {false, false, false, false, false},
                {false, true, false, false, false},
                {false, false, false, false, false},
                {false, false, false, false, false},
                {false, false, false, false, false},

        };

        int targetX = 1;
        int targetY = 1;
        boolean[][] brickMatrix = {
                {false, false, false},
                {false, true, false},
                {false, false, false}
        };

        boolean intersects = GameBoardUtils.intersects(gameBoard, brickMatrix, targetX, targetY);

        Assertions.assertTrue(intersects);
    }

    @Test
    void intersects_ShouldBeFalse() {
        boolean[][] gameBoard = {
                {false, false, false, false, false},
                {false, true, false, false, false},
                {false, false, false, false, false},
                {false, false, false, false, false},
                {false, false, false, false, false},

        };

        int targetX = 1;
        int targetY = 0;
        boolean[][] brickMatrix = {
                {false, false, false},
                {false, false, false},
                {false, false, true}
        };

        boolean intersects = GameBoardUtils.intersects(gameBoard, brickMatrix, targetX, targetY);

        Assertions.assertFalse(intersects);
    }

    @Test
    void locateToGameBoard() {
        boolean[][] gameBoard = new boolean[5][5];

        int targetX = 0;
        int targetY = 0;
        boolean[][] brickMatrix = {
                {false, false, false},
                {false, true, false},
                {false, false, false}
        };

        boolean[][] expected = {
                {true, false, false, false, false},
                {false, false, false, false, false},
                {false, false, false, false, false},
                {false, false, false, false, false},
                {false, false, false, false, false},

        };

        gameBoard = GameBoardUtils.locateToGameBoard(gameBoard, brickMatrix, targetX, targetY);

        for (int i = 0; i < expected.length; i++) {
            for (int j = 0; j < expected[i].length; j++) {
                Assertions.assertEquals(expected[i][j], gameBoard[i][j]);
            }
        }
    }
}