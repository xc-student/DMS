package com.comp2042;

/**
 * Interface defining the core operations for a Tetris game board.
 * Provides methods for brick movement, rotation, collision detection, and game state management.
 */
public interface Board {

    /**
     * Moves the current brick down by one row.
     * 
     * @return true if the brick was successfully moved, false if it cannot move further down
     */
    boolean moveBrickDown();

    /**
     * Moves the current brick one column to the left.
     * 
     * @return true if the brick was successfully moved, false if blocked
     */
    boolean moveBrickLeft();

    /**
     * Moves the current brick one column to the right.
     * 
     * @return true if the brick was successfully moved, false if blocked
     */
    boolean moveBrickRight();

    /**
     * Rotates the current brick counterclockwise.
     * 
     * @return true if the brick was successfully rotated, false if rotation is blocked
     */
    boolean rotateLeftBrick();

    /**
     * Creates and spawns a new brick at the top of the board.
     * 
     * @return true if the new brick overlaps with existing blocks (game over condition), false otherwise
     */
    boolean createNewBrick();

    /**
     * Gets the current state of the game board matrix.
     * 
     * @return a 2D array representing the board, where non-zero values indicate filled cells
     */
    int[][] getBoardMatrix();

    /**
     * Gets the current view data including brick position and shape.
     * 
     * @return ViewData object containing information needed to render the current game state
     */
    ViewData getViewData();

    /**
     * Merges the current brick into the background board when it can no longer move.
     */
    void mergeBrickToBackground();

    /**
     * Checks for and removes any complete rows from the board.
     * 
     * @return ClearRow object containing information about removed rows and score bonus
     */
    ClearRow clearRows();

    /**
     * Gets the score tracker for this board.
     * 
     * @return the Score object managing the current score
     */
    Score getScore();

    /**
     * Resets the board to start a new game.
     * Clears all blocks, resets the score, and creates the first brick.
     */
    void newGame();
}
