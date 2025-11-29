package com.comp2042;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the MatrixOperations utility class.
 * Tests matrix operations including copying, merging, and row clearing.
 */
public class MatrixOperationsTest {

    @Test
    public void testCopyMatrix() {
        int[][] original = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };
        int[][] copy = MatrixOperations.copy(original);
        
        assertArrayEquals(original, copy, "Copied matrix should have same values");
        assertNotSame(original, copy, "Copied matrix should be a different instance");
    }

    @Test
    public void testCopyMatrixIndependence() {
        int[][] original = {{1, 2}, {3, 4}};
        int[][] copy = MatrixOperations.copy(original);
        
        // Modify copy
        copy[0][0] = 99;
        
        assertEquals(1, original[0][0], "Original should not be affected by copy modification");
        assertEquals(99, copy[0][0], "Copy should be modified");
    }

    @Test
    public void testCopyEmptyMatrix() {
        int[][] original = new int[5][5];
        int[][] copy = MatrixOperations.copy(original);
        
        assertNotSame(original, copy, "Copied matrix should be a different instance");
        assertEquals(5, copy.length, "Copied matrix should have same dimensions");
        assertEquals(5, copy[0].length, "Copied matrix should have same dimensions");
    }

    @Test
    public void testCheckRemovingNoCompleteRows() {
        int[][] board = new int[10][10];
        board[9][0] = 1; // Incomplete row
        
        ClearRow result = MatrixOperations.checkRemoving(board);
        
        assertEquals(0, result.getLinesRemoved(), "Should not remove incomplete rows");
        assertEquals(0, result.getScoreBonus(), "Score bonus should be 0");
    }

    @Test
    public void testCheckRemovingOneCompleteRow() {
        int[][] board = new int[10][10];
        // Fill bottom row completely
        for (int j = 0; j < 10; j++) {
            board[9][j] = 1;
        }
        
        ClearRow result = MatrixOperations.checkRemoving(board);
        
        assertEquals(1, result.getLinesRemoved(), "Should remove one complete row");
        assertEquals(50, result.getScoreBonus(), "Score bonus should be 50 for 1 row");
    }

    @Test
    public void testCheckRemovingMultipleRows() {
        int[][] board = new int[10][10];
        // Fill bottom two rows completely
        for (int i = 8; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                board[i][j] = 1;
            }
        }
        
        ClearRow result = MatrixOperations.checkRemoving(board);
        
        assertEquals(2, result.getLinesRemoved(), "Should remove two complete rows");
        assertEquals(200, result.getScoreBonus(), "Score bonus should be 200 for 2 rows (50*2*2)");
    }

    @Test
    public void testCheckRemovingFourRows() {
        int[][] board = new int[10][10];
        // Fill bottom four rows completely (Tetris!)
        for (int i = 6; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                board[i][j] = 1;
            }
        }
        
        ClearRow result = MatrixOperations.checkRemoving(board);
        
        assertEquals(4, result.getLinesRemoved(), "Should remove four complete rows");
        assertEquals(800, result.getScoreBonus(), "Score bonus should be 800 for 4 rows (50*4*4)");
    }

    @Test
    public void testDeepCopyList() {
        int[][] matrix1 = {{1, 2}, {3, 4}};
        int[][] matrix2 = {{5, 6}, {7, 8}};
        java.util.List<int[][]> original = java.util.Arrays.asList(matrix1, matrix2);
        
        java.util.List<int[][]> copied = MatrixOperations.deepCopyList(original);
        
        assertEquals(2, copied.size(), "Copied list should have same size");
        assertNotSame(original.get(0), copied.get(0), "Matrices should be deep copied");
        
        // Modify copied matrix
        copied.get(0)[0][0] = 99;
        assertEquals(1, original.get(0)[0][0], "Original should not be affected");
    }

    @Test
    public void testCheckRemovingPreservesNonCompleteRows() {
        int[][] board = new int[10][10];
        // Fill bottom row completely
        for (int j = 0; j < 10; j++) {
            board[9][j] = 1;
        }
        // Add some blocks in row 8 (incomplete)
        board[8][0] = 1;
        board[8][1] = 1;
        
        ClearRow result = MatrixOperations.checkRemoving(board);
        
        assertEquals(1, result.getLinesRemoved(), "Should remove only complete row");
        // Check that incomplete row is preserved
        int[][] newBoard = result.getNewMatrix();
        assertEquals(1, newBoard[9][0], "Incomplete row should be preserved at bottom");
        assertEquals(1, newBoard[9][1], "Incomplete row should be preserved at bottom");
    }
}

