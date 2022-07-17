package ru.hse.germandilio.tetris.server.bricks;

import java.util.List;

public interface Brick {
    /**
     * Internal matrix that represents 3x3 brick.
     *
     * @return {@link List} of all brick rotations.
     */
    List<boolean[][]> getMatrix();

    /**
     * Number of available rotations.
     *
     * @return {@link Integer} number.
     */
    int getRotationsCount();
}
