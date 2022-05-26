package ru.hse.germandilio.tetris.server.bricks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CornerBrick implements Brick {
    private final List<boolean[][]> matrix = new ArrayList<>();

    public CornerBrick() {
        matrix.add(new boolean[][]{
                {false, false, false},
                {false, true, true},
                {false, true, false},
        });

        matrix.add(new boolean[][]{
                {false, false, false},
                {true, true, false},
                {false, true, false}
        });

        matrix.add(new boolean[][]{
                {false, true, false},
                {true, true, false},
                {false, false, false},
        });

        matrix.add(new boolean[][]{
                {false, true, false},
                {false, true, true},
                {false, false, false}
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
