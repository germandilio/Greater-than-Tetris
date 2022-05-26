package ru.hse.germandilio.tetris.server.bricks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TBrick implements Brick {
    private final List<boolean[][]> matrix = new ArrayList<>();

    public TBrick() {
        matrix.add(new boolean[][]{
                {true, false, false},
                {true, true, true},
                {true, false, false},
        });

        matrix.add(new boolean[][]{
                {true, true, true},
                {false, true, false},
                {false, true, false}
        });

        matrix.add(new boolean[][]{
                {false, false, true},
                {true, true, true},
                {false, false, true}
        });

        matrix.add(new boolean[][]{
                {false, true, false},
                {false, true, false},
                {true, true, true}
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
