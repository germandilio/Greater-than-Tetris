package ru.hse.germandilio.tetris.client.controllers;

import javafx.application.Platform;
import ru.hse.germandilio.tetris.client.model.GameResult;
import ru.hse.germandilio.tetris.client.model.GameSessionStats;
import ru.hse.germandilio.tetris.client.model.client.Client;
import ru.hse.germandilio.tetris.client.model.gameboard.GameBoard;
import ru.hse.germandilio.tetris.commands.CommandsAPI;
import ru.hse.germandilio.tetris.server.clienthandling.CommandSender;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

public class GameManager implements ActionProvider, AutoCloseable, CommandSender {
    private static final int DEFAULT_PORT = 5000;
    private static final String DEFAULT_IP = "127.0.0.1";

    private final TetrisViewController viewController;
    private final GameBoard gameBoard;

    private final GameSessionStats gameSessionStats;
    private Client client;

    private volatile boolean exceptionInServerThread = false;

    public GameManager(TetrisViewController controller) {
        viewController = controller;

        gameBoard = new GameBoard();
        gameSessionStats = new GameSessionStats();
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public void launch() throws IOException {
        viewController.initGameView(this, gameSessionStats);
        waitForAction("Подключение к серверу...");

        new Thread(() -> {
            try {
                client = new Client(DEFAULT_IP, DEFAULT_PORT, this, gameSessionStats);

                client.handle();
            } catch (UnknownHostException e) {
                exceptionInServerThread = true;
                //TODO platform run later blocking pop-up menu with only exit application button
                forceEndGame();

                System.out.println("Неизвестный ip адрес для подключения: " + DEFAULT_IP);
            } catch (IllegalArgumentException ex) {
                exceptionInServerThread = true;
                System.out.println("Неизвестный порт для подключения: " + DEFAULT_PORT);
            } catch (Exception ex) {
                exceptionInServerThread = true;
                System.out.println("Ошибка соединения с сервером.");
            }
        }).start();
    }

    public void registration() {
        viewController.blockUI();

        String name = viewController.getUserNameFromDialog();
        gameSessionStats.setName(name);

        // refresh UI
        viewController.setUpUserName(name);
        viewController.unblockUI();
    }

    @Override
    public void showTopResults(List<GameResult> results) {
        if (exceptionInServerThread) return;

        // новое дополнительное окно с результатами топ 10 игр

        Platform.runLater(() -> {

        });
    }

    @Override
    public void forceEndGame() {
        if (exceptionInServerThread) return;

        // pop up menu с единсвтенной кнопкой закрыть приложение
        // и подписью об отключении сервера

        Platform.runLater(() -> {

        });
    }

    @Override
    public void endGameWithoutOpponentResults() {
        if (exceptionInServerThread) return;

        // pop up с результатми автоматического победителя без данных оппонента

        Platform.runLater(() -> {
            viewController.resetActionStatus();

        });
    }

    @Override
    public void endGame() {
        if (exceptionInServerThread) return;

        // pop up со всей информацие о противники и себе
        // кнопки начать новую игру или выйти из приложения

        Platform.runLater(() -> {
            viewController.resetActionStatus();

        });
    }

    @Override
    public void waitForEndGame() {
        if (exceptionInServerThread) return;

        waitForAction("Ожидание партнера...");
    }

    @Override
    public void placeBrick(boolean[][] brick) {
        if (exceptionInServerThread) return;

        Platform.runLater(() -> {
            viewController.setUpNewBrick(brick);
        });
    }

    @Override
    public void startGame() {
        if (exceptionInServerThread) return;

        // setupAllViewParameters

        Platform.runLater(() -> {
            gameBoard.reset();

            viewController.startGame();
            viewController.setUpStopWatch();

            viewController.resetActionStatus();

            // start timer for automatic sending command for end_Game
            viewController.setUpTimeout(gameSessionStats.getMaxSessionTime());

            String command = CommandsAPI.buildCommand(CommandsAPI.GET_NEXT_BRICK,
                    Integer.toString(gameSessionStats.getBricksPlaced()));
            sendCommand(command);
        });
    }

    @Override
    public void waitForNewGame() {
        if (exceptionInServerThread) return;

        waitForAction("Ожидание партнера");
    }

    @Override
    public void unblockUser() {
        Platform.runLater(() -> {
            viewController.resetActionStatus();
            viewController.unblockUI();
        });
    }

    @Override
    public synchronized void sendCommand(String command) {
        client.sendCommand(command);
    }

    @Override
    public void close() throws Exception {
        if (client != null) {
            client.close();
        }
    }

    private void waitForAction(String displayActionStatus) {
        Platform.runLater(() -> {
            viewController.waitForAction(displayActionStatus);
        });
    }

    public String replaceWhiteSpaces(String string) {
        return string.replaceAll(" ", "%20");
    }
}
