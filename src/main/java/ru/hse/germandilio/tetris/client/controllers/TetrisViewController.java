package ru.hse.germandilio.tetris.client.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import ru.hse.germandilio.tetris.client.application.TetrisApplication;
import ru.hse.germandilio.tetris.client.model.GameSessionStats;
import ru.hse.germandilio.tetris.client.model.ViewGameResult;
import ru.hse.germandilio.tetris.client.model.gameboard.GameBoard;
import ru.hse.germandilio.tetris.server.clienthandling.CommandSender;
import ru.hse.germandilio.tetris.shared.commands.CommandsAPI;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import static ru.hse.germandilio.tetris.client.model.gameboard.GameBoard.BRICK_SIZE;
import static ru.hse.germandilio.tetris.client.model.gameboard.GameBoard.GAME_FIELD_SIZE;

public class TetrisViewController {
    // !----------------------- FXML COMPONENTS -----------------------!

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

    @FXML
    public Button top10Button;

    // !----------------------- BOARD COLORS -----------------------!

    private final Color dragBrickColor = Color.web("6195E2");
    private final Color initialColor = Color.TRANSPARENT;
    private final Color brickColor = Color.web("948E8E");

    // !----------------------- BRICK PROPERTIES -----------------------!

    private BrickToDragController brickToDragController;
    private boolean[][] brickToPlace;
    private final String draggingBrick = "$$$dragging brick$$$";

    // !----------------------- BUSINESS LOGIC COMPONENTS -----------------------!

    private GameSessionStats stats;
    private CommandSender gameManager;
    private GameBoard gameBoard;

    private boolean blockedUI = false;

    private Timeline stopwatch;

    public void blockUI() {
        blockedUI = true;
    }

    public void unblockUI() {
        blockedUI = false;
    }

    // !----------------------- UI CONTROL & UPDATE -----------------------!

    public void initGameView(CommandSender gameManager, GameBoard gameBoard, GameSessionStats stats) {
        this.stats = stats;
        this.gameManager = gameManager;
        this.gameBoard = gameBoard;

        gameBoard.initDisplayBoard();
        initGameBoard();
        initBrickToDragBoard();
    }

