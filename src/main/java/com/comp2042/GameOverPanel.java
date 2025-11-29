package com.comp2042;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;


/**
 * UI panel displayed when the game is over.
 * Shows a "GAME OVER" message to the player.
 */
public class GameOverPanel extends BorderPane {

    /**
     * Constructs a new GameOverPanel with a centered game over message.
     * Applies the "gameOverStyle" CSS class for styling.
     */
    public GameOverPanel() {
        final Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.getStyleClass().add("gameOverStyle");
        setCenter(gameOverLabel);
    }

}
