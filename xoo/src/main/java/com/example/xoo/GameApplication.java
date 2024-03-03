package com.example.xoo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GameApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GameApplication.class.getResource("mainWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Game");
        stage.setScene(scene);
        stage.setOnCloseRequest(e->stop());
        stage.show();
    }

    @Override
    public void stop(){
        Platform.exit();
    }
    public static void main(String[] args) {
        launch();
    }

}