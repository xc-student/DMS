package com.comp2042;

/**
 * Data class containing information about the next rotation state of a brick.
 * Holds both the shape matrix and the rotation position index.
 */
public final class NextShapeInfo {

    private final int[][] shape;
    private final int position;

    /**
     * Constructs a new NextShapeInfo with the specified shape and position.
     * 
     * @param shape the 2D array representing the brick shape at the next rotation
     * @param position the rotation index (0-based) in the brick's rotation sequence
     */
    public NextShapeInfo(final int[][] shape, final int position) {
        this.shape = shape;
        this.position = position;
    }

    /**
     * Gets a copy of the shape matrix for the next rotation.
     * 
     * @return a 2D array representing the brick shape
     */
    public int[][] getShape() {
        return MatrixOperations.copy(shape);
    }

    /**
     * Gets the rotation position index.
     * 
     * @return the rotation index in the brick's rotation sequence
     */
    public int getPosition() {
        return position;
    }
}
