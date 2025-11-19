package com.comp2042;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.effect.Reflection;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * GUI控制器类
 * 负责处理用户界面显示和用户输入事件
 * 实现Initializable接口，在FXML加载完成后自动初始化
 * 
 * 主要功能：
 * 1. 处理键盘输入事件（方向键、旋转等）
 * 2. 管理游戏画面渲染（方块、背景）
 * 3. 控制游戏动画（自动下落）
 * 4. 管理游戏状态（暂停、结束、重新开始）
 * 
 * @author 游戏开发者
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
    private Group gameOverGroup2; // 玩家2的游戏结束面板组
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

    /**
     * FXML初始化方法
     * 在FXML文件加载完成后自动调用，用于设置界面初始状态和事件监听
     * 
     * @param location FXML文件位置
     * @param resources 资源包（未使用）
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

        // 设置反射效果（用于视觉增强）
        final Reflection reflection = new Reflection();
        reflection.setFraction(0.8);
        reflection.setTopOpacity(0.9);
        reflection.setTopOffset(-12);
        
        // 将反射效果应用到分数标签
        if (scoreLabel != null) {
            scoreLabel.setEffect(reflection);
        }
        
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
        if (gameOverGroup2 != null) {
            gameOverGroup2.setVisible(false);
        }
        if (gameOverPanel2 != null) {
            gameOverPanel2.setVisible(false);
        }
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
     * 初始化游戏视图
     * 创建游戏背景显示矩阵和当前方块显示矩阵，启动自动下落动画
     * 
     * @param boardMatrix 游戏板矩阵数据
     * @param brick 当前方块的视图数据
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
                rectangle.setFill(getFillColor(brick.getBrickData()[i][j])); // 根据方块类型设置颜色
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

        // 初始化下一个方块预览
        initNextBrickPreview(brick.getNextBrickData());

        // 创建自动下落的时间线动画（根据难度设置速度）
        createTimeline();
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
                returnPaint = Color.TRANSPARENT; // 透明（空白）
                break;
            case 1:
                returnPaint = Color.AQUA; // 青色（I型方块）
                break;
            case 2:
                returnPaint = Color.BLUEVIOLET; // 蓝紫色（J型方块）
                break;
            case 3:
                returnPaint = Color.DARKGREEN; // 深绿色（L型方块）
                break;
            case 4:
                returnPaint = Color.YELLOW; // 黄色（O型方块）
                break;
            case 5:
                returnPaint = Color.RED; // 红色（S型方块）
                break;
            case 6:
                returnPaint = Color.BEIGE; // 米色（T型方块）
                break;
            case 7:
                returnPaint = Color.BURLYWOOD; // 浅棕色（Z型方块）
                break;
            default:
                returnPaint = Color.WHITE; // 默认白色
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
            double newX = panelX + brick.getxPosition() * (brickPanel.getVgap() + BRICK_SIZE) - 2;
            brickPanel.setLayoutX(newX);
            
            // 计算目标Y位置（游戏板从第2行开始显示，前两行是隐藏区域）
            double targetY = panelY + (brick.getyPosition() - 2) * (brickPanel.getHgap() + BRICK_SIZE) - 8;
            brickPanel.setLayoutY(targetY);
            
            // 更新方块每个部分的颜色
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    setRectangleData(brick.getBrickData()[i][j], rectangles[i][j]);
                }
            }
            
            // 刷新下一个方块预览
            refreshNextBrickPreview(brick.getNextBrickData());
        }
    }

    /**
     * 刷新游戏背景显示
     * 更新游戏板上已固定的方块显示
     * 
     * @param board 游戏板矩阵数据
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
        rectangle.setFill(getFillColor(color)); // 设置填充颜色
        rectangle.setArcHeight(8); // 设置圆角高度
        rectangle.setArcWidth(8);  // 设置圆角宽度
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
            if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
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
     * 设置输入事件监听器
     * 
     * @param eventListener 事件监听器实例
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
            // 开启对战模式：结束当前游戏，开始新的对战
            if (timeLine != null) {
                timeLine.stop();
            }
            gameOverPanel.setVisible(false);
            if (gameOverGroup2 != null) {
                gameOverGroup2.setVisible(false);
            }
            isPause.setValue(Boolean.FALSE);
            isGameOver.setValue(Boolean.FALSE);
            
            // 通知GameController开启对战模式
            gameController.setVsMode(true);
            
            // 开始新的对战游戏
            startNewGameDirectly(null);
        } else {
            // 关闭对战模式：结束当前游戏，回到单人模式
            if (timeLine != null) {
                timeLine.stop();
            }
            gameOverPanel.setVisible(false);
            if (gameOverGroup2 != null) {
                gameOverGroup2.setVisible(false);
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
     * 初始化对战模式视图（玩家2）
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
                rectangle.setFill(getFillColor(brick.getBrickData()[i][j]));
                rectangles2[i][j] = rectangle;
                brickPanel2.add(rectangle, j, i);
            }
        }
        
        // 设置玩家2方块面板的位置
        // 使用gamePanel2的boundsInParent来获取在父容器中的位置
        javafx.geometry.Bounds panel2Bounds = gamePanel2.getBoundsInParent();
        double panel2X = gameBoard2.getLayoutX() + panel2Bounds.getMinX();
        double panel2Y = gameBoard2.getLayoutY() + panel2Bounds.getMinY();
        brickPanel2.setLayoutX(panel2X + brick.getxPosition() * (brickPanel2.getVgap() + BRICK_SIZE) - 2);
        // 游戏板从第2行开始显示（前两行是隐藏区域），所以需要减去2行
        double targetY2 = panel2Y + (brick.getyPosition() - 2) * (brickPanel2.getHgap() + BRICK_SIZE) - 8;
        brickPanel2.setLayoutY(targetY2);
        
        // 初始化玩家2的下一个方块预览
        if (nextBrickPanel2 != null) {
            initNextBrickPreview2(brick.getNextBrickData());
        }
    }
    
    /**
     * 隐藏对战模式视图（玩家2）
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
        if (gameOverGroup2 != null) {
            gameOverGroup2.setVisible(false);
        }
        if (gameOverPanel2 != null) {
            gameOverPanel2.setVisible(false);
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
            double newX2 = panel2X + brick.getxPosition() * (brickPanel2.getVgap() + BRICK_SIZE) - 2;
            brickPanel2.setLayoutX(newX2);
            
            // 计算目标Y位置（游戏板从第2行开始显示，前两行是隐藏区域）
            double targetY2 = panel2Y + (brick.getyPosition() - 2) * (brickPanel2.getHgap() + BRICK_SIZE) - 8;
            brickPanel2.setLayoutY(targetY2);
            
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    setRectangleData(brick.getBrickData()[i][j], rectangles2[i][j]);
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
                    NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
                    groupNotification.getChildren().add(notificationPanel);
                    notificationPanel.showScore(groupNotification.getChildren());
                }
                
                refreshBrick2(downData.getViewData());
            }
        }
        gamePanel.requestFocus();
    }
    
    /**
     * 刷新玩家2的游戏背景显示
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
     * 绑定玩家2的分数显示
     */
    public void bindScore2(IntegerProperty integerProperty) {
        if (scoreLabel2 != null) {
            scoreLabel2.textProperty().bind(integerProperty.asString());
        }
    }
    
    /**
     * 玩家2游戏结束处理
     */
    public void gameOver2() {
        if (timeLine != null) {
            timeLine.stop();
        }
        if (gameOverGroup2 != null) {
            gameOverGroup2.setVisible(true);
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
     * 绑定分数显示
     * 将分数属性绑定到Label，实现实时更新
     * 
     * @param integerProperty 分数属性
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
     * 游戏结束处理
     * 停止自动下落动画，显示游戏结束面板
     */
    public void gameOver() {
        timeLine.stop(); // 停止自动下落
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
            wasPlaying[0] = true;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("New Game");
        alert.setHeaderText("Start New Game?");
        alert.setContentText("Are you sure you want to start a new game? Current progress will be lost.");
        
        // 设置按钮文本为英文
        ButtonType okButton = new ButtonType("OK");
        ButtonType cancelButton = new ButtonType("Cancel");
        alert.getButtonTypes().setAll(okButton, cancelButton);
        
        // 显示对话框并等待用户响应
        alert.showAndWait().ifPresent(response -> {
            if (response == okButton) {
                // 用户确认，开始新游戏
                startNewGameDirectly(null);
            } else {
                // 用户取消，恢复游戏状态
                if (wasPlaying[0] && isGameOver.getValue() == Boolean.FALSE) {
                    timeLine.play();
                }
            }
        });
    }

    /**
     * 开始新游戏（按钮点击时调用，显示确认对话框）
     * 重置游戏状态，重新开始游戏
     * 
     * @param actionEvent 动作事件
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
        gameOverPanel.setVisible(false); // 隐藏游戏结束面板
        if (gameOverGroup2 != null) {
            gameOverGroup2.setVisible(false); // 隐藏玩家2的游戏结束面板
        }
        pausePanel.setVisible(false); // 隐藏暂停面板
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
        
        gamePanel.requestFocus(); // 设置焦点
        // 根据当前难度重新创建时间线
        createTimeline();
        isPause.setValue(Boolean.FALSE); // 取消暂停状态
        isGameOver.setValue(Boolean.FALSE); // 取消游戏结束状态
    }

    /**
     * 暂停游戏
     * 切换游戏的暂停/继续状态
     * 
     * @param actionEvent 动作事件
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
                isPause.setValue(Boolean.TRUE);
                pausePanel.setVisible(true); // 显示暂停提示
            } else {
                // 继续游戏
                timeLine.play();
                isPause.setValue(Boolean.FALSE);
                pausePanel.setVisible(false); // 隐藏暂停提示
            }
            gamePanel.requestFocus();
        }
    }
}
