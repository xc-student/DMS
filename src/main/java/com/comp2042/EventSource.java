package com.comp2042;

/**
 * Enum representing the source of a game event.
 * Distinguishes between user-initiated actions and automatic system actions.
 */
public enum EventSource {
    /** Event triggered by user input (keyboard) */
    USER,
    /** Event triggered automatically by the game thread (e.g., automatic brick descent) */
    THREAD
}
