package com.comp2042.logic.bricks;

import com.comp2042.MatrixOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the O-shaped Tetris brick (tetromino).
 * This brick is a 2x2 square and has only 1 rotation state (it looks the same when rotated).
 */
final class OBrick implements Brick {

    private final List<int[][]> brickMatrix = new ArrayList<>();

    /**
     * Constructs an O-brick and initializes its rotation matrix.
     * Since the O-piece is symmetrical, it only has one orientation.
     */
    public OBrick() {
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 4, 4, 0},
                {0, 4, 4, 0},
                {0, 0, 0, 0}
        });
    }

    /**
     * Gets a deep copy of all rotation matrices for this brick.
     * 
     * @return a list containing a single 2D array (the O-piece has only one rotation state)
     */
    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }

}
