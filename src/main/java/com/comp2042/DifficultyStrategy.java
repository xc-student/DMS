package com.comp2042;

/**
 * Strategy interface for defining different difficulty levels in the game.
 * Implements the Strategy design pattern to allow flexible difficulty configurations.
 * 
 * Different implementations can provide varying game speeds, scoring multipliers,
 * and other difficulty-related parameters.
 */
public interface DifficultyStrategy {
    
    /**
     * Gets the time interval between automatic brick falls.
     * 
     * @return the fall interval in milliseconds
     */
    long getFallInterval();
    
    /**
     * Gets the score multiplier for this difficulty level.
     * Higher difficulties may award more points.
     * 
     * @return the score multiplier (e.g., 1 for normal, 2 for double points)
     */
    int getScoreMultiplier();
    
    /**
     * Gets the name of this difficulty level.
     * 
     * @return the difficulty name (e.g., "Easy", "Hard")
     */
    String getDifficultyName();
}
