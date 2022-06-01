package ru.hse.germandilio.tetris.client.controllers;

import javafx.application.Platform;
import ru.hse.germandilio.tetris.client.model.GameSessionStats;
import ru.hse.germandilio.tetris.client.model.ViewGameResult;
import ru.hse.germandilio.tetris.client.model.client.Client;
import ru.hse.germandilio.tetris.client.model.gameboard.GameBoard;
import ru.hse.germandilio.tetris.server.clienthandling.CommandSender;
import ru.hse.germandilio.tetris.shared.commands.CommandsAPI;

import java.net.UnknownHostException;
import java.util.List;

public class GameManager implements ActionProvider, AutoCloseable, CommandSender {
    private static final int DEFAULT_PORT = 5000;
    private static final String DEFAULT_IP = "127.0.0.1";

    private static final String USER_WAIT_CONNECTION = "Подключение к серверу...";
    private static final String USER_WAIT_OPPONENT = "Ожидание партнера";

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

    public void launch() {
        viewController.initGameView(this, gameBoard, gameSessionStats);
        waitForAction(USER_WAIT_CONNECTION);

        new Thread(() -> {
            try {
                client = new Client(DEFAULT_IP, DEFAULT_PORT, this, gameSessionStats);

                // handle responses from server
                client.handle();
            } catch (UnknownHostException e) {
                exceptionInServerThread = true;
                forceEndGame();

                System.out.println("Unknown host with address: " + DEFAULT_IP);
            } catch (IllegalArgumentException ex) {
                exceptionInServerThread = true;
                System.out.println("Illegal port: " + DEFAULT_PORT);
            } catch (Exception ex) {
                exceptionInServerThread = true;
                System.out.println("Server connection error.");
            }
        }).start();
    }

    public void registration() {
        viewController.blockUI();

        // preview name in UI
        String name = viewController.getUserNameFromDialog();
        gameSessionStats.setName(name);

        // refresh UI
        viewController.setupUserName(name);
        viewController.unblockUI();
    }

    @Override
    public void showTopResults(List<ViewGameResult> results) {
        if (exceptionInServerThread) return;

        // show new window with top results
        Platform.runLater(() -> {
            // unblock UI
            viewController.resetActionStatus();
            // show
            viewController.showTopSessions(results);
        });
    }

    @Override
    public void forceEndGame() {
        if (exceptionInServerThread) return;

        Platform.runLater(() -> {
            viewController.unblockUI();
            viewController.resetActionStatus();

            // show pop-up menu with only exit button
            viewController.showQuitApp();
        });
    }

    @Override
    public void endGame() {
        if (exceptionInServerThread) return;

        Platform.runLater(() -> {
            viewController.unblockUI();
            viewController.resetActionStatus();

            // show window with results
            viewController.showEndGameResults();
        });
    }

    @Override
    public void waitForEndGame() {
        if (exceptionInServerThread) return;

        // wait
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

        // setup all UI parameters
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

        // wait
        waitForAction(USER_WAIT_OPPONENT);
    }

    @Override
    public void unblockUser() {
        Platform.runLater(viewController::resetActionStatus);
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
}
