package ru.hse.germandilio.tetris.client.model.client;

import ru.hse.germandilio.tetris.client.controllers.ActionProvider;
import ru.hse.germandilio.tetris.client.model.ViewGameResult;
import ru.hse.germandilio.tetris.client.model.GameSessionStats;
import ru.hse.germandilio.tetris.shared.commands.CommandsAPI;

import java.time.*;
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
            case START_GAME -> startGame(arguments);
            case NEXT_BRICK -> placeNextPrick(arguments);
            case WAITING_FOR_END_GAME -> waitForEndGame();
            case END_GAME -> endGame(arguments);
            case END_GAME_OPPONENT_LEAVE -> endGameWithoutOpponentResults();
            case SERVER_DISCONNECTED -> forceEndGame();
            case TOP_RESULTS -> showTopResults(arguments);
        }
    }

    private void hasConnected() {
        gameManager.unblockUser();
    }

    private void showTopResults(List<String> arguments) {
        // convert to GameResults list
        int listLength = Integer.parseInt(arguments.get(0));
        List<ViewGameResult> results = new ArrayList<>();
        for (int i = 0; i < listLength; i++) {
            results.add(parseGameResult(i, arguments));
        }

        // show results view
        gameManager.showTopResults(Collections.unmodifiableList(results));
    }

    private void forceEndGame() {
        gameManager.forceEndGame();
    }

    private void endGameWithoutOpponentResults() {
        // set reason of end game
        gameStats.setOpponentLeave(true);

        // show end game
        gameManager.endGame();
    }

    private void endGame(List<String> arguments) {
        // set reason of end game
        gameStats.setOpponentLeave(false);

        // save opponent name
        String opponentName = toNaturalView(arguments.get(0));
        gameStats.setOpponentName(opponentName);

        // save opponent game duration
        long opponentSessionTime = Long.parseLong(arguments.get(1));
        gameStats.setOpponentGameDuration(opponentSessionTime);

        // save opponent brick placed
        int opponentBricks = Integer.parseInt(arguments.get(2));
        gameStats.setOpponentBricksPlaced(opponentBricks);

        // save winner name
        String winnerName = toNaturalView(arguments.get(3));
        gameStats.setWinnerName(winnerName);

        // show end game
        gameManager.endGame();
    }

    private void waitForEndGame() {
        gameManager.waitForEndGame();
    }

    private void placeNextPrick(List<String> arguments) {
        // retrieve brick index
        var charsBrick = arguments.get(0).toCharArray();
        var brick = convertBrick(charsBrick);

        gameManager.placeBrick(brick);
    }

    private void startGame(List<String> arguments) {
        // save and check opponent name
        String opponentName = toNaturalView(arguments.get(0));
        gameStats.setOpponentName(opponentName);

        // save opponent game duration
        long maxSessionTime = Long.parseLong(arguments.get(1));
        gameStats.setMaxSessionTime(maxSessionTime);

        gameManager.startGame();
    }

    private void waitForNewGame() {
        gameManager.waitForNewGame();
    }

    /**
     * Convert {@code String} represented brick to boolean matrix.
     * @param brickString {@code String} represented brick
     * @return Matrix 3x3 of booleans.
     */
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

    /**
     * Replace all internal symbols to white spaces.
     * @param string {@code String} where replace.
     * @return {@code String} natural view.
     */
    private String toNaturalView(String string) {
        return string.replaceAll("%20", " ");
    }

    private ViewGameResult parseGameResult(int indexPos, List<String> arguments) {
        int offset = indexPos * 4;
        String playerName = toNaturalView(arguments.get(1 + offset));

        // date and time with local time zone
        var timeWithoutTimeZone = LocalDateTime.parse(arguments.get(2 + offset), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        var zonedDateTime = timeWithoutTimeZone.atZone(ZoneOffset.UTC);
        var endGameTime = DateTimeFormatter
                .ofPattern("yyyy-MMM-dd HH:mm:ss z")
                .format(zonedDateTime.withZoneSameInstant(ZoneOffset.systemDefault()));

        String movesCount = arguments.get(3 + offset);

        // localize session time duration
        long sessionDurationInSeconds = Long.parseLong(arguments.get(4 + offset));
        String gameDuration = LocalTime.ofSecondOfDay(sessionDurationInSeconds).format(DateTimeFormatter.ISO_LOCAL_TIME);

        // return viewed object
        return new ViewGameResult(playerName, endGameTime, movesCount, gameDuration);
    }
}
