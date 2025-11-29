package com.comp2042;

/**
 * Represents a movement event in the game.
 * Contains information about the type of movement and the source of the event.
 */
public final class MoveEvent {
    private final EventType eventType;
    private final EventSource eventSource;

    /**
     * Constructs a new MoveEvent with the specified type and source.
     * 
     * @param eventType the type of movement (DOWN, LEFT, RIGHT, or ROTATE)
     * @param eventSource the source of the event (USER or THREAD)
     */
    public MoveEvent(EventType eventType, EventSource eventSource) {
        this.eventType = eventType;
        this.eventSource = eventSource;
    }

    /**
     * Gets the type of this movement event.
     * 
     * @return the EventType indicating the direction or action
     */
    public EventType getEventType() {
        return eventType;
    }

    /**
     * Gets the source of this movement event.
     * 
     * @return the EventSource indicating whether this was triggered by user input or automatically
     */
    public EventSource getEventSource() {
        return eventSource;
    }
}
