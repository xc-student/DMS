package com.comp2042;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 * GUI controller class
 * Handles user interface display and user input events
 * Implements Initializable interface, automatically initializes after FXML is loaded
 * 
 * Main features:
 * 1. Handles keyboard input events (arrow keys, rotation, etc.)
 * 2. Manages game rendering (bricks, background)
 * 3. Controls game animations (automatic descent)
 * 4. Manages game state (pause, end, restart)
 * 
 * @author XiangCan
 * @version 1.0
 */
public class GuiController implements Initializable {

    // 方块显示大小常量（像素）
    private static final int BRICK_SIZE = 20;

    // FXML注入的游戏板容器
    @FXML
    private javafx.scene.layout.BorderPane gameBoard;
    
    // FXML注入的游戏面板，用于显示游戏背景
    @FXML
    private GridPane gamePanel;

    // FXML注入的通知组，用于显示消除行的分数提示
    @FXML
    private Group groupNotification;

    // FXML注入的方块面板，用于显示当前下落的方块
    @FXML
    private GridPane brickPanel;

    // FXML注入的游戏结束面板
    @FXML
    private GameOverPanel gameOverPanel;

    // FXML注入的暂停面板
    @FXML
    private PausePanel pausePanel;

    // FXML注入的分数标签
    @FXML
    private Label scoreLabel;
    
    // FXML注入的按钮
    @FXML
    private javafx.scene.control.Button newGameButton;
    
    @FXML
    private javafx.scene.control.Button pauseButton;

    // FXML注入的下一个方块预览面板
    @FXML
    private GridPane nextBrickPanel;
    @FXML
    private VBox player1Options;

    // 玩家1与玩家2面板的布局位置
    private static final double SINGLE_PLAYER_BOARD_X = 900;
    private static final double SINGLE_PLAYER_OPTIONS_X = 1200;
    private static final double VS_PLAYER1_BOARD_X = 1300;
    private static final double VS_PLAYER2_BOARD_X = 500;
    private static final double VS_PLAYER1_OPTIONS_X = 1600;
    private static final double VS_PLAYER2_OPTIONS_X = 800;
    private static final double PLAYER2_BOARD_DEFAULT_X = 420;
    private static final double PLAYER2_OPTIONS_DEFAULT_X = 655;
    private static final double NOTIFICATION_OFFSET_X = 20;
    private static final double NOTIFICATION_OFFSET_Y = 40;
    
    // 游戏背景显示矩阵，存储每个格子的Rectangle对象
    private Rectangle[][] displayMatrix;

    // 输入事件监听器，用于处理用户输入
    private InputEventListener eventListener;

    // 当前方块显示矩阵，存储方块每个部分的Rectangle对象
    private Rectangle[][] rectangles;

    // 下一个方块显示矩阵，存储下一个方块每个部分的Rectangle对象
    private Rectangle[][] nextBrickRectangles;

    // 游戏自动下落的时间线动画
    private Timeline timeLine;
    // 自动加分的时间线动画
    private Timeline scoreTimeline;

    // 游戏暂停状态属性
    private final BooleanProperty isPause = new SimpleBooleanProperty();

    // 游戏结束状态属性
    private final BooleanProperty isGameOver = new SimpleBooleanProperty();
    
    // 难度模式：true = 困难，false = 简单
    private boolean isHardMode = false;
    
    // 难度按钮
    @FXML
    private javafx.scene.control.Button easyButton;
    
    @FXML
    private javafx.scene.control.Button hardButton;
    
    // 对战模式按钮
    @FXML
    private javafx.scene.control.ToggleButton vsModeButton;
    
    // 玩家2的UI元素（对战模式）
    @FXML
    private GridPane gamePanel2; // 玩家2的游戏面板
    @FXML
    private GridPane brickPanel2; // 玩家2的方块面板
    @FXML
    private GridPane nextBrickPanel2; // 玩家2的下一个方块预览面板
    @FXML
    private Label scoreLabel2; // 玩家2的分数标签
    @FXML
    private GameOverPanel gameOverPanel2; // 玩家2的游戏结束面板
    @FXML
    private PausePanel pausePanel2; // 玩家2的暂停面板
    @FXML
    private Group groupNotification2; // 玩家2的通知组
    @FXML
    private javafx.scene.layout.BorderPane gameBoard2; // 玩家2的游戏板容器
    @FXML
    private javafx.scene.layout.VBox nextBrickVBox2; // 玩家2的下一个方块预览容器
    
    // 玩家2的显示矩阵
    private Rectangle[][] displayMatrix2;
    private Rectangle[][] rectangles2;
    private Rectangle[][] nextBrickRectangles2;
    
    
    // 游戏控制器引用
    private GameController gameController;
    
    // 对战模式状态
    private boolean isVsMode = false;

    // 裁剪区域，避免背景角超出圆角边框
    private final Rectangle gamePanelClip = new Rectangle();
    private final Rectangle gamePanel2Clip = new Rectangle();

