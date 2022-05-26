package ru.hse.germandilio.tetris.server.bricks;

import java.util.List;

public interface Brick {
    List<boolean[][]> getMatrix();

    int getRotationsCount();
}
