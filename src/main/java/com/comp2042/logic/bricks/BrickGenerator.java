package com.comp2042.logic.bricks;

/**
 * Interface for generating bricks in the game.
 * Provides methods to get the current brick and preview the next brick.
 */
public interface BrickGenerator {

    /**
     * Gets the next brick to be used in the game and removes it from the queue.
     * 
     * @return the next Brick object
     */
    Brick getBrick();

    /**
     * Previews the next brick without removing it from the queue.
     * 
     * @return the next Brick object that will be returned by getBrick()
     */
    Brick getNextBrick();
}
