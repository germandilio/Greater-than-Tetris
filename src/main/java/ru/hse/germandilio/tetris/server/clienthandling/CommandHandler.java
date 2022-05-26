package ru.hse.germandilio.tetris.server.clienthandling;

import ru.hse.germandilio.tetris.commands.CommandsAPI;
import ru.hse.germandilio.tetris.server.game.ServerGameManager;

import java.util.List;

public class CommandHandler {
    private final ServerGameManager serverGame;
    private final Connection connection;

    public CommandHandler(ServerGameManager serverGame, Connection clientConnection) {
        this.serverGame = serverGame;
        this.connection = clientConnection;
    }

    public void handle(CommandsAPI command, List<String> arguments) {
        switch(command) {
            case STARTING_GAME -> startingClientGame(arguments);
            case GET_NEXT_BRICK -> sendBrick(arguments);
            case LEAVE_GAME -> clientLeavingGame(arguments);
            case CLIENT_DISCONNECTED -> clientQuit(arguments);
            case GET_TOP -> getTop(arguments);
        }
    }

    private void startingClientGame(List<String> arguments) {
        // set name for connection
        serverGame.setName(connection, arguments.get(0));

        // send waiting new game
        serverGame.sendWaitingStartGame(connection);

        // try to start game if all users are ready to start
        if (serverGame.readyToStartGame()) {
            serverGame.startGame(connection);
        }
    }

    private void sendBrick(List<String> arguments) {
        // check brick index
        int index = 0;
        try {
            index = Integer.parseInt(arguments.get(0));
        } catch (NumberFormatException e) {
            System.out.println("From client with name=" + connection.getName() + " was received command with wrong index.");
            System.out.println("Server will automatically send brick with index=0");
            return;
        }

        serverGame.sendNextBrick(connection, index);
    }

    private void clientLeavingGame(List<String> arguments) {
        // save client game session configuration
        int brickPlaced = Integer.parseInt(arguments.get(0));
        connection.setBrickPlaced(brickPlaced);

        long duration = Long.parseLong(arguments.get(1));
        connection.setGameSessionDuration(duration);

        // send waiting for end game
        serverGame.sendWaitingEndGame(connection);

        // save to database game session results
        serverGame.saveGameSessionResults(connection);

        // if game ready to end send end_game on clients
        if (serverGame.readyToEndGame()) {
            serverGame.endGame(connection);
        }
    }

    private void clientQuit(List<String> arguments) {
        // send for opponent "end game opponent leave"
        serverGame.sendEndGameWithoutResults(connection);
    }

    private void getTop(List<String> arguments) {
        int top = 10;
        try {
            top = Integer.parseInt(arguments.get(0));
        } catch (NumberFormatException e) {
            System.out.println("From client with name=" + connection.getName() + " was received command with wrong top parameter.");
            System.out.println("Server will automatically send top 10");
            return;
        }

        // send result to client
        serverGame.getTopSessions(connection, top);
    }
}