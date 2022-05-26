package ru.hse.germandilio.tetris.server.bricks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OBrick implements Brick {
    private final List<boolean[][]> matrix = new ArrayList<>();

    public OBrick() {
        matrix.add(new boolean[][]{
                {false, false, false},
                {false, true, false},
                {false, false, false},
        });
    }

    @Override
    public List<boolean[][]> getMatrix() {
        return Collections.unmodifiableList(matrix);
    }

    @Override
    public int getRotationsCount() {
        return matrix.size();
    }
}
