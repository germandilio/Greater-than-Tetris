package ru.hse.germandilio.tetris.client.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Window;
import javafx.util.Duration;
import ru.hse.germandilio.tetris.client.model.UserStats;

import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import static ru.hse.germandilio.tetris.client.model.gameboard.GameBoard.BRICK_SIZE;
import static ru.hse.germandilio.tetris.client.model.gameboard.GameBoard.GAME_FIELD_SIZE;

public class TetrisViewController implements IReset {
    @FXML
    private Text userName;

    @FXML
    private Text partnerName;

    @FXML
    private Text timeout;

    @FXML
    private GridPane gridPanel;

    @FXML
    private GridPane brickToDrag;

    @FXML
    private Text currentSessionStopwatch;

    @FXML
    private Text status;

    private final Color dragBrickColor = Color.web("6195E2");
    private final Color initialColor = Color.TRANSPARENT;
    private final Color brickColor = Color.web("948E8E");

    private UserStats stats;
    private Timeline stopwatch;

    private BrickToDragController preview;
    private boolean[][] brickToPlace;
    private final String draggingBrick = "$$$dragging brick$$$";

    private GameManager gameManager;

    public void initGameView(GameManager gameManager, UserStats stats) {
        this.stats = stats;
        this.gameManager = gameManager;

        gameManager.getGameBoard().initDisplayBoard();

        initGameBoard();
        initBrickToDragBoard();
    }

    @FXML
    public void handleDragOnNextBrick(MouseEvent mouseEvent) {
        Dragboard db = brickToDrag.startDragAndDrop(TransferMode.ANY);

        ClipboardContent context = new ClipboardContent();
        context.putString(draggingBrick);

        db.setContent(context);
        mouseEvent.consume();
    }

    @FXML
    public void handleDragOver(DragEvent dragEvent) {
        if (dragEvent.getDragboard().hasString()) {
            dragEvent.acceptTransferModes(TransferMode.ANY);
        }
    }

    @FXML
    public void handleBrickDropped(DragEvent dragEvent) {
        try {
            if (!dragEvent.getDragboard().hasString()) return;
            String string = dragEvent.getDragboard().getString();
            if (!string.equals(draggingBrick)) return;

            boolean successfully = gameManager.getGameBoard().placeBrickOnBoard(brickToPlace,
                    ((int) dragEvent.getSceneX()) / BRICK_SIZE,
                    ((int) dragEvent.getSceneY()) / BRICK_SIZE);

            stats.actionHappened();
            if (successfully) {
                redrawBoard();

                gameManager.waitForNextBrick();
            }
        } catch (Exception ex) {
        }
    }

    @FXML
    public void onExitButtonClick() {
        try {
            //stop game time
            stopwatch.stop();

            var exitWindow = setUpExitWindow();
            ButtonType newGameButton = new ButtonType("Начать новую игру");
            ButtonType exitConfirmedButton = new ButtonType("Выйти");

            exitWindow.getButtonTypes().clear();
            exitWindow.getButtonTypes().addAll(newGameButton, exitConfirmedButton);
            Window window = exitWindow.getDialogPane().getScene().getWindow();
            window.setOnCloseRequest(e -> {
                stopwatch.play();
                exitWindow.close();
            });

            var response = exitWindow.showAndWait();
            if (response.isPresent()) {
                handleButtonsResponse(response, newGameButton, exitConfirmedButton);
            }
        } catch (Exception ex) {
        }
    }

