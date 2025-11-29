package com.comp2042.logic.bricks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Generates bricks randomly from the standard set of Tetris pieces.
 * Maintains a queue of upcoming bricks to ensure smooth gameplay and allow for next brick preview.
 */
public class RandomBrickGenerator implements BrickGenerator {

    private final List<Brick> brickList;

    private final Deque<Brick> nextBricks = new ArrayDeque<>();

    /**
     * Constructs a new RandomBrickGenerator and initializes the brick queue.
     * Creates the standard set of 7 Tetris pieces (I, J, L, O, S, T, Z) and pre-generates the first two bricks.
     */
    public RandomBrickGenerator() {
        brickList = new ArrayList<>();
        brickList.add(new IBrick());
        brickList.add(new JBrick());
        brickList.add(new LBrick());
        brickList.add(new OBrick());
        brickList.add(new SBrick());
        brickList.add(new TBrick());
        brickList.add(new ZBrick());
        nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
        nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
    }

    /**
     * Gets the next brick from the queue and generates a new one to maintain the queue.
     * Ensures there is always at least one brick in the queue for preview.
     * 
     * @return the next Brick to be used in the game
     */
    @Override
    public Brick getBrick() {
        if (nextBricks.size() <= 1) {
            nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
        }
        return nextBricks.poll();
    }

    /**
     * Previews the next brick without removing it from the queue.
     * 
     * @return the Brick that will be returned by the next call to getBrick()
     */
    @Override
    public Brick getNextBrick() {
        return nextBricks.peek();
    }
}
