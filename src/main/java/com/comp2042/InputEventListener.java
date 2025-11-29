package com.comp2042;

/**
 * Interface for listening to and handling user input events in the game.
 * Implementations of this interface respond to movement, rotation, and game control events.
 */
public interface InputEventListener {

    /**
     * Handles the down movement event when the user presses the down key.
     * 
     * @param event the movement event containing event source information
     * @return DownData containing information about cleared rows and updated view state
     */
    DownData onDownEvent(MoveEvent event);

    /**
     * Handles the left movement event when the user presses the left key.
     * 
     * @param event the movement event containing event source information
     * @return ViewData containing updated brick position and shape information
     */
    ViewData onLeftEvent(MoveEvent event);

    /**
     * Handles the right movement event when the user presses the right key.
     * 
     * @param event the movement event containing event source information
     * @return ViewData containing updated brick position and shape information
     */
    ViewData onRightEvent(MoveEvent event);

    /**
     * Handles the rotation event when the user presses the rotation key.
     * 
     * @param event the movement event containing event source information
     * @return ViewData containing updated brick position and shape information
     */
    ViewData onRotateEvent(MoveEvent event);

    /**
     * Creates and initializes a new game, resetting all game state.
     */
    void createNewGame();
}
