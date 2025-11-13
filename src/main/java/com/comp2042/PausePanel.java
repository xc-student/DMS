package com.comp2042;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;


public class PausePanel extends BorderPane {

    public PausePanel() {
        final Label pauseLabel = new Label("Game Paused");
        pauseLabel.getStyleClass().add("pauseStyle");
        setCenter(pauseLabel);
    }

}

