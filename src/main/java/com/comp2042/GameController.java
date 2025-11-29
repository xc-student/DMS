package com.comp2042;

/**
 * Main game controller that manages game logic and coordinates between the model and view.
 * Implements InputEventListener to handle user input events for both single-player and versus modes.
 * This class manages up to two game boards for versus mode gameplay.
 */
public class GameController implements InputEventListener {

    private Board board = new SimpleBoard(25, 10);
    private Board board2 = null; // Player 2's game board (versus mode)

    private final GuiController viewGuiController;
    private boolean isVsMode = false; // Whether in versus mode

    /**
     * Constructs a new GameController and initializes the game.
     * Sets up the first game board, binds it to the GUI controller, and initializes the game view.
     * 
     * @param c the GUI controller that manages the visual representation of the game
     */
    public GameController(GuiController c) {
        viewGuiController = c;
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
    }

    /**
     * Sets the versus mode for two-player gameplay.
     * When enabled, creates and initializes a second game board for player 2.
     * 
     * @param vsMode true to enable versus mode, false for single-player mode
     */
    public void setVsMode(boolean vsMode) {
        isVsMode = vsMode;
        if (vsMode) {
            // 创建玩家2的游戏板
            if (board2 == null) {
                board2 = new SimpleBoard(25, 10);
                board2.createNewBrick();
            }
            viewGuiController.initVsModeView(board2.getBoardMatrix(), board2.getViewData());
            viewGuiController.bindScore2(board2.getScore().scoreProperty());
        } else {
            // 关闭对战模式，清理玩家2的游戏板
            board2 = null;
            viewGuiController.hideVsModeView();
        }
    }

    /**
     * Handles the down movement event for player 1 (controlled by arrow keys).
     * 
     * @param event the movement event triggered by user input
     * @return DownData containing information about cleared rows and updated view data
     */
    @Override
    public DownData onDownEvent(MoveEvent event) {
        return handleDownEvent(board, event, false);
    }

    /**
     * Handles the down movement event for player 2 (controlled by WASD keys).
     * Only active in versus mode.
     * 
     * @param event the movement event triggered by user input
     * @return DownData containing information about cleared rows and updated view data, or null if not in versus mode
     */
    public DownData onDownEvent2(MoveEvent event) {
        if (isVsMode && board2 != null) {
            return handleDownEvent(board2, event, true);
        }
        return null;
    }

    /**
     * Common method to handle down movement events for any player.
     * Manages brick movement, merging, row clearing, scoring, and game over detection.
     * 
     * @param targetBoard the game board to operate on
     * @param event the movement event
     * @param isPlayer2 true if this is for player 2, false for player 1
     * @return DownData containing cleared row information and updated view data
     */
    private DownData handleDownEvent(Board targetBoard, MoveEvent event, boolean isPlayer2) {
        boolean canMove = targetBoard.moveBrickDown();
        ClearRow clearRow = null;
        if (!canMove) {
            targetBoard.mergeBrickToBackground();
            clearRow = targetBoard.clearRows();
            if (clearRow.getLinesRemoved() > 0) {
                targetBoard.getScore().add(clearRow.getScoreBonus());
            }
            if (targetBoard.createNewBrick()) {
                if (isPlayer2) {
                    viewGuiController.gameOver2();
                } else {
                    viewGuiController.gameOver();
                }
            }

            if (isPlayer2) {
                viewGuiController.refreshGameBackground2(targetBoard.getBoardMatrix());
            } else {
                viewGuiController.refreshGameBackground(targetBoard.getBoardMatrix());
            }

        } else {
            if (event.getEventSource() == EventSource.USER) {
                targetBoard.getScore().add(1);
            }
        }
        return new DownData(clearRow, targetBoard.getViewData());
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    /**
     * Handles the left movement event for player 2.
     * Only active in versus mode.
     * 
     * @param event the movement event triggered by user input
     * @return ViewData containing updated brick position and shape, or null if not in versus mode
     */
    public ViewData onLeftEvent2(MoveEvent event) {
        if (isVsMode && board2 != null) {
            board2.moveBrickLeft();
            return board2.getViewData();
        }
        return null;
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    /**
     * Handles the right movement event for player 2.
     * Only active in versus mode.
     * 
     * @param event the movement event triggered by user input
     * @return ViewData containing updated brick position and shape, or null if not in versus mode
     */
    public ViewData onRightEvent2(MoveEvent event) {
        if (isVsMode && board2 != null) {
            board2.moveBrickRight();
            return board2.getViewData();
        }
        return null;
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }

    /**
     * Handles the rotation event for player 2.
     * Only active in versus mode.
     * 
     * @param event the movement event triggered by user input
     * @return ViewData containing updated brick position and shape, or null if not in versus mode
     */
    public ViewData onRotateEvent2(MoveEvent event) {
        if (isVsMode && board2 != null) {
            board2.rotateLeftBrick();
            return board2.getViewData();
        }
        return null;
    }

    @Override
    public void createNewGame() {
        board.newGame();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        
        // 如果是对战模式，也重置玩家2
        if (isVsMode && board2 != null) {
            board2.newGame();
            viewGuiController.refreshGameBackground2(board2.getBoardMatrix());
        }
    }

    /**
     * Gets the game board for player 1.
     * 
     * @return the Board instance for player 1
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Gets the game board for player 2.
     * 
     * @return the Board instance for player 2, or null if not in versus mode
     */
    public Board getBoard2() {
        return board2;
    }

    /**
     * Checks if the game is currently in versus mode.
     * 
     * @return true if in versus mode, false otherwise
     */
    public boolean isVsMode() {
        return isVsMode;
    }
}
