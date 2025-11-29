package com.comp2042;

/**
 * Enum representing the types of movement events in the game.
 * Defines the possible directions and actions a brick can take.
 */
public enum EventType {
    /** Move the brick down by one row */
    DOWN,
    /** Move the brick one column to the left */
    LEFT,
    /** Move the brick one column to the right */
    RIGHT,
    /** Rotate the brick counterclockwise */
    ROTATE
}
