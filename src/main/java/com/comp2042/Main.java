package com.comp2042;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Main entry point for the Tetris game application.
 * Extends JavaFX Application class and is responsible for launching the game interface.
 * 
 * @author XiangCan
 * @version 1.0
 */
public class Main extends Application {

    /**
     * Starts the JavaFX application.
     * Loads the FXML layout file, creates the scene, displays the window, and initializes the game controller.
     * 
     * @param primaryStage the primary stage window for this application
     * @throws Exception if an error occurs during FXML loading or initialization
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        // 获取FXML布局文件的URL路径
        URL location = getClass().getClassLoader().getResource("gameLayout.fxml");
        ResourceBundle resources = null; // 不使用资源包，设为null
        
        // 创建FXML加载器，用于加载界面布局文件
        FXMLLoader fxmlLoader = new FXMLLoader(location, resources);
        
        // 加载FXML文件并获取根节点
        Parent root = fxmlLoader.load();
        
        // 获取FXML文件中定义的控制器实例（GuiController）
        GuiController c = fxmlLoader.getController();

        // 设置窗口标题
        primaryStage.setTitle("TetrisJFX");
        
        // 创建场景，设置窗口大小（对战模式需要更大的窗口）
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
        
        // 将场景设置到主舞台
        primaryStage.setScene(scene);
        
        // 设置全屏显示
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
        primaryStage.show();
        
        // 创建游戏控制器，传入GUI控制器实例
        // 这里建立了视图层（GuiController）和控制层（GameController）的连接
        new GameController(c);
    }

    /**
     * Main entry point of the program.
     * Launches the JavaFX application.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args); // Launch the JavaFX application
    }
}
