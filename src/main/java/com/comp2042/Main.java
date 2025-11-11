package com.comp2042;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Tetris游戏的主入口类
 * 继承自JavaFX的Application类，负责启动游戏界面
 * 
 * @author 游戏开发者
 * @version 1.0
 */
public class Main extends Application {

    /**
     * JavaFX应用程序的启动方法
     * 负责加载FXML界面文件，创建场景，显示窗口，并初始化游戏控制器
     * 
     * @param primaryStage 主舞台窗口
     * @throws Exception 如果加载FXML文件或初始化过程中出现异常
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
        
        // 创建场景，设置窗口大小为300x510像素
        Scene scene = new Scene(root, 400, 610);
        
        // 将场景设置到主舞台
        primaryStage.setScene(scene);
        
        // 显示游戏窗口
        primaryStage.show();
        
        // 创建游戏控制器，传入GUI控制器实例
        // 这里建立了视图层（GuiController）和控制层（GameController）的连接
        new GameController(c);
    }

    /**
     * 程序主入口方法
     * 启动JavaFX应用程序
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        launch(args); // 启动JavaFX应用程序
    }
}
