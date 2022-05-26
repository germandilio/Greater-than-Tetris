package ru.hse.germandilio.tetris.server.bricks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShortTBrick implements Brick {
    private final List<boolean[][]> matrix = new ArrayList<>();

    public ShortTBrick() {
        matrix.add(new boolean[][]{
                {true, false, false},
                {true, true, false},
                {true, false, false},
        });

        matrix.add(new boolean[][]{
                {true, true, true},
                {false, true, false},
                {false, false, false}
        });

        matrix.add(new boolean[][]{
                {false, false, true},
                {false, true, true},
                {false, false, true}
        });

        matrix.add(new boolean[][]{
                {false, false, false},
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
