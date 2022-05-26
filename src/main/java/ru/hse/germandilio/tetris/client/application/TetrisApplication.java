package ru.hse.germandilio.tetris.client.application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.hse.germandilio.tetris.client.controllers.GameManager;
import ru.hse.germandilio.tetris.client.controllers.TetrisViewController;

import java.io.IOException;

public class TetrisApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TetrisApplication.class.getResource("tetris-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 740, 452);
        stage.setTitle("Tetris");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        try {
            TetrisViewController viewController = fxmlLoader.getController();

            var manager = new GameManager(viewController);

            stage.setOnCloseRequest((windowEvent) -> {
                manager.exit();
                Platform.exit();
            });
            manager.startGame();

        } catch (IOException ex) {
            System.out.println("Cannot connect to the server. Run server first!");
        } catch(Exception ex) {
            System.out.println("Poo-pi-poop. Something went wrong.");
        }
    }

    public static void main(String[] args) {
        launch();
    }
}