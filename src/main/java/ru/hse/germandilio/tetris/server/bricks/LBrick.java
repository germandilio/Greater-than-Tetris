package ru.hse.germandilio.tetris.server.bricks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LBrick implements Brick {
    private final List<boolean[][]> matrix = new ArrayList<>();

    public LBrick() {
        matrix.add(new boolean[][]{
                {true, false, false},
                {true, false, false},
                {true, true, true},
        });

        matrix.add(new boolean[][]{
                {false, false, true},
                {false, false, true},
                {true, true, true}
        });

        matrix.add(new boolean[][]{
                {true, true, true},
                {true, false, false},
                {true, false, false}
        });

        matrix.add(new boolean[][]{
                {true, true, true},
                {false, false, true},
                {false, false, true}
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
