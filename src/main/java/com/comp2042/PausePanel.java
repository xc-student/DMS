package com.comp2042;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;


/**
 * UI panel displayed when the game is paused.
 * Shows a "Game Paused" message to the player.
 */
public class PausePanel extends BorderPane {

    /**
     * Constructs a new PausePanel with a centered pause message.
     * Applies the "pauseStyle" CSS class for styling.
     */
    public PausePanel() {
        final Label pauseLabel = new Label("Game \tPaused");
        pauseLabel.getStyleClass().add("pauseStyle");
        setCenter(pauseLabel);
    }

}
