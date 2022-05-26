package ru.hse.germandilio.tetris.client.controllers;

import ru.hse.germandilio.tetris.client.model.UserStats;
import ru.hse.germandilio.tetris.client.model.client.Client;
import ru.hse.germandilio.tetris.client.model.gameboard.GameBoard;
import ru.hse.germandilio.tetris.commands.GameStatus;
import ru.hse.germandilio.tetris.commands.UserCommand;

import java.io.IOException;

public class GameManager implements ActionProvider {
    private static final int DEFAULT_PORT = 5000;
    private static final String DEFAULT_IP = "127.0.0.1";

    private final TetrisViewController viewController;
    private final GameBoard gameBoard;

    private final UserStats userStats;
    private Client client;

    public GameManager(TetrisViewController controller) {
        viewController = controller;

        gameBoard = new GameBoard();
        userStats = new UserStats();
    }

    public void startGame() throws IOException {
        viewController.initGameView(this, userStats);
        waitForAction("Регистрация на сервере");

        client = new Client(DEFAULT_IP, DEFAULT_PORT, this);

        client.waitingForNewGame();
        client.startNewGame();
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public void startNewGame() {
        gameBoard.reset();
        userStats.reset();
        viewController.reset();
    }

    @Override
    public void startTimer(long time) {
        viewController.setUpTimeout(time);
        viewController.setUpMaxTime(time);
    }

    @Override
    public void startGame(String partnerName) {
        userStats.setPartnerName(partnerName);
        viewController.setUpPartnerName(partnerName);
        viewController.setUpStopWatch();
        waitForNextBrick();
    }

    @Override
    public void endGame(int opponentBricks, long opponentTime, int myBricks, long myTime,
                        String winnerName) {
        viewController.exitWithResults(opponentBricks, opponentTime, myBricks, myTime, winnerName);
    }

    @Override
    public void endGameSingle() {
        viewController.exitWithResults();
    }

    @Override
    public String getUserName() {
        String name = viewController.getUserNameFromDialog();
        userStats.setName(name);
        viewController.setUpUserName(name);
        return name;
    }

    @Override
    public long getGameTime() {
        return 0;
    }

    @Override
    public void waitForNextBrick() {
        client.getNextBrick();
        client.waitForNextBrick();
    }

    @Override
    public void placeNextBrick(boolean[][] brick) {
        viewController.setUpNewBrick(brick);
    }

    @Override
    public void waitForAction(String label) {
        viewController.setStatus(label);
    }

    @Override
    public void setStatus(GameStatus status) {
        userStats.setStatus(status);
    }

    @Override
    public GameStatus getStatus() {
        return userStats.getStatus();
    }

    @Override
    public void closeBlockingWindow() {
        viewController.resetStatus();
    }

    public void exit() {
        if (client != null) {
            client.quit();
        }
    }

    public void sendEndGame() {
        String end = UserCommand.buildCommand(UserCommand.END_GAME, Long.toString(userStats.getGameSessionDuration()));
        client.sendCommand(end);
        client.waitForEndGame();
    }
}
