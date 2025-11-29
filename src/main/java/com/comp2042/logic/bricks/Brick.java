package com.comp2042.logic.bricks;

import java.util.List;

/**
 * Interface representing a Tetris brick (tetromino).
 * Provides access to the brick's shape matrices for all rotation states.
 */
public interface Brick {

    /**
     * Gets the list of shape matrices for all rotation states of this brick.
     * Each matrix in the list represents one rotation state.
     * 
     * @return a list of 2D arrays, where each array represents the brick shape at a specific rotation
     */
    List<int[][]> getShapeMatrix();
}
