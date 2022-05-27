package ru.hse.germandilio.tetris.client.model.client;

import ru.hse.germandilio.tetris.client.controllers.ActionProvider;
import ru.hse.germandilio.tetris.client.model.GameResult;
import ru.hse.germandilio.tetris.client.model.GameSessionStats;
import ru.hse.germandilio.tetris.commands.CommandsAPI;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClientCommandHandler {
    private final ActionProvider gameManager;
    private final GameSessionStats gameStats;

    public ClientCommandHandler(ActionProvider gameManager, GameSessionStats userStats) {
        this.gameManager = gameManager;
        this.gameStats = userStats;
    }

    public void handle(CommandsAPI command, List<String> arguments) {
        switch(command) {
            case CONNECTED -> hasConnected();
            case WAITING_FOR_NEW_GAME -> waitForNewGame();
            case START_GAME -> startGame(false, arguments);
            case START_GAME_SINGLE -> startGame(true, arguments);
            case NEXT_BRICK -> placeNextPrick(arguments);
            case WAITING_FOR_END_GAME -> waitForEndGame();
            case END_GAME -> endGame(arguments);
            case END_GAME_OPPONENT_LEAVE -> endGameWithoutOpponentResults();
            case SERVER_DISCONNECTED -> forceEndGame();
            case TOP_10 -> showTopResults(arguments);
        }
    }

    private void hasConnected() {
        gameManager.unblockUser();
    }

    private void showTopResults(List<String> arguments) {
        int listLength = Integer.parseInt(arguments.get(0));
        List<GameResult> results = new ArrayList<>();
        for (int i = 0; i < listLength; i++) {
            results.add(parseGameResult(i, arguments));
        }

        gameManager.showTopResults(Collections.unmodifiableList(results));
    }

    private void forceEndGame() {
        gameManager.forceEndGame();
    }

    private void endGameWithoutOpponentResults() {
        gameManager.endGameWithoutOpponentResults();
    }

    private void endGame(List<String> arguments) {
        String opponentName = toNaturalView(arguments.get(0));
        gameStats.setOpponentName(opponentName);

        long opponentSessionTime = Long.parseLong(arguments.get(1));
        gameStats.setOpponentGameDuration(opponentSessionTime);

        int opponentBricks = Integer.parseInt(arguments.get(2));
        gameStats.setOpponentBricksPlaced(opponentBricks);

        String winnerName = toNaturalView(arguments.get(3));
        gameStats.setWinnerName(winnerName);

        gameManager.endGame();
    }

    private void waitForEndGame() {
        gameManager.waitForEndGame();
    }

    private void placeNextPrick(List<String> arguments) {
        var charsBrick = arguments.get(0).toCharArray();
        var brick = convertBrick(charsBrick);

        gameManager.placeBrick(brick);
    }

    private void startGame(boolean singleMode, List<String> arguments) {
        if (singleMode) {
            gameStats.setOpponentName("");

            long maxSessionTime = Long.parseLong(arguments.get(0));
            gameStats.setMaxSessionTime(maxSessionTime);
        } else {
            String opponentName = arguments.get(0);
            gameStats.setOpponentName(opponentName);

            long maxSessionTime = Long.parseLong(arguments.get(1));
            gameStats.setMaxSessionTime(maxSessionTime);
        }

        gameManager.startGame();
    }

    private void waitForNewGame() {
        gameManager.waitForNewGame();
    }

    private boolean[][] convertBrick(char[] brickString) {
        int length = 3;
        boolean[][] brick = new boolean[length][length];
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                int item = Integer.parseInt(Character.toString(brickString[i * length + j]));
                brick[i][j] = item != 0;
            }
        }
        return brick;
    }

    private String toNaturalView(String string) {
        return string.replaceAll("%20", " ");
    }

    private GameResult parseGameResult(int indexPos, List<String> arguments) {
        String playerName = arguments.get(1 + indexPos);
        LocalDateTime time = LocalDateTime.parse(arguments.get(2 + indexPos), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        int movesCount = Integer.parseInt(arguments.get(3 + indexPos));
        long sessionDuration = Long.parseLong(arguments.get(4 + indexPos));

        return new GameResult(playerName, time, movesCount, sessionDuration);
    }
}
