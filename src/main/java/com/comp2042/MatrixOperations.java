package com.comp2042;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class providing static methods for matrix operations used in the Tetris game.
 * Includes methods for collision detection, matrix copying, merging, and row clearing.
 */
public class MatrixOperations {


    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private MatrixOperations(){

    }

    /**
     * Checks if a brick intersects with the board boundaries or existing blocks.
     * 
     * @param matrix the game board matrix
     * @param brick the brick shape matrix to check
     * @param x the x-coordinate (column) where the brick would be placed
     * @param y the y-coordinate (row) where the brick would be placed
     * @return true if there is an intersection (collision), false otherwise
     */
    public static boolean intersect(final int[][] matrix, final int[][] brick, int x, int y) {
        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {
                int targetX = x + i;
                int targetY = y + j;
                if (brick[j][i] != 0 && (checkOutOfBound(matrix, targetX, targetY) || matrix[targetY][targetX] != 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if the specified coordinates are out of bounds for the given matrix.
     * 
     * @param matrix the matrix to check bounds against
     * @param targetX the x-coordinate to check
     * @param targetY the y-coordinate to check
     * @return true if the coordinates are out of bounds, false otherwise
     */
    private static boolean checkOutOfBound(int[][] matrix, int targetX, int targetY) {
        boolean returnValue = true;
        if (targetX >= 0 && targetY < matrix.length && targetX < matrix[targetY].length) {
            returnValue = false;
        }
        return returnValue;
    }

    /**
     * Creates a deep copy of a 2D integer array.
     * 
     * @param original the original 2D array to copy
     * @return a new 2D array with the same values as the original
     */
    public static int[][] copy(int[][] original) {
        int[][] myInt = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            int[] aMatrix = original[i];
            int aLength = aMatrix.length;
            myInt[i] = new int[aLength];
            System.arraycopy(aMatrix, 0, myInt[i], 0, aLength);
        }
        return myInt;
    }

    /**
     * Merges a brick into the game board at the specified position.
     * 
     * @param filledFields the current game board matrix
     * @param brick the brick shape matrix to merge
     * @param x the x-coordinate (column) where the brick should be merged
     * @param y the y-coordinate (row) where the brick should be merged
     * @return a new matrix with the brick merged into the board
     */
    public static int[][] merge(int[][] filledFields, int[][] brick, int x, int y) {
        int[][] copy = copy(filledFields);
        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {
                int targetX = x + i;
                int targetY = y + j;
                if (brick[j][i] != 0) {
                    copy[targetY][targetX] = brick[j][i];
                }
            }
        }
        return copy;
    }

    /**
     * Checks for and removes complete rows from the game board.
     * Calculates the score bonus based on the number of rows cleared.
     * 
     * @param matrix the game board matrix to check
     * @return ClearRow object containing the number of cleared rows, updated matrix, and score bonus
     */
    public static ClearRow checkRemoving(final int[][] matrix) {
        int[][] tmp = new int[matrix.length][matrix[0].length];
        Deque<int[]> newRows = new ArrayDeque<>();
        List<Integer> clearedRows = new ArrayList<>();

        for (int i = 0; i < matrix.length; i++) {
            int[] tmpRow = new int[matrix[i].length];
            boolean rowToClear = true;
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] == 0) {
                    rowToClear = false;
                }
                tmpRow[j] = matrix[i][j];
            }
            if (rowToClear) {
                clearedRows.add(i);
            } else {
                newRows.add(tmpRow);
            }
        }
        for (int i = matrix.length - 1; i >= 0; i--) {
            int[] row = newRows.pollLast();
            if (row != null) {
                tmp[i] = row;
            } else {
                break;
            }
        }
        int scoreBonus = 50 * clearedRows.size() * clearedRows.size();
        return new ClearRow(clearedRows.size(), tmp, scoreBonus);
    }

    /**
     * Creates a deep copy of a list of 2D integer arrays.
     * 
     * @param list the list of 2D arrays to copy
     * @return a new list containing deep copies of all matrices
     */
    public static List<int[][]> deepCopyList(List<int[][]> list){
        return list.stream().map(MatrixOperations::copy).collect(Collectors.toList());
    }

}
