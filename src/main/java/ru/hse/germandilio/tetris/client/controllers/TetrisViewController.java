package ru.hse.germandilio.tetris.client.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Window;
import javafx.util.Duration;
import ru.hse.germandilio.tetris.client.model.GameSessionStats;
import ru.hse.germandilio.tetris.commands.CommandsAPI;

import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import static ru.hse.germandilio.tetris.client.model.gameboard.GameBoard.BRICK_SIZE;
import static ru.hse.germandilio.tetris.client.model.gameboard.GameBoard.GAME_FIELD_SIZE;

public class TetrisViewController {
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

    @FXML
    private Button startStopButton;

    private final Color dragBrickColor = Color.web("6195E2");
    private final Color initialColor = Color.TRANSPARENT;
    private final Color brickColor = Color.web("948E8E");

    private GameSessionStats stats;
    private Timeline stopwatch;

    private BrickToDragController preview;
    private boolean[][] brickToPlace;
    private final String draggingBrick = "$$$dragging brick$$$";

    private GameManager gameManager;

    private boolean blockedUI = false;

    public void blockUI() {
        blockedUI = true;
    }

    public void unblockUI() {
        blockedUI = false;
    }

    public void initGameView(GameManager gameManager, GameSessionStats stats) {
        this.stats = stats;
        this.gameManager = gameManager;

        gameManager.getGameBoard().initDisplayBoard();

        initGameBoard();
        initBrickToDragBoard();
    }

    @FXML
    public void handleDragOnNextBrick(MouseEvent mouseEvent) {
        if (blockedUI) return;

        Dragboard db = brickToDrag.startDragAndDrop(TransferMode.ANY);

        ClipboardContent context = new ClipboardContent();
        context.putString(draggingBrick);

        db.setContent(context);
        mouseEvent.consume();
    }

    @FXML
    public void handleDragOver(DragEvent dragEvent) {
        if (blockedUI) return;

        if (dragEvent.getDragboard().hasString()) {
            dragEvent.acceptTransferModes(TransferMode.ANY);
        }
    }

    @FXML
    public void handleBrickDropped(DragEvent dragEvent) {
        if (blockedUI) return;

        try {
            if (!dragEvent.getDragboard().hasString()) return;
            String string = dragEvent.getDragboard().getString();
            if (!string.equals(draggingBrick)) return;

            boolean successfully = gameManager.getGameBoard().placeBrickOnBoard(brickToPlace,
                    ((int) dragEvent.getSceneX()) / BRICK_SIZE,
                    ((int) dragEvent.getSceneY()) / BRICK_SIZE);

            stats.brickPlaced();
            if (successfully) {
                redrawBoard();

                String nextBrickPrompt = CommandsAPI.buildCommand(CommandsAPI.GET_NEXT_BRICK,
                        Integer.toString(stats.getBricksPlaced()));
                gameManager.sendCommand(nextBrickPrompt);
            }
        } catch (Exception ex) {
        }
    }

    @FXML
    public void onGameStartStopClick() {
        if (blockedUI) return;

        try {
            if (!stats.gameStarted()) {
                // start game
                sendStartGame();
                startStopButton.setText("Завершить игру");
            } else {
                endGame();
            }
        } catch (Exception ex) {
        }
    }

    private void sendStartGame() {
        if (stats.gameStarted()) return;

        resetCurrentTime();
        gameManager.getGameBoard().reset();
        stats.reset();

        String clientName = gameManager.replaceWhiteSpaces(stats.getName());
        String command = CommandsAPI.buildCommand(CommandsAPI.STARTING_GAME,
                clientName);
        gameManager.sendCommand(command);
    }

    public void startGame() {
        // refresh UI properties
        partnerName.setText(stats.getOpponentName());

        System.out.println("stats.getOpponentName() = " + stats.getOpponentName());

        String maxTime = convertTime(stats.getMaxSessionTime());
        timeout.setText(maxTime);

        System.out.println("stats.getMaxSessionTime() = " + maxTime);
    }

    private void endGame() {
        //stop game time
        stopwatch.stop();

        String command = CommandsAPI.buildCommand(CommandsAPI.LEAVE_GAME,
                Integer.toString(stats.getBricksPlaced()),
                Long.toString(stats.getGameSessionDuration()));
        gameManager.sendCommand(command);
    }

//    var exitWindow = setUpExitWindow();
//    ButtonType newGameButton = new ButtonType("Начать новую игру");
//    ButtonType exitConfirmedButton = new ButtonType("Выйти");
//
//        exitWindow.getButtonTypes().clear();
//        exitWindow.getButtonTypes().addAll(newGameButton, exitConfirmedButton);
//    Window window = exitWindow.getDialogPane().getScene().getWindow();
//        window.setOnCloseRequest(e -> {
//        stopwatch.play();
//        exitWindow.close();
//    });
//
//    var response = exitWindow.showAndWait();
//        if (response.isPresent()) {
//        handleButtonsResponse(response, newGameButton, exitConfirmedButton);
//    }

    public void displayBrickToDrag(boolean[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                preview.getBoardNode(i, j).setFill(setColor(matrix[i][j]));
            }
        }
    }

    public void resetCurrentTime() {
        currentSessionStopwatch.setText("00:00:00");
        stopwatch.stop();
    }

    public void setUpStopWatch() {
        //sets stopwatch
        stopwatch = new Timeline(new KeyFrame(Duration.seconds(1), actionEvent -> {
            stats.updateStopWatch();

            long seconds = stats.getGameSessionDuration();
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
        exitAlertWindow.setHeaderText("Ходов: " + stats.getBricksPlaced() + ". Время в игре: "
                + convertTime(stats.getGameSessionDuration()));
        exitAlertWindow.setContentText("Вы действительно хотите выйти?");

        return exitAlertWindow;
    }

    private void handleButtonsResponse(Optional<ButtonType> response, ButtonType newGameButton,
                                       ButtonType exitConfirmedButton) {
        if (response.isEmpty()) {
            throw new IllegalStateException("Unknown button response");
        } else if (response.get() == newGameButton) {
            // reset board
            // reset game session stats
            // send command for starting game
            // TODO
            //gameManager.sta();
        } else if (response.get() == exitConfirmedButton) {
            Platform.exit();
        } else {
            throw new IllegalStateException("Unknown button response");
        }
    }

    public String getUserNameFromDialog() {
        if (!Platform.isFxApplicationThread()) {
            return null;
        }

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
                    name = result.get();
                    break;
                } else {
                    correct = false;
                }
            }
            inputDialog.close();
            return name;
    }

    public void setUpTimeout(long time) {
        //sets timer
        Timer timer = new Timer();

        TimerTask closeSession = new TimerTask() {
            @Override
            public void run() {
                String bricksPlaced = Integer.toString(stats.getBricksPlaced());
                String sessionTime = Long.toString(stats.getGameSessionDuration());
                String command = CommandsAPI.buildCommand(CommandsAPI.LEAVE_GAME,
                        bricksPlaced,
                        sessionTime);

                gameManager.sendCommand(command);
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

    private String convertTime(long time) {
        long hours = time / 360;
        long minutes = (time - hours * 360) / 60;
        long seconds = time % 60;

        return String.format("%02d:%02d:%02d", hours,
                minutes, seconds);
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

    public void waitForAction(String displayActionStatus) {
        blockUI();
        status.setText(displayActionStatus);
    }

    public void resetActionStatus() {
        unblockUI();
        status.setText("");
    }
}