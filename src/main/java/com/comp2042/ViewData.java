package com.comp2042;

/**
 * Data class containing view-related information for rendering the game state.
 * Holds the current brick data, position, and next brick preview information.
 */
public final class ViewData {

    private final int[][] brickData;
    private final int xPosition;
    private final int yPosition;
    private final int[][] nextBrickData;

    /**
     * Constructs a new ViewData object with the specified brick and position information.
     * 
     * @param brickData the 2D array representing the current brick shape
     * @param xPosition the x-coordinate (column) of the brick's position
     * @param yPosition the y-coordinate (row) of the brick's position
     * @param nextBrickData the 2D array representing the next brick shape for preview
     */
    public ViewData(int[][] brickData, int xPosition, int yPosition, int[][] nextBrickData) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.nextBrickData = nextBrickData;
    }

    /**
     * Gets a copy of the current brick's shape data.
     * 
     * @return a 2D array representing the brick shape
     */
    public int[][] getBrickData() {
        return MatrixOperations.copy(brickData);
    }

    /**
     * Gets the x-coordinate (column) of the brick's current position.
     * 
     * @return the x position
     */
    public int getxPosition() {
        return xPosition;
    }

    /**
     * Gets the y-coordinate (row) of the brick's current position.
     * 
     * @return the y position
     */
    public int getyPosition() {
        return yPosition;
    }

    /**
     * Gets a copy of the next brick's shape data for preview display.
     * 
     * @return a 2D array representing the next brick shape
     */
    public int[][] getNextBrickData() {
        return MatrixOperations.copy(nextBrickData);
    }
}
