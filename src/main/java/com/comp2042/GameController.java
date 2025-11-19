package com.comp2042;

public class GameController implements InputEventListener {

    private Board board = new SimpleBoard(25, 10);
    private Board board2 = null; // 玩家2的游戏板（对战模式）

    private final GuiController viewGuiController;
    private boolean isVsMode = false; // 是否处于对战模式

    public GameController(GuiController c) {
        viewGuiController = c;
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
    }

    /**
     * 设置对战模式
     * @param vsMode true = 对战模式，false = 单人模式
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
     * 玩家1的下落事件（方向键控制）
     */
    @Override
    public DownData onDownEvent(MoveEvent event) {
        return handleDownEvent(board, event, false);
    }

    /**
     * 玩家2的下落事件（WASD控制）
     */
    public DownData onDownEvent2(MoveEvent event) {
        if (isVsMode && board2 != null) {
            return handleDownEvent(board2, event, true);
        }
        return null;
    }

    /**
     * 处理下落事件的通用方法
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
     * 玩家2的左移事件
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
     * 玩家2的右移事件
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
     * 玩家2的旋转事件
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
     * 获取玩家1的游戏板
     */
    public Board getBoard() {
        return board;
    }

    /**
     * 获取玩家2的游戏板
     */
    public Board getBoard2() {
        return board2;
    }

    /**
     * 是否处于对战模式
     */
    public boolean isVsMode() {
        return isVsMode;
    }
}
