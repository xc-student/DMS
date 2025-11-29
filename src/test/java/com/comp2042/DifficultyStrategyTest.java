package com.comp2042;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for difficulty strategy implementations.
 * Tests the Strategy design pattern implementation for difficulty levels.
 */
public class DifficultyStrategyTest {

    @Test
    public void testEasyDifficultyFallInterval() {
        DifficultyStrategy easy = new EasyDifficultyStrategy();
        assertEquals(400, easy.getFallInterval(), "Easy difficulty should have 400ms fall interval");
    }

    @Test
    public void testEasyDifficultyScoreMultiplier() {
        DifficultyStrategy easy = new EasyDifficultyStrategy();
        assertEquals(1, easy.getScoreMultiplier(), "Easy difficulty should have 1x score multiplier");
    }

    @Test
    public void testEasyDifficultyName() {
        DifficultyStrategy easy = new EasyDifficultyStrategy();
        assertEquals("Easy", easy.getDifficultyName(), "Easy difficulty should have correct name");
    }

    @Test
    public void testHardDifficultyFallInterval() {
        DifficultyStrategy hard = new HardDifficultyStrategy();
        assertEquals(200, hard.getFallInterval(), "Hard difficulty should have 200ms fall interval");
    }

    @Test
    public void testHardDifficultyScoreMultiplier() {
        DifficultyStrategy hard = new HardDifficultyStrategy();
        assertEquals(2, hard.getScoreMultiplier(), "Hard difficulty should have 2x score multiplier");
    }

    @Test
    public void testHardDifficultyName() {
        DifficultyStrategy hard = new HardDifficultyStrategy();
        assertEquals("Hard", hard.getDifficultyName(), "Hard difficulty should have correct name");
    }

    @Test
    public void testHardIsFasterThanEasy() {
        DifficultyStrategy easy = new EasyDifficultyStrategy();
        DifficultyStrategy hard = new HardDifficultyStrategy();
        assertTrue(hard.getFallInterval() < easy.getFallInterval(), 
                   "Hard difficulty should be faster (shorter interval) than easy");
    }

    @Test
    public void testHardHasHigherScoreMultiplier() {
        DifficultyStrategy easy = new EasyDifficultyStrategy();
        DifficultyStrategy hard = new HardDifficultyStrategy();
        assertTrue(hard.getScoreMultiplier() > easy.getScoreMultiplier(),
                   "Hard difficulty should have higher score multiplier than easy");
    }

    @Test
    public void testStrategyPolymorphism() {
        DifficultyStrategy[] strategies = {
            new EasyDifficultyStrategy(),
            new HardDifficultyStrategy()
        };
        
        for (DifficultyStrategy strategy : strategies) {
            assertNotNull(strategy.getDifficultyName(), "Strategy should have a name");
            assertTrue(strategy.getFallInterval() > 0, "Fall interval should be positive");
            assertTrue(strategy.getScoreMultiplier() > 0, "Score multiplier should be positive");
        }
    }
}
