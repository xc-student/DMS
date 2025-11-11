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

    // FXML注入的分数标签
    @FXML
    private Label scoreLabel;

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
                    
                    // 处理左移：左方向键或A键
                    if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
                        refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
                        keyEvent.consume(); // 消费事件，防止重复处理
                    }
                    
                    // 处理右移：右方向键或D键
                    if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
                        refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    
                    // 处理旋转：上方向键或W键
                    if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
                        refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
                        keyEvent.consume();
                    }
                    
                    // 处理快速下落：下方向键或S键
                    if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.S) {
                        moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
                        keyEvent.consume();
                    }
                }
                
                // 处理暂停/继续：空格键或P键
                if (keyEvent.getCode() == KeyCode.SPACE || keyEvent.getCode() == KeyCode.P) {
                    togglePause();
                    keyEvent.consume();
                }
                
                // 处理新游戏：N键
                if (keyEvent.getCode() == KeyCode.N) {
                    newGame(null);
                }
            }
        });
        
        // 初始时隐藏游戏结束面板
        gameOverPanel.setVisible(false);

        // 设置反射效果（用于视觉增强）
        final Reflection reflection = new Reflection();
        reflection.setFraction(0.8);
        reflection.setTopOpacity(0.9);
        reflection.setTopOffset(-12);
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
        brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
        brickPanel.setLayoutY(-42 + gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);

        // 初始化下一个方块预览
        initNextBrickPreview(brick.getNextBrickData());

        // 创建自动下落的时间线动画
        timeLine = new Timeline(new KeyFrame(
                Duration.millis(400), // 每400毫秒执行一次
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD)) // 自动下落事件
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE); // 无限循环
        timeLine.play(); // 开始播放动画
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
            // 更新方块面板的位置
            brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
            brickPanel.setLayoutY(-42 + gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);
            
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
        rectangle.setArcHeight(9); // 设置圆角高度
        rectangle.setArcWidth(9);  // 设置圆角宽度
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
     * 开始新游戏
     * 重置游戏状态，重新开始游戏
     * 
     * @param actionEvent 动作事件（未使用）
     */
    public void newGame(ActionEvent actionEvent) {
        timeLine.stop(); // 停止当前动画
        gameOverPanel.setVisible(false); // 隐藏游戏结束面板
        eventListener.createNewGame(); // 通知控制器创建新游戏
        
        // 获取新的游戏视图数据来初始化下一个方块预览
        // 通过触发一次空的下落事件来获取当前视图数据
        DownData downData = eventListener.onDownEvent(new MoveEvent(EventType.DOWN, EventSource.THREAD));
        if (downData != null && downData.getViewData() != null) {
            initNextBrickPreview(downData.getViewData().getNextBrickData());
        }
        
        gamePanel.requestFocus(); // 设置焦点
        timeLine.play(); // 重新开始自动下落
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
            } else {
                // 继续游戏
                timeLine.play();
                isPause.setValue(Boolean.FALSE);
            }
            gamePanel.requestFocus();
        }
    }
}
