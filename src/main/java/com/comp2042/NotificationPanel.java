package com.comp2042;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * UI panel for displaying animated score notifications.
 * Shows bonus points or score updates with fade and slide animations.
 */
public class NotificationPanel extends BorderPane {

    /**
     * Constructs a new NotificationPanel with the specified text.
     * Applies the "bonusStyle" CSS class for styling.
     * 
     * @param text the notification text to display (e.g., "+100 points")
     */
    public NotificationPanel(String text) {
        setMinHeight(200);
        setMinWidth(220);
        final Label score = new Label(text);
        score.getStyleClass().add("bonusStyle");
        // Removed hardcoded effects to use CSS styling
        // final Effect glow = new Glow(0.6);
        // score.setEffect(glow);
        // score.setTextFill(Color.WHITE);
        setCenter(score);

    }

    /**
     * Displays the notification with an animated fade and upward slide effect.
     * Automatically removes the panel from the provided list when the animation completes.
     * 
     * @param list the observable list of nodes containing this panel
     */
    public void showScore(ObservableList<Node> list) {
        FadeTransition ft = new FadeTransition(Duration.millis(2000), this);
        TranslateTransition tt = new TranslateTransition(Duration.millis(2500), this);
        tt.setToY(this.getLayoutY() - 40);
        ft.setFromValue(1);
        ft.setToValue(0);
        ParallelTransition transition = new ParallelTransition(tt, ft);
        transition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                list.remove(NotificationPanel.this);
            }
        });
        transition.play();
    }
}
