package ru.hse.germandilio.tetris.client.model.gameboard;

public class GameBoardUtils {
    /**
     * Checks if possible to place brick on board.
     *
     * @param gameBoard         gameBoard to place
     * @param brickMatrix       matrix of brick
     * @param xCoordinateCenter X coordinate of figure center on board to place
     * @param yCoordinateCenter Y coordinate of figure center on board to place
     * @return true - if possible. Otherwise, false.
     */
    public static boolean intersects(boolean[][] gameBoard, boolean[][] brickMatrix, int xCoordinateCenter, int yCoordinateCenter) {
        // (x; y) - center of figure offset
        xCoordinateCenter -= 1;
        yCoordinateCenter -= 1;

        for (int i = 0; i < brickMatrix.length; i++) {
            for (int j = 0; j < brickMatrix[i].length; j++) {
                int posX = xCoordinateCenter + j;
                int posY = yCoordinateCenter + i;

                if (brickMatrix[i][j] && (outOfBounds(gameBoard, posX, posY) || gameBoard[posY][posX])) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Locate brick on board
     *
     * @param gameBoard         gameBoard to place
     * @param brickMatrix       matrix of brick
     * @param xCoordinateCenter X coordinate of figure center on board to place
     * @param yCoordinateCenter Y coordinate of figure center on board to place
     * @return new gameBoard with placed brick.
     */
    public static boolean[][] locateToGameBoard(boolean[][] gameBoard, boolean[][] brickMatrix,
                                                int xCoordinateCenter, int yCoordinateCenter) {
        // (x; y) - center of figure offset
        xCoordinateCenter -= 1;
        yCoordinateCenter -= 1;

        boolean[][] mergedBoard = deepCopy(gameBoard);
        for (int i = 0; i < brickMatrix.length; i++) {
            for (int j = 0; j < brickMatrix[i].length; j++) {
                int x = xCoordinateCenter + j;
                int y = yCoordinateCenter + i;

                if (brickMatrix[i][j]) {
                    mergedBoard[y][x] = brickMatrix[i][j];
                }
            }
        }

        return mergedBoard;
    }

    private static boolean[][] deepCopy(boolean[][] toCopy) {
        boolean[][] copy = new boolean[toCopy.length][];
        for (int i = 0; i < toCopy.length; i++) {
            copy[i] = toCopy[i].clone();
        }
        return copy;
    }

    private static boolean outOfBounds(boolean[][] matrix, int posX, int posY) {
        boolean correct = posX >= 0 && posY >= 0 && posY < matrix.length && posX < matrix[posY].length;
        return !correct;
    }
}
