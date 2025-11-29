package com.comp2042;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Score class.
 * Tests score addition, reset functionality, and property binding.
 */
public class ScoreTest {

    private Score score;

    @BeforeEach
    public void setUp() {
        score = new Score();
    }

    @Test
    public void testInitialScoreIsZero() {
        assertEquals(0, score.scoreProperty().get(), "Initial score should be 0");
    }

    @Test
    public void testAddScore() {
        score.add(100);
        assertEquals(100, score.scoreProperty().get(), "Score should be 100 after adding 100");
    }

    @Test
    public void testAddMultipleScores() {
        score.add(50);
        score.add(30);
        score.add(20);
        assertEquals(100, score.scoreProperty().get(), "Score should be 100 after adding 50+30+20");
    }

    @Test
    public void testResetScore() {
        score.add(500);
        score.reset();
        assertEquals(0, score.scoreProperty().get(), "Score should be 0 after reset");
    }

    @Test
    public void testAddNegativeScore() {
        score.add(100);
        score.add(-50);
        assertEquals(50, score.scoreProperty().get(), "Score should handle negative additions");
    }

    @Test
    public void testScorePropertyNotNull() {
        assertNotNull(score.scoreProperty(), "Score property should not be null");
    }

    @Test
    public void testMultipleResets() {
        score.add(100);
        score.reset();
        score.add(200);
        score.reset();
        assertEquals(0, score.scoreProperty().get(), "Score should be 0 after multiple resets");
    }
}
