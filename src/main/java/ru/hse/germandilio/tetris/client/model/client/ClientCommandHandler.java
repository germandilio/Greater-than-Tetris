package ru.hse.germandilio.tetris.client.model.client;

import ru.hse.germandilio.tetris.client.controllers.ActionProvider;
import ru.hse.germandilio.tetris.commands.CommandsAPI;
import ru.hse.germandilio.tetris.server.clienthandling.CommandSender;

import java.util.List;

public class ClientCommandHandler {
    private final ActionProvider gameManager;
    private final CommandSender commandSender;

    public ClientCommandHandler(CommandSender commandSender, ActionProvider gameManager) {
        this.gameManager = gameManager;
        this.commandSender = commandSender;
    }

    public void handle(CommandsAPI command, List<String> arguments) {
        switch(command) {
            case CONNECTED -> authorize(arguments);
            case WAITING_START_GAME -> waitStartGame();
            case WAITING_END_GAME -> waitEndGame();
            case START_GAME -> startGame(false, arguments);
            case END_GAME -> endGame(false, arguments);
            case START_GAME_SINGLE -> startGame(true, arguments);
            case END_GAME_SINGLE -> endGame(true, arguments);
            case NEXT_BRICK -> placeNextBrick(arguments);
        }
    }

    private void placeNextBrick(List<String> arguments) {
        var stringBrick = arguments.get(0).toCharArray();

        int length = 3;
        boolean[][] brick = new boolean[length][length];
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                int item = Integer.parseInt(Character.toString(stringBrick[i * length + j]));
                brick[i][j] = item != 0;
            }
        }

        gameManager.placeNextBrick(brick);
    }

    private void endGame(boolean singleMode, List<String> arguments) {
        gameManager.setStatus(GameStatus.END_GAME);

        if (singleMode) {
            gameManager.endGameSingle();
        }
        else {
            gameManager.endGame(Integer.parseInt(arguments.get(0)), Long.parseLong(arguments.get(1)),
                    Integer.parseInt(arguments.get(2)), Long.parseLong(arguments.get(3)),
                    arguments.get(4));
        }
    }

    private void startGame(boolean singleMode, List<String> arguments) {
        gameManager.setStatus(GameStatus.IN_GAME);
        gameManager.closeBlockingWindow();

        String partnerName = singleMode ? null : arguments.get(0);
        gameManager.startGame(partnerName);
    }

    private void waitEndGame() {
        gameManager.setStatus(GameStatus.END_GAME);

        gameManager.waitForAction("Ожидание завершения игры партнером...");
    }

    private void waitStartGame() {
        gameManager.setStatus(GameStatus.READY_TO_START_GAME);

        gameManager.waitForAction("Ожидание партнера...");
    }

    private void authorize(List<String> arguments) {
        gameManager.setStatus(GameStatus.CONNECTED);

        long maxTime = Long.parseLong(arguments.get(0));

        String userName = gameManager.getUserName();
        String name = UserCommand.buildCommand(UserCommand.NAME, userName);
        commandSender.sendCommand(name);

        gameManager.startTimer(maxTime);
    }
}
