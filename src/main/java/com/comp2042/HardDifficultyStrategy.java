package com.comp2042;

/**
 * Hard difficulty strategy implementation.
 * Provides faster fall speed and higher score multiplier for experienced players.
 */
public class HardDifficultyStrategy implements DifficultyStrategy {
    
    private static final long FALL_INTERVAL = 200; // milliseconds (faster than easy)
    private static final int SCORE_MULTIPLIER = 2; // double points
    
    @Override
    public long getFallInterval() {
        return FALL_INTERVAL;
    }
    
    @Override
    public int getScoreMultiplier() {
        return SCORE_MULTIPLIER;
    }
    
    @Override
    public String getDifficultyName() {
        return "Hard";
    }
}
