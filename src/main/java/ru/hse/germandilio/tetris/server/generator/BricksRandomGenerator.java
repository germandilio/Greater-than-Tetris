package ru.hse.germandilio.tetris.server.generator;

import ru.hse.germandilio.tetris.server.bricks.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BricksRandomGenerator {
    private static final int MAX_NUMBER_IN_SEQUENCE = 81;

    private final List<Brick> brickTypes = new ArrayList<>();
    private final List<boolean[][]> generatedBrickSequence = new ArrayList<>();

    /**
     * Initialize brick generation and building bricks sequence.
     */
    public BricksRandomGenerator() {
        generateBrickTypes();
        generateBrickSequence();
    }

    /**
     * Get next brick in sequence by index.
     *
     * @param indexInSequence index
     * @return Boolean matrix (brick). For converting to String use {@link #convertToString(boolean[][])}.
     */
    public synchronized boolean[][] getBrick(int indexInSequence) {
        if (indexInSequence < generatedBrickSequence.size() && indexInSequence >= 0) {
            return generatedBrickSequence.get(indexInSequence);
        }
        return null;
    }

    /**
     * Converts brick matrix of booleans to string representation fo zeros and ones.
     *
     * @param brick brick matrix
     * @return String representation (ex. 1 0 1 0 0 0 0 ... 1)
     */
    public String convertToString(boolean[][] brick) {
        StringBuilder sb = new StringBuilder();

        for (boolean[] booleans : brick) {
            for (boolean aBoolean : booleans) {
                if (aBoolean) {
                    sb.append(1);
                } else {
                    sb.append(0);
                }
            }
        }
        return sb.toString();
    }

    private void generateBrickTypes() {
        brickTypes.add(new TBrick());
        brickTypes.add(new ShortTBrick());
        brickTypes.add(new CornerBrick());
        brickTypes.add(new IBrick());
        brickTypes.add(new JBrick());
        brickTypes.add(new OBrick());
        brickTypes.add(new SBrick());
        brickTypes.add(new LBrick());
    }

    private boolean[][] getRandomBrick() {
        int randomTypeIndex = randomInt(brickTypes.size());
        var brick = brickTypes.get(randomTypeIndex);
        return randomRotation(brick);
    }

    private void generateBrickSequence() {
        for (int i = 0; i < MAX_NUMBER_IN_SEQUENCE; i++) {
            generatedBrickSequence.add(getRandomBrick());
        }
    }

    private boolean[][] randomRotation(Brick brick) {
        int randomRotationIndex = randomInt(brick.getRotationsCount());
        return brick.getMatrix().get(randomRotationIndex);
    }

    private int randomInt(int maxValue) {
        return ThreadLocalRandom.current().nextInt(maxValue);
    }
}
