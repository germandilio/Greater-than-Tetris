package ru.hse.germandilio.tetris.model.gameboard;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.hse.germandilio.tetris.client.model.gameboard.GameBoard;

class GameBoardTest {

    @Test
    void placeBrickOnBoard() {
        GameBoard board = new GameBoard();

        int targetX = 1;
        int targetY = 1;
        boolean[][] brickMatrix = {
                {false, true, false},
                {false, true, false},
                {false, true, false}
        };

        boolean successfully = board.placeBrickOnBoard(brickMatrix, targetX, targetY);

        Assertions.assertTrue(successfully);
    }
}