package ru.hse.germandilio.tetris.client.controllers;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static ru.hse.germandilio.tetris.client.model.gameboard.GameBoard.BRICK_SIZE;


public class BrickToDragController implements Reset {
    private final Rectangle[][] board;
    private final int boardSize = 3;

    public BrickToDragController() {
        board = new Rectangle[boardSize][boardSize];
    }

    public void initPreviewBoard() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);

                board[i][j] = rectangle;
            }
        }
    }

    public Rectangle getBoardNode(int y, int x) {
        return board[y][x];
    }

    public int getBoardSize() {
        return boardSize;
    }


    @Override
    public void reset() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                board[i][j].setFill(Color.TRANSPARENT);
            }
        }
    }
}
