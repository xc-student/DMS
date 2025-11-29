package com.comp2042;

/**
 * Data class containing information about cleared rows after a brick is placed.
 * Includes the number of lines removed, the updated board matrix, and the score bonus earned.
 */
public final class ClearRow {

    private final int linesRemoved;
    private final int[][] newMatrix;
    private final int scoreBonus;

    /**
     * Constructs a new ClearRow object with the specified clear row information.
     * 
     * @param linesRemoved the number of complete rows that were removed
     * @param newMatrix the updated board matrix after row removal
     * @param scoreBonus the score bonus earned from clearing rows
     */
    public ClearRow(int linesRemoved, int[][] newMatrix, int scoreBonus) {
        this.linesRemoved = linesRemoved;
        this.newMatrix = newMatrix;
        this.scoreBonus = scoreBonus;
    }

    /**
     * Gets the number of lines that were removed.
     * 
     * @return the count of removed lines
     */
    public int getLinesRemoved() {
        return linesRemoved;
    }

    /**
     * Gets a copy of the updated board matrix after row removal.
     * 
     * @return a 2D array representing the new board state
     */
    public int[][] getNewMatrix() {
        return MatrixOperations.copy(newMatrix);
    }

    /**
     * Gets the score bonus earned from clearing rows.
     * 
     * @return the score bonus value
     */
    public int getScoreBonus() {
        return scoreBonus;
    }
}
