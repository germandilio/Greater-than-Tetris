package ru.hse.germandilio.tetris.client.model.gameboard;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import ru.hse.germandilio.tetris.client.controllers.IReset;


public class GameBoard implements IReset {
    private boolean[][] gameBoard;
    private final Rectangle[][] displayBoard;

    public static final int GAME_FIELD_SIZE = 9;
    public static final int BRICK_SIZE = 49;

    public GameBoard() {
        gameBoard = new boolean[GAME_FIELD_SIZE][GAME_FIELD_SIZE];
        displayBoard = new Rectangle[GAME_FIELD_SIZE][GAME_FIELD_SIZE];
    }

    /**
     * Initialize gameBoardView
     */
    public void initDisplayBoard() {
        // init displayBoard
        for (int i = 0; i < GAME_FIELD_SIZE; i++) {
            for (int j = 0; j < GAME_FIELD_SIZE; j++) {
                displayBoard[i][j] = getInitialBrick();
            }
        }
    }

    public Rectangle getInitialBrick() {
        Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
        rectangle.setFill(Color.TRANSPARENT);
        return rectangle;
    }

    public Rectangle getDisplayBoardNode(int y, int x) {
        return displayBoard[y][x];
    }

    public boolean getGameBoardNode(int y, int x) {
        return gameBoard[y][x];
    }

    public boolean placeBrickOnBoard(boolean[][] brickToPlace, int xCenterPosition, int yCenterPosition) {
        if (!GameBoardUtils.intersects(gameBoard, brickToPlace, xCenterPosition, yCenterPosition)) {
            gameBoard = GameBoardUtils.locateToGameBoard(gameBoard, brickToPlace, xCenterPosition, yCenterPosition);
            return true;
        }
        return false;
    }

    public void reset() {
        gameBoard = new boolean[GAME_FIELD_SIZE][GAME_FIELD_SIZE];

        // clear displayBoard
        for (int i = 0; i < GAME_FIELD_SIZE; i++) {
            for (int j = 0; j < GAME_FIELD_SIZE; j++) {
                displayBoard[i][j].setFill(Color.TRANSPARENT);
            }
        }
    }
}
