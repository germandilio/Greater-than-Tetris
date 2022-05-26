package ru.hse.germandilio.tetris.server;

import ru.hse.germandilio.tetris.commands.CommandSender;
import ru.hse.germandilio.tetris.commands.GameStatus;
import ru.hse.germandilio.tetris.commands.ServerCommand;
import ru.hse.germandilio.tetris.commands.UserCommand;
import ru.hse.germandilio.tetris.server.generator.BricksRandomGenerator;

import java.util.List;

public class ServerCommandHandler {
    private final CallbackProvider statusController;
    private final CommandSender commandSender;

    private final UserInfo userInfo;

    private BricksRandomGenerator bricksGenerator = null;

    public ServerCommandHandler(CommandSender commandSender, CallbackProvider statusController, UserInfo userInfo) {
        this.statusController = statusController;
        this.userInfo = userInfo;
        this.commandSender = commandSender;
    }

    public void handle(UserCommand command, List<String> arguments) {
        switch(command) {
            case NAME -> getUserName(arguments);
            case END_GAME -> endGame(arguments);
            case QUIT -> quit();
            case GET_NEXT_BRICK -> sendBrick();
        }
    }

    public void setBrickGenerator(BricksRandomGenerator generator) {
        bricksGenerator = generator;
    }

    private void sendBrick() {
        if (bricksGenerator == null) {
            throw new IllegalStateException("Generator is null. Probably client send illegal command.");
        }

        var brick = bricksGenerator.getBrick(userInfo.getMovesCount());
        userInfo.incrementMovesCount();

        String command = ServerCommand.buildCommand(ServerCommand.NEXT_BRICK, bricksGenerator.convertToString(brick));
        commandSender.sendCommand(command);
    }

    private void quit() {
        userInfo.setStatus(GameStatus.DISCONNECTED);
        statusController.statusChanged();
    }

    private void endGame(List<String> arguments) {
        try {
            long time = Long.parseLong(arguments.get(0));
            userInfo.setGameDuration(time);

            String command = ServerCommand.buildCommand(ServerCommand.WAITING_END_GAME);
            commandSender.sendCommand(command);

            userInfo.setStatus(GameStatus.END_GAME);
            statusController.statusChanged();

        } catch (NumberFormatException ex) {
            System.out.println("Invalid gameTime from user " + userInfo.getName());
        }
    }

    private void getUserName(List<String> arguments) {
        // reformat to natural view
        String name = arguments.get(0).replaceAll("%20", " ");

        userInfo.setName(name);
        userInfo.setStatus(GameStatus.READY_TO_START_GAME);

        String response = ServerCommand.buildCommand(ServerCommand.WAITING_START_GAME);
        commandSender.sendCommand(response);

        statusController.statusChanged();
    }
}
