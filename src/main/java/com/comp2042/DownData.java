package com.comp2042;

/**
 * Data class containing the result of a downward movement event.
 * Combines information about cleared rows and updated view data.
 */
public final class DownData {
    private final ClearRow clearRow;
    private final ViewData viewData;

    /**
     * Constructs a new DownData object with clear row and view information.
     * 
     * @param clearRow information about cleared rows, or null if no rows were cleared
     * @param viewData the updated view data after the movement
     */
    public DownData(ClearRow clearRow, ViewData viewData) {
        this.clearRow = clearRow;
        this.viewData = viewData;
    }

    /**
     * Gets the clear row information.
     * 
     * @return ClearRow object containing information about cleared rows, or null if none
     */
    public ClearRow getClearRow() {
        return clearRow;
    }

    /**
     * Gets the updated view data.
     * 
     * @return ViewData object containing current brick position and shape information
     */
    public ViewData getViewData() {
        return viewData;
    }
}