    public void displayBrickToDrag(boolean[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                brickToDragController.getBoardNode(i, j).setFill(setColor(matrix[i][j]));
            }
        }
    }

    public void resetCurrentTime() {
        currentSessionStopwatch.setText("00:00:00");
        if (stopwatch != null) {
            stopwatch.stop();
        }
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

    public void setUpTimeout(long time) {
        //sets timer
        Timer timer = new Timer();

        TimerTask closeSession = new TimerTask() {
            @Override
            public void run() {
                if (stats.isEndedGame()) return;

                // update last stopWatch clock to avoid race condition time mismatch.
                stats.stopGame();
                long seconds = stats.getGameSessionDuration();
                currentSessionStopwatch.setText(convertTime(seconds));

                // send leave game command
                endGame();
            }
        };
        timer.schedule(closeSession, time * 1000);
    }

    public void setUpNewBrick(boolean[][] brick) {
        brickToPlace = brick;

        displayBrickToDrag(brickToPlace);
    }

    private void initGameBoard() {
        // create game fields
        for (int i = 0; i < GAME_FIELD_SIZE; i++) {
            for (int j = 0; j < GAME_FIELD_SIZE; j++) {
                gridPanel.add(gameBoard.getDisplayBoardNode(i, j), j, i);
            }
        }
    }

    private void initBrickToDragBoard() {
        brickToDragController = new BrickToDragController();
        brickToDragController.initPreviewBoard();

        for (int i = 0; i < brickToDragController.getBoardSize(); i++) {
            for (int j = 0; j < brickToDragController.getBoardSize(); j++) {
                brickToDrag.add(brickToDragController.getBoardNode(i, j), j, i);
            }
        }
    }

    private void redrawBoard() {
        for (int i = 0; i < GAME_FIELD_SIZE; i++) {
            for (int j = 0; j < GAME_FIELD_SIZE; j++) {
                redrawRectangular(gameBoard.getGameBoardNode(i, j),
                        gameBoard.getDisplayBoardNode(i, j));
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

    // !----------------------- DRAG & DROP -----------------------!

    @FXML
    public void handleDragOnNextBrick(MouseEvent mouseEvent) {
        if (blockedUI) return;
        if (stats == null || !stats.gameStarted()) return;

        Dragboard db = brickToDrag.startDragAndDrop(TransferMode.ANY);

        ClipboardContent context = new ClipboardContent();
        context.putString(draggingBrick);

        db.setContent(context);
        mouseEvent.consume();
    }

    @FXML
    public void handleDragOver(DragEvent dragEvent) {
        if (blockedUI) return;
        if (stats == null || !stats.gameStarted()) return;

        if (dragEvent.getDragboard().hasString()) {
            dragEvent.acceptTransferModes(TransferMode.ANY);
        }
    }

    @FXML
    public void handleBrickDropped(DragEvent dragEvent) {
        if (blockedUI) return;
        if (stats == null || !stats.gameStarted()) return;

        try {
            if (!dragEvent.getDragboard().hasString()) return;
            String string = dragEvent.getDragboard().getString();
            if (!string.equals(draggingBrick)) return;

            // is it placed on board
            boolean successfully = gameBoard.placeBrickOnBoard(brickToPlace,
                    ((int) dragEvent.getSceneX() - 270) / BRICK_SIZE - 1,
                    ((int) dragEvent.getSceneY()) / BRICK_SIZE);

            if (successfully) {
                stats.brickPlaced();

                // update board
                redrawBoard();

                // send on server next brick command
                String nextBrickPrompt = CommandsAPI.buildCommand(CommandsAPI.GET_NEXT_BRICK,
                        Integer.toString(stats.getBricksPlaced()));
                gameManager.sendCommand(nextBrickPrompt);
            }
        } catch (Exception ignored) {
        }
    }

    // !----------------------- GAME MANAGER HANDLERS -----------------------!

    @FXML
    public void onGameStartStopClick(MouseEvent mouseEvent) {
        if (blockedUI) return;

        try {
            if (!stats.gameStarted()) {
                sendStartGame();
                // update button name (switch)
                startStopButton.setText("Завершить игру");
            } else {
                endGame();
            }
        } catch (Exception ignored) {
        }
    }

    @FXML
    public void onTop10ResultClick(MouseEvent mouseEvent) {
        if (blockedUI) return;

        final int defaultTopCount = 10;
        try {
            if (!stats.gameStarted()) {
                String command = CommandsAPI.buildCommand(CommandsAPI.GET_TOP,
                        Integer.toString(defaultTopCount));
                gameManager.sendCommand(command);
            }
        } catch (Exception ignored) {
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
            inputDialog.setTitle("Регистрация");
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

    private void sendStartGame() {
        if (stats.gameStarted()) return;

        // clear state before starting game
        clearWindow();

        String clientName = replaceWhiteSpaces(stats.getName());
        String command = CommandsAPI.buildCommand(CommandsAPI.STARTING_GAME,
                clientName);
        gameManager.sendCommand(command);

        blockUI();
    }

    public void startGame() {
        // refresh UI properties
        partnerName.setText(stats.getOpponentName());

        String maxTime = convertTime(stats.getMaxSessionTime());
        timeout.setText(maxTime);
    }

    private void endGame() {
        //stop game time
        stopwatch.stop();

        // save local ending game state
        stats.setEndedGame(true);

        String command = CommandsAPI.buildCommand(CommandsAPI.LEAVE_GAME,
                Integer.toString(stats.getBricksPlaced()),
                Long.toString(stats.getGameSessionDuration()));
        gameManager.sendCommand(command);
    }

    public void showEndGameResults() {
        if (!Platform.isFxApplicationThread()) {
            return;
        }
        if (stopwatch != null) {
            stopwatch.stop();
        }

        // setup window based on game session results
        var exitWindow = setupEndGameWindow();
        exitWindow.setContentText("Начать новую игру?");

        ButtonType newGameButton = new ButtonType("Да");
        ButtonType top10Games = new ButtonType("ТОП 10");
        ButtonType exitConfirmedButton = new ButtonType("Выйти");

        // add buttons handler for "new game" of "quit" buttons
        exitWindow.getButtonTypes().clear();
        exitWindow.getButtonTypes().addAll(newGameButton, top10Games, exitConfirmedButton);
        Window window = exitWindow.getDialogPane().getScene().getWindow();

        // set reset all properties except username on closing alert
        setOnCloseGameResults(window, exitWindow);

        try {
            var response = exitWindow.showAndWait();
            response.ifPresent(buttonType -> handleButtonsResponse(buttonType, newGameButton, top10Games, exitConfirmedButton));
        } catch (IllegalStateException ex) {
            clearWindow();
        }
    }

    public void showQuitApp() {
        if (!Platform.isFxApplicationThread()) {
            return;
        }
        Alert exitAlertWindow = new Alert(Alert.AlertType.ERROR);

        exitAlertWindow.setTitle("Уведомление");
        exitAlertWindow.setHeaderText("Сервер неожиданно завершил работу");

        ButtonType exitConfirmedButton = new ButtonType("Выйти");

        // add buttons handler for "new game" of "quit" buttons
        exitAlertWindow.getButtonTypes().clear();
        exitAlertWindow.getButtonTypes().addAll(exitConfirmedButton);
        Window window = exitAlertWindow.getDialogPane().getScene().getWindow();

        // set exit window handler
        window.setOnCloseRequest(e -> {
            // close alerting pop-up window
            exitAlertWindow.close();
            // close application
            exitApplication();
        });

        try {
            exitAlertWindow.showAndWait();
        } finally {
            exitApplication();
        }
    }

    public void showTopSessions(List<ViewGameResult> results) {
        blockUI();

        Platform.runLater(() -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(TetrisApplication.class.getResource("top-results.fxml"));
                Scene scene = new Scene(fxmlLoader.load());

                TopResultsController topResultsController = fxmlLoader.getController();

                // transfer result data to view controller
                topResultsController.setResultsSet(results);

                Stage stage = new Stage();
                stage.setTitle("TOP 10");
                stage.setScene(scene);

                // unblock parent window UI
                stage.setOnCloseRequest((windowEvent) -> {
                    unblockUI();
                    stage.close();
                });

                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void setupUserName(String name) {
        userName.setText(name);
    }

    public void waitForAction(String displayActionStatus) {
        if (!Platform.isFxApplicationThread()) {
            return;
        }

        blockUI();
        status.setText(displayActionStatus);
    }

    public void resetActionStatus() {
        if (!Platform.isFxApplicationThread()) {
            return;
        }

        unblockUI();
        status.setText("");
    }

    private void handleButtonsResponse(ButtonType response, ButtonType newGameButton, ButtonType top10Games,
                                       ButtonType exitConfirmedButton) {
        if (response == newGameButton) {
            // clear previous game session stats
            stats.reset();
            // clearing window and send starting game on server
            sendStartGame();
            startStopButton.setText("Завершить игру");
        } else if (response == exitConfirmedButton) {
            exitApplication();
        } else if (response == top10Games) {
            // clear state before viewing top 10
            clearWindow();

            onTop10ResultClick(null);
        } else {
            throw new IllegalStateException("Unknown button response");
        }
    }

    private Alert setupEndGameWindow() {
        Alert exitAlertWindow = new Alert(Alert.AlertType.INFORMATION);

        exitAlertWindow.setTitle("Результаты игры");

        // set body properties
        if (stats.isOpponentLeaveGame()) {
            setupWithoutResultsAlert(exitAlertWindow);
        } else {
            setupWithResultsAlert(exitAlertWindow);
        }
        return exitAlertWindow;
    }

    private void setupWithoutResultsAlert(Alert alertToSetup) {
        String clientStats = "Фигур: " + stats.getBricksPlaced() + ". Время в игре: "
                + convertTime(stats.getGameSessionDuration()) + "\n";

        alertToSetup.setHeaderText("Вы:\n" + clientStats + "Победитель: " + stats.getName());
    }

    private void setupWithResultsAlert(Alert alertToSetup) {
        String clientStats = "Фигур: " + stats.getBricksPlaced() + ". Время в игре: "
                + convertTime(stats.getGameSessionDuration()) + ".\n";

        String opponentStats = "";
        if (stats.hasOpponent()) {
            opponentStats = stats.getOpponentName() + ":\nФигур: " +
                    stats.getOpponentBricksPlaced() + ". Время в игре: " +
                    convertTime(stats.getOpponentGameDuration()) + ".\n";
        }

        alertToSetup.setHeaderText("Вы:\n" + clientStats + opponentStats + "Победитель: " + stats.getWinnerName());
    }

    private void setOnCloseGameResults(Window resultsWindow, Alert alert) {
        resultsWindow.setOnCloseRequest(e -> {
            // if user wants to close game result window
            clearWindow();

            // close alerting pop-up window
            alert.close();
        });
    }

    private void clearWindow() {
        // reset UI timers
        resetActionStatus();
        resetCurrentTime();
        timeout.setText("00:00:00");
        partnerName.setText("");

        // reset board and dragging preview
        stats.reset();
        gameBoard.reset();
        brickToDragController.reset();

        // reset button state (switch)
        startStopButton.setText("Начать игру");
    }

    private void exitApplication() {
        try {
            String command = CommandsAPI.buildCommand(CommandsAPI.CLIENT_DISCONNECTED);
            gameManager.sendCommand(command);

            gameManager.close();
        } catch (Exception ignored) {
        } finally {
            // exit application
            Platform.exit();
        }
        System.exit(0);
    }

    private String replaceWhiteSpaces(String string) {
        return string.replaceAll(" ", "%20");
    }

    private String convertTime(long time) {
        long hours = time / 360;
        long minutes = (time - hours * 360) / 60;
        long seconds = time % 60;

        return String.format("%02d:%02d:%02d", hours,
                minutes, seconds);
    }
}