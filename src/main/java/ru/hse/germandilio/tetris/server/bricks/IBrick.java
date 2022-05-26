package ru.hse.germandilio.tetris.server.bricks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IBrick implements Brick {
    private final List<boolean[][]> matrix = new ArrayList<>();

    public IBrick() {
        matrix.add(new boolean[][]{
                {false, false, false},
                {true, true, true},
                {false, false, false},
        });

        matrix.add(new boolean[][]{
                {false, true, false},
                {false, true, false},
                {false, true, false}
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
