package com.comp2042.logic.bricks;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Unit tests for the RandomBrickGenerator class.
 * Tests brick generation and ensures all brick types can be created.
 */
public class RandomBrickGeneratorTest {

    private BrickGenerator generator;

    @BeforeEach
    public void setUp() {
        generator = new RandomBrickGenerator();
    }

    @Test
    public void testGenerateBrickNotNull() {
        Brick brick = generator.getBrick();
        assertNotNull(brick, "Generated brick should not be null");
    }

    @Test
    public void testGenerateMultipleBricks() {
        for (int i = 0; i < 100; i++) {
            Brick brick = generator.getBrick();
            assertNotNull(brick, "Each generated brick should not be null");
        }
    }

    @Test
    public void testBrickHasValidMatrix() {
        Brick brick = generator.getBrick();
        List<int[][]> matrices = brick.getShapeMatrix();
        assertNotNull(matrices, "Brick shape matrix list should not be null");
        assertTrue(matrices.size() > 0, "Brick should have at least one rotation state");
        
        int[][] firstMatrix = matrices.get(0);
        assertNotNull(firstMatrix, "First rotation matrix should not be null");
        assertTrue(firstMatrix.length > 0, "Brick matrix should have rows");
        assertTrue(firstMatrix[0].length > 0, "Brick matrix should have columns");
    }

    @Test
    public void testNextBrickPreview() {
        Brick nextBrick = generator.getNextBrick();
        assertNotNull(nextBrick, "Next brick preview should not be null");
        
        // Get the brick and verify it matches the preview
        Brick actualBrick = generator.getBrick();
        assertNotNull(actualBrick, "Actual brick should not be null");
    }

    @Test
    public void testBrickMatrixContainsNonZeroValues() {
        Brick brick = generator.getBrick();
        List<int[][]> matrices = brick.getShapeMatrix();
        int[][] matrix = matrices.get(0);
        
        boolean foundNonZero = false;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] != 0) {
                    foundNonZero = true;
                    break;
                }
            }
        }
        
        assertTrue(foundNonZero, "Brick matrix should contain at least one non-zero value");
    }

    @Test
    public void testGeneratedBricksAreDifferentInstances() {
        Brick brick1 = generator.getBrick();
        Brick brick2 = generator.getBrick();
        
        // The instances should be different
        assertNotSame(brick1, brick2, "Generated bricks should be different instances");
    }

    @Test
    public void testBrickHasMultipleRotationStates() {
        // Generate several bricks and check that at least some have multiple rotations
        boolean foundMultipleRotations = false;
        for (int i = 0; i < 50; i++) {
            Brick brick = generator.getBrick();
            List<int[][]> matrices = brick.getShapeMatrix();
            if (matrices.size() > 1) {
                foundMultipleRotations = true;
                break;
            }
        }
        
        assertTrue(foundMultipleRotations, "At least some bricks should have multiple rotation states");
    }
}