    public void displayBrickToDrag(boolean[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                preview.getBoardNode(i, j).setFill(setColor(matrix[i][j]));
            }
        }
    }

    public void reset() {
        //TODO replace
        //reset drag brick view
        //setUpNewBrick();

        currentSessionStopwatch.setText("00:00:00");
        setUpStopWatch();
    }

    public void setUpStopWatch() {
        stats.reset();
        //sets stopwatch
        stopwatch = new Timeline(new KeyFrame(Duration.seconds(1), actionEvent -> {
            stats.updateStopWatch();

            long seconds = stats.getSecondsSinceGameStart();
            currentSessionStopwatch.setText(convertTime(seconds));
        }));

        stopwatch.setCycleCount(Timeline.INDEFINITE);
        stopwatch.play();
    }

    public void setUpNewBrick(boolean[][] brick) {
        brickToPlace = brick;

        displayBrickToDrag(brickToPlace);
    }

    private void initGameBoard() {
        // create game fields
        for (int i = 0; i < GAME_FIELD_SIZE; i++) {
            for (int j = 0; j < GAME_FIELD_SIZE; j++) {
                gridPanel.add(gameManager.getGameBoard().getDisplayBoardNode(i, j), j, i);
            }
        }
    }

    private void initBrickToDragBoard() {
        preview = new BrickToDragController();
        preview.initPreviewBoard();

        for (int i = 0; i < preview.getBoardSize(); i++) {
            for (int j = 0; j < preview.getBoardSize(); j++) {
                brickToDrag.add(preview.getBoardNode(i, j), j, i);
            }
        }
    }

    private void redrawBoard() {
        for (int i = 0; i < GAME_FIELD_SIZE; i++) {
            for (int j = 0; j < GAME_FIELD_SIZE; j++) {
                redrawRectangular(gameManager.getGameBoard().getGameBoardNode(i, j),
                        gameManager.getGameBoard().getDisplayBoardNode(i, j));
            }
        }
    }

    private Color setColor(boolean isPresent) {
        if (isPresent) return dragBrickColor;
        return initialColor;
    }

    private void redrawRectangular(boolean isPresent, Rectangle rectangle) {
        if (isPresent) {
            rectangle.setFill(brickColor);
        }
    }

    private Alert setUpExitWindow() {
        Alert exitAlertWindow = new Alert(Alert.AlertType.INFORMATION);

        exitAlertWindow.setTitle("Выберите действие");
        exitAlertWindow.setHeaderText("Ходов: " + stats.getActionsCounter() + ". Время в игре: "
                + convertTime(stats.getSecondsSinceGameStart()));
        exitAlertWindow.setContentText("Вы действительно хотите выйти?");

        return exitAlertWindow;
    }

    private void handleButtonsResponse(Optional<ButtonType> response, ButtonType newGameButton,
                                       ButtonType exitConfirmedButton) {
        if (response.isEmpty()) {
            throw new IllegalStateException("Unknown button response");
        } else if (response.get() == newGameButton) {
            gameManager.startNewGame();
        } else if (response.get() == exitConfirmedButton) {
            Platform.exit();
        } else {
            throw new IllegalStateException("Unknown button response");
        }
    }

    public String getUserNameFromDialog() {
        if (Platform.isFxApplicationThread()) {
            boolean correct = true;
            TextInputDialog inputDialog = new TextInputDialog();
            String name;

            while (true) {

                inputDialog.setTitle("Имя игрока");
                inputDialog.setHeaderText("Введите ваше имя");
                if (!correct) {
                    inputDialog.setHeaderText("Введите ваше имя. (имя не может быть пустым)");
                }

                Optional<String> result = inputDialog.showAndWait();

                if (result.isPresent() && !result.get().isBlank()) {
                    name = result.get().replaceAll("\\n|\\s", "%20");
                    break;
                } else {
                    correct = false;
                }
            }
            inputDialog.close();
            return name;
        }
        return null;
    }

    public void setStatus(String action) {
        status.setText(action);
    }

    public void resetStatus() {
        status.setText("");
    }

    public void setUpTimeout(long time) {
        //sets timer
        Timer timer = new Timer();

        TimerTask closeSession = new TimerTask() {
            @Override
            public void run() {
                gameManager.sendEndGame();
            }
        };
        timer.schedule(closeSession, time * 1000);
    }

    public void exitWithResults() {
        exitWithResults(0, 0, 0, 0, null);
    }

    public void exitWithResults(int opponentBricks, long opponentTime, int myBricks, long myTime, String winnerName) {
        var exitWindow = setupEndGameWindow(opponentBricks,opponentTime, myBricks, myTime, winnerName);
        ButtonType newGameButton = new ButtonType("Игра окончена");
        ButtonType exitConfirmedButton = new ButtonType("Выйти");

        exitWindow.getButtonTypes().clear();
        exitWindow.getButtonTypes().addAll(newGameButton, exitConfirmedButton);
        Window window = exitWindow.getDialogPane().getScene().getWindow();
        window.setOnCloseRequest(e -> {
            stopwatch.play();
            exitWindow.close();
        });

        var response = exitWindow.showAndWait();
        if (response.isPresent()) {
            handleButtonsResponse(response, newGameButton, exitConfirmedButton);
        }
    }

    private Alert setupEndGameWindow(int opponentBricks, long opponentTime, int myBricks, long myTime,
                                     String winnerName) {
        Alert exitAlertWindow = new Alert(Alert.AlertType.INFORMATION);

        exitAlertWindow.setTitle("Результаты игры");
        if (opponentBricks == 0 && opponentTime == 0) {
            exitAlertWindow.setHeaderText("Вы:\nХодов: " + myBricks + ". Время в игре: "
                    + convertTime(myTime));
        } else {
            exitAlertWindow.setHeaderText("Вы:\nХодов: " + myBricks + ". Время в игре: "
                    + convertTime(myTime) + "\nПартнер:\nХодов: " + opponentBricks + ". Время в игре: " +
                    convertTime(opponentTime) + ".\nПобедитель: " + winnerName);
        }

        exitAlertWindow.setContentText("Начать новую игру?");
        return exitAlertWindow;
    }

    private String convertTime(long seconds) {
        return String.format("%02d:%02d:%02d", seconds / 360,
                seconds / 60, seconds % 60);
    }

    public void setUpPartnerName(String partnerName) {
        this.partnerName.setText(partnerName);
    }

    public void setUpUserName(String userName) {
        this.userName.setText(userName);
    }

    public void setUpMaxTime(long seconds) {
        this.timeout.setText(convertTime(seconds));
    }
}