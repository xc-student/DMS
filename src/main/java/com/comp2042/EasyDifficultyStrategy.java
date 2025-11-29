package com.comp2042;

/**
 * Easy difficulty strategy implementation.
 * Provides slower fall speed and standard scoring for beginner players.
 */
public class EasyDifficultyStrategy implements DifficultyStrategy {
    
    private static final long FALL_INTERVAL = 400; // milliseconds
    private static final int SCORE_MULTIPLIER = 1;
    
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
        return "Easy";
    }
}