    /**
     * FXML initialization method
     * Automatically called after FXML file is loaded, used to set initial interface state and event listeners
     * 
     * @param location FXML file location
     * @param resources Resource bundle (unused)
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 加载数字字体文件
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);
        
        // 设置游戏面板可以接收键盘焦点
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        
        // 设置键盘事件监听器
        gamePanel.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                // 只有在游戏未暂停且未结束时才响应键盘输入
                if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
                    
                    // 玩家1控制（方向键）
                    if (keyEvent.getCode() == KeyCode.LEFT) {
                        refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    
                    if (keyEvent.getCode() == KeyCode.RIGHT) {
                        refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    
                    if (keyEvent.getCode() == KeyCode.UP) {
                        refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
                        keyEvent.consume();
                    }
                    
                    if (keyEvent.getCode() == KeyCode.DOWN) {
                        moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
                        keyEvent.consume();
                    }
                    
                    // 玩家2控制（WASD键）- 仅在对战模式时生效
                    if (isVsMode && gameController != null) {
                        if (keyEvent.getCode() == KeyCode.A) {
                            ViewData viewData = gameController.onLeftEvent2(new MoveEvent(EventType.LEFT, EventSource.USER));
                            if (viewData != null) {
                                refreshBrick2(viewData);
                            }
                            keyEvent.consume();
                        }
                        
                        if (keyEvent.getCode() == KeyCode.D) {
                            ViewData viewData = gameController.onRightEvent2(new MoveEvent(EventType.RIGHT, EventSource.USER));
                            if (viewData != null) {
                                refreshBrick2(viewData);
                            }
                            keyEvent.consume();
                        }
                        
                        if (keyEvent.getCode() == KeyCode.W) {
                            ViewData viewData = gameController.onRotateEvent2(new MoveEvent(EventType.ROTATE, EventSource.USER));
                            if (viewData != null) {
                                refreshBrick2(viewData);
                            }
                            keyEvent.consume();
                        }
                        
                        if (keyEvent.getCode() == KeyCode.S) {
                            moveDown2(new MoveEvent(EventType.DOWN, EventSource.USER));
                            keyEvent.consume();
                        }
                    }
                }
                
                // 处理暂停/继续：空格键或P键
                if (keyEvent.getCode() == KeyCode.SPACE || keyEvent.getCode() == KeyCode.P) {
                    togglePause();
                    keyEvent.consume();
                }
                
                // 处理新游戏：N键
                if (keyEvent.getCode() == KeyCode.N) {
                    confirmNewGame();
                    keyEvent.consume();
                }
            }
        });
        
        // 初始时隐藏游戏结束面板和暂停面板
        gameOverPanel.setVisible(false);
        pausePanel.setVisible(false);

        // Reflection effect removed to use CSS DropShadow for neon glow
        // if (scoreLabel != null) {
        //     scoreLabel.setEffect(reflection);
        // }
        
        // 手动绑定按钮事件（确保按钮可点击）
        if (newGameButton != null) {
            newGameButton.setOnAction(e -> {
                System.out.println("New Game button clicked!"); // 调试输出
                newGame(e);
            });
            // 确保按钮可以接收鼠标事件
            newGameButton.setMouseTransparent(false);
            newGameButton.setDisable(false);
        }
        if (pauseButton != null) {
            pauseButton.setOnAction(e -> {
                System.out.println("Pause button clicked!"); // 调试输出
                pauseGame(e);
            });
            // 确保按钮可以接收鼠标事件
            pauseButton.setMouseTransparent(false);
            pauseButton.setDisable(false);
        }
        
        // 绑定难度选择按钮
        if (easyButton != null) {
            easyButton.setOnAction(e -> setDifficulty(false));
            easyButton.setMouseTransparent(false);
            easyButton.setDisable(false);
            // 初始状态：简单模式被选中
            updateDifficultyButtonStyle(false);
        }
        if (hardButton != null) {
            
            hardButton.setOnAction(e -> setDifficulty(true));
            hardButton.setMouseTransparent(false);
            hardButton.setDisable(false);
        }
        
        // 绑定对战模式按钮
        if (vsModeButton != null) {
            vsModeButton.setOnAction(e -> toggleVsMode());
            vsModeButton.setMouseTransparent(false);
            vsModeButton.setDisable(false);
        }
        
        // 初始时隐藏玩家2的UI元素
        if (gameBoard2 != null) {
            gameBoard2.setVisible(false);
        }
        if (brickPanel2 != null) {
            brickPanel2.setVisible(false);
        }
        if (nextBrickVBox2 != null) {
            nextBrickVBox2.setVisible(false);
        }
        if (scoreLabel2 != null) {
            scoreLabel2.setVisible(false);
        }
        if (groupNotification2 != null) {
            groupNotification2.setVisible(false);
        }
        if (gameOverPanel2 != null) {
            gameOverPanel2.setVisible(false);
        }
        if (pausePanel2 != null) {
            pausePanel2.setVisible(false);
        }
        
        // 初始化布局
        applySinglePlayerLayout();
        Platform.runLater(() -> {
            updateNotificationPosition();
            // configurePanelClip(gamePanel, gamePanelClip);
            // configurePanelClip(gamePanel2, gamePanel2Clip);
        });
    }
    
    /**
     * 设置游戏难度
     * @param hardMode true = 困难模式，false = 简单模式
     */
    private void setDifficulty(boolean hardMode) {
        isHardMode = hardMode;
        updateDifficultyButtonStyle(hardMode);
        
        // 如果游戏正在进行，更新时间线速度
        if (timeLine != null && isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
            createTimeline();
        }
    }
    
