package com.comp2042;

import com.comp2042.logic.bricks.Brick;

/**
 * Manages brick rotation logic for the game.
 * Keeps track of the current rotation state and provides methods to rotate bricks.
 */
public class BrickRotator {

    private Brick brick;
    private int currentShape = 0;

    /**
     * Gets information about the next rotation state of the current brick.
     * 
     * @return NextShapeInfo containing the shape matrix and position index of the next rotation
     */
    public NextShapeInfo getNextShape() {
        int nextShape = currentShape;
        nextShape = (++nextShape) % brick.getShapeMatrix().size();
        return new NextShapeInfo(brick.getShapeMatrix().get(nextShape), nextShape);
    }

    /**
     * Gets the current shape matrix of the brick at its current rotation.
     * 
     * @return a 2D array representing the current brick shape
     */
    public int[][] getCurrentShape() {
        return brick.getShapeMatrix().get(currentShape);
    }

    /**
     * Sets the current rotation index of the brick.
     * 
     * @param currentShape the rotation index to set
     */
    public void setCurrentShape(int currentShape) {
        this.currentShape = currentShape;
    }

    /**
     * Sets a new brick and resets the rotation to the initial state.
     * 
     * @param brick the new Brick object to manage
     */
    public void setBrick(Brick brick) {
        this.brick = brick;
        currentShape = 0;
    }


}