    /**
     * 更新难度按钮的样式，显示当前选中的难度
     * @param hardMode true = 困难模式被选中
     */
    private void updateDifficultyButtonStyle(boolean hardMode) {
        if (easyButton != null && hardButton != null) {
            if (hardMode) {
                // 困难模式被选中
                easyButton.getStyleClass().remove("difficultyButtonSelected");
                hardButton.getStyleClass().add("difficultyButtonSelected");
            } else {
                // 简单模式被选中
                easyButton.getStyleClass().add("difficultyButtonSelected");
                hardButton.getStyleClass().remove("difficultyButtonSelected");
            }
        }
    }

    /**
     * Initialize game view
     * Creates game background display matrix and current brick display matrix, starts automatic descent animation
     * 
     * @param boardMatrix Game board matrix data
     * @param brick Current brick view data
     */
    public void initGameView(int[][] boardMatrix, ViewData brick) {
        // 创建游戏背景显示矩阵
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        // 从第2行开始显示（前两行是隐藏区域）
        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT); // 初始为透明
                displayMatrix[i][j] = rectangle;
                // 添加到游戏面板，列索引为j，行索引为i-2（因为前两行不显示）
                gamePanel.add(rectangle, j, i - 2);
            }
        }

        // 创建当前方块显示矩阵
        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                setRectangleData(brick.getBrickData()[i][j], rectangle, true); // 高亮当前下落方块
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }
        
        // 设置方块面板的位置（根据方块在游戏板中的位置）
        // 使用gamePanel的boundsInParent来获取在父容器中的位置
        javafx.geometry.Bounds panelBounds = gamePanel.getBoundsInParent();
        double panelX = gameBoard.getLayoutX() + panelBounds.getMinX();
        double panelY = gameBoard.getLayoutY() + panelBounds.getMinY();
        brickPanel.setLayoutX(panelX + brick.getxPosition() * (brickPanel.getVgap() + BRICK_SIZE));
        // 游戏板从第2行开始显示（前两行是隐藏区域），所以需要减去2行
        double targetY = panelY + (brick.getyPosition() - 2) * (brickPanel.getHgap() + BRICK_SIZE) - 8;
        brickPanel.setLayoutY(targetY);

        bringBrickPanelsToFront();

        // 初始化下一个方块预览
        initNextBrickPreview(brick.getNextBrickData());

        // 创建自动下落的时间线动画（根据难度设置速度）
        createTimeline();
        createScoreTimeline();
    }
    
    /**
     * 创建或更新自动下落的时间线动画
     * 根据当前难度模式设置下降速度
     */
    private void createTimeline() {
        // 如果已有时间线，先停止
        if (timeLine != null) {
            timeLine.stop();
        }
        
        // 根据难度设置下降间隔：简单模式400ms，困难模式200ms
        long fallInterval = isHardMode ? 200 : 400;
        
        // 创建时间线，同时处理玩家1和玩家2（如果是对战模式）的自动下落
        timeLine = new Timeline(new KeyFrame(
                Duration.millis(fallInterval),
                ae -> {
                    // 玩家1自动下落
                    moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD));
                    // 如果是对战模式，玩家2也自动下落
                    if (isVsMode && gameController != null) {
                        moveDown2(new MoveEvent(EventType.DOWN, EventSource.THREAD));
                    }
                }
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE); // 无限循环
        
        // 如果游戏未暂停且未结束，开始播放
        if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
            timeLine.play();
        }
    }

    /**
     * 创建或更新自动加分的时间线动画
     * 每秒增加1分
     */
    private void createScoreTimeline() {
        // 如果已有时间线，先停止
        if (scoreTimeline != null) {
            scoreTimeline.stop();
        }
        
        // 创建时间线，每秒触发一次
        scoreTimeline = new Timeline(new KeyFrame(
                Duration.seconds(1),
                ae -> {
                    // 玩家1加分
                    if (gameController != null && gameController.getBoard() != null) {
                        gameController.getBoard().getScore().add(1);
                    }
                    // 如果是对战模式，玩家2也加分
                    if (isVsMode && gameController != null && gameController.getBoard2() != null) {
                        gameController.getBoard2().getScore().add(1);
                    }
                }
        ));
        scoreTimeline.setCycleCount(Timeline.INDEFINITE); // 无限循环
        
        // 如果游戏未暂停且未结束，开始播放
        if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
            scoreTimeline.play();
        }
    }

    /**
     * 根据方块类型编号获取对应的颜色
     * 不同的数字代表不同的方块类型和颜色
     * 
     * @param i 方块类型编号
     * @return 对应的颜色Paint对象
     */
    private Paint getFillColor(int i) {
        Paint returnPaint;
        switch (i) {
            case 0:
                returnPaint = Color.TRANSPARENT;
                break;
            case 1:
                returnPaint = Color.rgb(0, 240, 255); // Neon Cyan (I)
                break;
            case 2:
                returnPaint = Color.rgb(180, 0, 255); // Neon Purple (J)
                break;
            case 3:
                returnPaint = Color.rgb(0, 255, 0); // Neon Green (L)
                break;
            case 4:
                returnPaint = Color.rgb(255, 255, 0); // Neon Yellow (O)
                break;
            case 5:
                returnPaint = Color.rgb(255, 0, 80); // Neon Red (S)
                break;
            case 6:
                returnPaint = Color.rgb(255, 0, 255); // Neon Magenta (T)
                break;
            case 7:
                returnPaint = Color.rgb(255, 165, 0); // Neon Orange (Z)
                break;
            default:
                returnPaint = Color.WHITE;
                break;
        }
        return returnPaint;
    }


    /**
     * 刷新当前方块的显示
     * 更新方块的位置和颜色显示
     * 
     * @param brick 包含方块位置和形状数据的ViewData对象
     */
    private void refreshBrick(ViewData brick) {
        if (isPause.getValue() == Boolean.FALSE) {
            // 使用gamePanel的boundsInParent来获取在父容器中的位置
            javafx.geometry.Bounds panelBounds = gamePanel.getBoundsInParent();
            double panelX = gameBoard.getLayoutX() + panelBounds.getMinX();
            double panelY = gameBoard.getLayoutY() + panelBounds.getMinY();
            double newX = panelX + brick.getxPosition() * (brickPanel.getHgap() + BRICK_SIZE);
            brickPanel.setLayoutX(newX);
            
            // 计算目标Y位置（游戏板从第2行开始显示，前两行是隐藏区域）
            double targetY = panelY + (brick.getyPosition() - 2) * (brickPanel.getVgap() + BRICK_SIZE) - 8;
            brickPanel.setLayoutY(targetY);
            
        bringBrickPanelsToFront();

            // 更新方块每个部分的颜色
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    setRectangleData(brick.getBrickData()[i][j], rectangles[i][j], true);
                }
            }
            
            // 刷新下一个方块预览
            refreshNextBrickPreview(brick.getNextBrickData());
        }
    }

    /**
     * Refresh game background display
     * Updates fixed bricks display on the game board
     * 
     * @param board Game board matrix data
     */
    public void refreshGameBackground(int[][] board) {
        // 从第2行开始更新（前两行是隐藏区域）
        for (int i = 2; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    /**
     * 设置矩形的显示属性
     * 根据颜色编号设置矩形的填充颜色和圆角
     * 
     * @param color 颜色编号
     * @param rectangle 要设置的矩形对象
     */
    private void setRectangleData(int color, Rectangle rectangle) {
        setRectangleData(color, rectangle, false);
    }

    private void setRectangleData(int color, Rectangle rectangle, boolean highlight) {
        rectangle.setFill(getFillColor(color)); // 设置填充颜色
        rectangle.setArcHeight(10); // slightly rounder
        rectangle.setArcWidth(10);

        if (color != 0) {
            if (highlight) {
                // Falling block: No glow, but has border
                rectangle.setEffect(null);
                rectangle.setStroke(Color.WHITE);
                rectangle.setStrokeWidth(2);
                rectangle.setStrokeType(javafx.scene.shape.StrokeType.INSIDE);
            } else {
                // Merged block: No glow
                rectangle.setEffect(null);
                
                // Subtle inner stroke
                rectangle.setStroke(Color.rgb(255, 255, 255, 0.3));
                rectangle.setStrokeWidth(1);
                rectangle.setStrokeType(javafx.scene.shape.StrokeType.INSIDE);
            }
        } else {
            rectangle.setEffect(null);
            rectangle.setStroke(Color.TRANSPARENT);
            rectangle.setStrokeWidth(0);
        }
    }

    /**
     * 处理方块下落事件
     * 调用事件监听器处理下落逻辑，显示消除行的分数提示
     * 
     * @param event 下落事件对象
     */
    private void moveDown(MoveEvent event) {
        if (isPause.getValue() == Boolean.FALSE) {
            // 调用事件监听器处理下落逻辑
            DownData downData = eventListener.onDownEvent(event);
            
            // 如果有行被消除，显示分数提示
            if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0 && groupNotification != null) {
                updateNotificationPosition(); // Ensure position is correct
                NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
                groupNotification.getChildren().add(notificationPanel);
                notificationPanel.showScore(groupNotification.getChildren());
            }
            
            // 刷新方块显示
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus(); // 确保游戏面板保持焦点
    }

    /**
     * Set input event listener
     * 
     * @param eventListener Event listener instance
     */
    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
        // 如果eventListener是GameController，保存引用
        if (eventListener instanceof GameController) {
            this.gameController = (GameController) eventListener;
        }
    }
    
    /**
     * 切换对战模式
     */
    private void toggleVsMode() {
        if (gameController == null) {
            return;
        }
        
        isVsMode = !isVsMode;
        
        // 更新按钮状态
        if (vsModeButton != null) {
            vsModeButton.setSelected(isVsMode);
        }
        
        if (isVsMode) {
            // 调整布局：玩家1移到右侧，玩家2放在左侧
            applyVsLayout();
            // 开启对战模式：结束当前游戏，开始新的对战
            if (timeLine != null) {
                timeLine.stop();
            }
            if (scoreTimeline != null) {
                scoreTimeline.stop();
            }
            gameOverPanel.setVisible(false);
            if (groupNotification2 != null) {
                groupNotification2.setVisible(true);
            }
            if (gameOverPanel2 != null) {
                gameOverPanel2.setVisible(false);
            }
            if (pausePanel2 != null) {
                pausePanel2.setVisible(false);
            }
            isPause.setValue(Boolean.FALSE);
            isGameOver.setValue(Boolean.FALSE);
            
            // 通知GameController开启对战模式
            gameController.setVsMode(true);
            
            // 开始新的对战游戏
            startNewGameDirectly(null);
        } else {
            // 恢复单人模式布局
            applySinglePlayerLayout();
            // 关闭对战模式：结束当前游戏，回到单人模式
            if (timeLine != null) {
                timeLine.stop();
            }
            if (scoreTimeline != null) {
                scoreTimeline.stop();
            }
            gameOverPanel.setVisible(false);
            if (groupNotification2 != null) {
                groupNotification2.setVisible(false);
            }
            if (pausePanel2 != null) {
                pausePanel2.setVisible(false);
            }
            isPause.setValue(Boolean.FALSE);
            isGameOver.setValue(Boolean.FALSE);
            
            // 隐藏玩家2的UI
            hideVsModeView();
            
            // 通知GameController关闭对战模式
            gameController.setVsMode(false);
            
            // 开始新的单人游戏
            startNewGameDirectly(null);
        }
    }
    
    /**
     * Initialize versus mode view (Player 2)
     */
    public void initVsModeView(int[][] boardMatrix, ViewData brick) {
        if (gamePanel2 == null || brickPanel2 == null) {
            return;
        }
        
        // 显示玩家2的UI元素
        if (gameBoard2 != null) {
            gameBoard2.setVisible(true);
        }
        gamePanel2.setVisible(true);
        brickPanel2.setVisible(true);
        if (nextBrickVBox2 != null) {
            nextBrickVBox2.setVisible(true);
        }
        if (scoreLabel2 != null) {
            scoreLabel2.setVisible(true);
        }
        if (groupNotification2 != null) {
            groupNotification2.setVisible(true);
        }
        if (pausePanel2 != null) {
            pausePanel2.setVisible(false);
        }
        
        // 创建玩家2的游戏背景显示矩阵
        displayMatrix2 = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix2[i][j] = rectangle;
                gamePanel2.add(rectangle, j, i - 2);
            }
        }
        
        // 创建玩家2的当前方块显示矩阵
        rectangles2 = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                setRectangleData(brick.getBrickData()[i][j], rectangle, true);
                rectangles2[i][j] = rectangle;
                brickPanel2.add(rectangle, j, i);
            }
        }
        
        // 设置玩家2方块面板的位置
        // 使用gamePanel2的boundsInParent来获取在父容器中的位置
        javafx.geometry.Bounds panel2Bounds = gamePanel2.getBoundsInParent();
        double panel2X = gameBoard2.getLayoutX() + panel2Bounds.getMinX();
        double panel2Y = gameBoard2.getLayoutY() + panel2Bounds.getMinY();
        brickPanel2.setLayoutX(panel2X + brick.getxPosition() * (brickPanel2.getHgap() + BRICK_SIZE) - 2 + 1);
        // 游戏板从第2行开始显示（前两行是隐藏区域），所以需要减去2行
        double targetY2 = panel2Y + (brick.getyPosition() - 2) * (brickPanel2.getVgap() + BRICK_SIZE) - 8;
        brickPanel2.setLayoutY(targetY2);
        
        // 初始化玩家2的下一个方块预览
        if (nextBrickPanel2 != null) {
            initNextBrickPreview2(brick.getNextBrickData());
        }

        // 确保通知组在布局完成后定位到玩家2棋盘内部
        Platform.runLater(this::updateNotificationPosition);
    }
    
    /**
     * Hide versus mode view (Player 2)
     */
    public void hideVsModeView() {
        if (gameBoard2 != null) {
            gameBoard2.setVisible(false);
        }
        if (gamePanel2 != null) {
            gamePanel2.setVisible(false);
            gamePanel2.getChildren().clear();
        }
        if (brickPanel2 != null) {
            brickPanel2.setVisible(false);
            brickPanel2.getChildren().clear();
        }
        if (nextBrickVBox2 != null) {
            nextBrickVBox2.setVisible(false);
        }
        if (nextBrickPanel2 != null) {
            nextBrickPanel2.getChildren().clear();
        }
        if (scoreLabel2 != null) {
            scoreLabel2.setVisible(false);
        }
        if (groupNotification2 != null) {
            groupNotification2.setVisible(false);
        }
        if (gameOverPanel2 != null) {
            gameOverPanel2.setVisible(false);
        }
        if (pausePanel2 != null) {
            pausePanel2.setVisible(false);
        }
        
        displayMatrix2 = null;
        rectangles2 = null;
        nextBrickRectangles2 = null;
    }
    
    /**
     * 刷新玩家2的方块显示
     */
    private void refreshBrick2(ViewData brick) {
        if (isPause.getValue() == Boolean.FALSE && brickPanel2 != null) {
            // 使用gamePanel2的boundsInParent来获取在父容器中的位置
            javafx.geometry.Bounds panel2Bounds = gamePanel2.getBoundsInParent();
            double panel2X = gameBoard2.getLayoutX() + panel2Bounds.getMinX();
            double panel2Y = gameBoard2.getLayoutY() + panel2Bounds.getMinY();
            double newX2 = panel2X + brick.getxPosition() * (brickPanel2.getHgap() + BRICK_SIZE) - 2 + 1;
            brickPanel2.setLayoutX(newX2);
            
            // 计算目标Y位置（游戏板从第2行开始显示，前两行是隐藏区域）
            double targetY2 = panel2Y + (brick.getyPosition() - 2) * (brickPanel2.getVgap() + BRICK_SIZE) - 8;
            brickPanel2.setLayoutY(targetY2);

            bringBrickPanelsToFront();
            
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    setRectangleData(brick.getBrickData()[i][j], rectangles2[i][j], true);
                }
            }
            
            if (nextBrickPanel2 != null) {
                refreshNextBrickPreview2(brick.getNextBrickData());
            }
        }
    }
    
    /**
     * 处理玩家2的方块下落事件
     */
    private void moveDown2(MoveEvent event) {
        if (isPause.getValue() == Boolean.FALSE && gameController != null) {
            DownData downData = gameController.onDownEvent2(event);
            
            if (downData != null) {
                if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                    updateNotificationPosition(); // Ensure position is correct
                    NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
                    Group targetGroup = groupNotification2 != null ? groupNotification2 : groupNotification;
                    if (targetGroup != null) {
                        targetGroup.setVisible(true); // Ensure visible
                        targetGroup.getChildren().add(notificationPanel);
                        notificationPanel.showScore(targetGroup.getChildren());
                        targetGroup.toFront(); // Ensure on top
                    }
                }
                
                refreshBrick2(downData.getViewData());
            }
        }
        gamePanel.requestFocus();
    }
    
    /**
     * Refresh Player 2's game background display
     */
    public void refreshGameBackground2(int[][] board) {
        if (displayMatrix2 != null) {
            for (int i = 2; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    setRectangleData(board[i][j], displayMatrix2[i][j]);
                }
            }
        }
    }
    
    /**
     * Bind Player 2's score display
     */
    public void bindScore2(IntegerProperty integerProperty) {
        if (scoreLabel2 != null) {
            scoreLabel2.textProperty().bind(integerProperty.asString());
        }
    }
    
    /**
     * Handle Player 2 game over
     */
    public void gameOver2() {
        if (timeLine != null) {
            timeLine.stop();
        }
        if (scoreTimeline != null) {
            scoreTimeline.stop();
        }
        if (groupNotification2 != null) {
            groupNotification2.setVisible(true);
        }
        if (gameOverPanel2 != null) {
            gameOverPanel2.setVisible(true);
        }
    }
    
    /**
     * 初始化玩家2的下一个方块预览
     */
    private void initNextBrickPreview2(int[][] nextBrickData) {
        if (nextBrickPanel2 != null && nextBrickData != null) {
            nextBrickPanel2.getChildren().clear();
            
            if (nextBrickData.length > 0 && nextBrickData[0].length > 0) {
                nextBrickRectangles2 = new Rectangle[nextBrickData.length][nextBrickData[0].length];
                for (int i = 0; i < nextBrickData.length; i++) {
                    for (int j = 0; j < nextBrickData[i].length; j++) {
                        Rectangle rectangle = new Rectangle(BRICK_SIZE - 2, BRICK_SIZE - 2);
                        rectangle.setFill(getFillColor(nextBrickData[i][j]));
                        setRectangleData(nextBrickData[i][j], rectangle);
                        nextBrickRectangles2[i][j] = rectangle;
                        nextBrickPanel2.add(rectangle, j, i);
                    }
                }
            }
        }
    }
    
    /**
     * 刷新玩家2的下一个方块预览
     */
    private void refreshNextBrickPreview2(int[][] nextBrickData) {
        if (nextBrickPanel2 != null && nextBrickData != null && nextBrickRectangles2 != null) {
            for (int i = 0; i < nextBrickData.length && i < nextBrickRectangles2.length; i++) {
                for (int j = 0; j < nextBrickData[i].length && j < nextBrickRectangles2[i].length; j++) {
                    setRectangleData(nextBrickData[i][j], nextBrickRectangles2[i][j]);
                }
            }
        }
    }

    /**
     * Bind score display
     * Binds score property to Label for real-time updates
     * 
     * @param integerProperty Score property
     */
    public void bindScore(IntegerProperty integerProperty) {
        if (scoreLabel != null) {
            // 将分数属性绑定到Label的文本属性，使用StringConverter格式化
            scoreLabel.textProperty().bind(integerProperty.asString());
        }
    }

    /**
     * 初始化下一个方块预览
     * 创建下一个方块的显示矩阵
     * 
     * @param nextBrickData 下一个方块的数据矩阵
     */
    private void initNextBrickPreview(int[][] nextBrickData) {
        if (nextBrickPanel != null && nextBrickData != null) {
            // 清除之前的预览
            nextBrickPanel.getChildren().clear();
            
            // 创建下一个方块显示矩阵
            if (nextBrickData.length > 0 && nextBrickData[0].length > 0) {
                nextBrickRectangles = new Rectangle[nextBrickData.length][nextBrickData[0].length];
                for (int i = 0; i < nextBrickData.length; i++) {
                    for (int j = 0; j < nextBrickData[i].length; j++) {
                        Rectangle rectangle = new Rectangle(BRICK_SIZE - 2, BRICK_SIZE - 2);
                        rectangle.setFill(getFillColor(nextBrickData[i][j]));
                        setRectangleData(nextBrickData[i][j], rectangle);
                        nextBrickRectangles[i][j] = rectangle;
                        nextBrickPanel.add(rectangle, j, i);
                    }
                }
            }
        }
    }

    /**
     * 刷新下一个方块预览
     * 更新下一个方块的显示
     * 
     * @param nextBrickData 下一个方块的数据矩阵
     */
    private void refreshNextBrickPreview(int[][] nextBrickData) {
        if (nextBrickPanel != null && nextBrickData != null && nextBrickRectangles != null) {
            for (int i = 0; i < nextBrickData.length && i < nextBrickRectangles.length; i++) {
                for (int j = 0; j < nextBrickData[i].length && j < nextBrickRectangles[i].length; j++) {
                    setRectangleData(nextBrickData[i][j], nextBrickRectangles[i][j]);
                }
            }
        }
    }

    /**
     * Handle game over
     * Stops automatic descent animation, displays game over panel
     */
    public void gameOver() {
        timeLine.stop(); // 停止自动下落
        if (scoreTimeline != null) {
            scoreTimeline.stop(); // 停止自动加分
        }
        gameOverPanel.setVisible(true); // 显示游戏结束面板
        isGameOver.setValue(Boolean.TRUE); // 设置游戏结束状态
    }

    /**
     * 确认开始新游戏
     * 显示确认对话框，询问用户是否要开始新游戏
     * 在显示对话框时暂停游戏，如果用户取消则恢复游戏
     */
    private void confirmNewGame() {
        // 如果游戏未结束，在显示对话框前暂停游戏
        final boolean wasPaused = isPause.getValue();
        final boolean[] wasPlaying = {false};
        if (isGameOver.getValue() == Boolean.FALSE && !wasPaused) {
            timeLine.pause();
            if (scoreTimeline != null) {
                scoreTimeline.pause();
            }
            wasPlaying[0] = true;
        }
        
        startNewGameDirectly(null);
    }

    /**
     * Start new game (called when button is clicked, shows confirmation dialog)
     * Resets game state and restarts the game
     * 
     * @param actionEvent Action event
     */
    public void newGame(ActionEvent actionEvent) {
        confirmNewGame();
    }
    
    /**
     * 直接开始新游戏（不显示确认对话框）
     * 重置游戏状态，重新开始游戏
     * 
     * @param actionEvent 动作事件（未使用）
     */
    private void startNewGameDirectly(ActionEvent actionEvent) {
        if (timeLine != null) {
            timeLine.stop(); // 停止当前动画
        }
        if (scoreTimeline != null) {
            scoreTimeline.stop();
        }
        gameOverPanel.setVisible(false); // 隐藏游戏结束面板
        if (gameOverPanel2 != null) {
            gameOverPanel2.setVisible(false);
        }
        if (groupNotification2 != null) {
            groupNotification2.setVisible(false); // 隐藏玩家2的通知组
        }
        pausePanel.setVisible(false); // 隐藏暂停面板
        if (pausePanel2 != null) {
            pausePanel2.setVisible(false);
        }
        eventListener.createNewGame(); // 通知控制器创建新游戏
        
        // 获取新的游戏视图数据来初始化下一个方块预览
        // 通过触发一次空的下落事件来获取当前视图数据
        DownData downData = eventListener.onDownEvent(new MoveEvent(EventType.DOWN, EventSource.THREAD));
        if (downData != null && downData.getViewData() != null) {
            initNextBrickPreview(downData.getViewData().getNextBrickData());
        }
        
        // 如果是对战模式，也初始化玩家2的下一个方块预览
        if (isVsMode && gameController != null && gameController.getBoard2() != null) {
            DownData downData2 = gameController.onDownEvent2(new MoveEvent(EventType.DOWN, EventSource.THREAD));
            if (downData2 != null && downData2.getViewData() != null) {
                initNextBrickPreview2(downData2.getViewData().getNextBrickData());
            }
        }
        
        isPause.setValue(Boolean.FALSE); // 取消暂停状态
        isGameOver.setValue(Boolean.FALSE); // 取消游戏结束状态
        
        // 根据当前难度重新创建时间线
        createTimeline();
        createScoreTimeline();
        
        
        refreshCurrentBricksPosition(); // 确保方块位置与最新布局一致
        gamePanel.requestFocus(); // 设置焦点
    }

    /**
     * 应用对战模式的布局：玩家1在右侧，玩家2在左侧
     */
    private void applyVsLayout() {
        if (gameBoard != null) {
            gameBoard.setLayoutX(VS_PLAYER1_BOARD_X);
        }
        if (player1Options != null) {
            player1Options.setLayoutX(VS_PLAYER1_OPTIONS_X);
        }
        if (gameBoard2 != null) {
            gameBoard2.setLayoutX(VS_PLAYER2_BOARD_X);
        }
        if (nextBrickVBox2 != null) {
            nextBrickVBox2.setLayoutX(VS_PLAYER2_OPTIONS_X);
            nextBrickVBox2.setVisible(true);
        }
        if (groupNotification2 != null) {
            groupNotification2.setVisible(true);
        }
        updateNotificationPosition();
    }

    /**
     * 恢复单人模式布局：玩家1在左侧，玩家2保持默认位置
     */
    private void applySinglePlayerLayout() {
        if (gameBoard != null) {
            gameBoard.setLayoutX(SINGLE_PLAYER_BOARD_X);
        }
        if (gameBoard2 != null) {
            gameBoard2.setLayoutX(PLAYER2_BOARD_DEFAULT_X);
        }
        if (player1Options != null) {
            player1Options.setLayoutX(SINGLE_PLAYER_OPTIONS_X);
        }
        if (nextBrickVBox2 != null) {
            nextBrickVBox2.setLayoutX(PLAYER2_OPTIONS_DEFAULT_X);
            nextBrickVBox2.setVisible(false);
        }
        if (groupNotification2 != null) {
            groupNotification2.setVisible(false);
        }
        updateNotificationPosition();
    }

    /**
     * 根据当前布局刷新方块的显示位置
     */
    private void refreshCurrentBricksPosition() {
        if (gameController != null && gameController.getBoard() != null) {
            refreshBrick(gameController.getBoard().getViewData());
        }
        if (isVsMode && gameController != null && gameController.getBoard2() != null) {
            refreshBrick2(gameController.getBoard2().getViewData());
        }
    }

    /**
     * 根据玩家1棋盘位置更新得分提示/暂停提示的位置
     */
    private void updateNotificationPosition() {
        positionNotificationGroup(groupNotification, gamePanel);
        positionNotificationGroup(groupNotification2, gamePanel2);
    }

    private void positionNotificationGroup(Group notificationGroup, GridPane targetPanel) {
        if (notificationGroup == null || targetPanel == null || targetPanel.getScene() == null) {
            return;
        }
        Parent parent = notificationGroup.getParent();
        if (!(parent instanceof Pane)) {
            return;
        }
        Bounds panelBounds = targetPanel.localToScene(targetPanel.getBoundsInLocal());
        Point2D topLeft = ((Pane) parent).sceneToLocal(panelBounds.getMinX(), panelBounds.getMinY());
        notificationGroup.setLayoutX(topLeft.getX() + NOTIFICATION_OFFSET_X);
        notificationGroup.setLayoutY(topLeft.getY() + NOTIFICATION_OFFSET_Y);
        notificationGroup.toFront();
    }

    private void bringBrickPanelsToFront() {
        if (brickPanel != null) {
            brickPanel.toFront();
        }
        if (brickPanel2 != null) {
            brickPanel2.toFront();
        }
        if (groupNotification != null) {
            groupNotification.toFront();
        }
        if (groupNotification2 != null) {
            groupNotification2.toFront();
        }
    }

    private void configurePanelClip(GridPane panel, Rectangle clip) {
        if (panel == null) {
            return;
        }
        clip.setArcWidth(36);
        clip.setArcHeight(36);
        Bounds bounds = panel.getLayoutBounds();
        clip.setWidth(bounds.getWidth());
        clip.setHeight(bounds.getHeight());
        panel.setClip(clip);

        panel.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
            clip.setWidth(newBounds.getWidth());
            clip.setHeight(newBounds.getHeight());
        });
    }

    /**
     * Pause game
     * Toggles game pause/resume state
     * 
     * @param actionEvent Action event
     */
    public void pauseGame(ActionEvent actionEvent) {
        togglePause();
        gamePanel.requestFocus(); // 保持焦点
    }

    /**
     * 切换暂停/继续状态
     * 暂停时停止自动下落动画，继续时恢复动画
     */
    private void togglePause() {
        if (isGameOver.getValue() == Boolean.FALSE) {
            if (isPause.getValue() == Boolean.FALSE) {
                // 暂停游戏
                timeLine.pause();
                if (scoreTimeline != null) {
                    scoreTimeline.pause();
                }
                isPause.setValue(Boolean.TRUE);
                if (pausePanel != null) {
                    pausePanel.setVisible(true); // 显示暂停提示
                }
                // if (isVsMode && pausePanel2 != null) {
                //     pausePanel2.setVisible(true);
                // }
            } else {
                // 继续游戏
                timeLine.play();
                if (scoreTimeline != null) {
                    scoreTimeline.play();
                }
                isPause.setValue(Boolean.FALSE);
                if (pausePanel != null) {
                    pausePanel.setVisible(false); // 隐藏暂停提示
                }
                if (pausePanel2 != null) {
                    pausePanel2.setVisible(false);
                }
            }
            gamePanel.requestFocus();
        }
    }
}
