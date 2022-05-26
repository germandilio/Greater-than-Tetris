package ru.hse.germandilio.tetris.server.game;

import ru.hse.germandilio.tetris.commands.CommandsAPI;
import ru.hse.germandilio.tetris.server.clienthandling.CommandSender;
import ru.hse.germandilio.tetris.server.clienthandling.Connection;
import ru.hse.germandilio.tetris.server.generator.BricksRandomGenerator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ServerGameManager {
    private final int maxUsersNumber;
    private final long maxSessionTime;

    private final Map<Connection, CommandSender> userConnections = Collections.synchronizedMap(new HashMap<>());

    private int currentUsersCount;
    private BricksRandomGenerator brickGenerator;

    public ServerGameManager(int usersNumber, long maxSessionTime) {
        this.maxUsersNumber = usersNumber;
        this.maxSessionTime = maxSessionTime;
    }

    public int getMaxUsersNumber() {
        return maxUsersNumber;
    }

    public long getMaxSessionTime() {
        return maxSessionTime;
    }

    public int getCurrentUsersCount() {
        return currentUsersCount;
    }

    public synchronized Connection playerConnected(CommandSender clientHandler) {
        if (currentUsersCount == maxUsersNumber)
            throw new IllegalStateException("Current clients number is bigger than max affordable number.");

        currentUsersCount++;

        // zero based id
        var connection = new Connection(currentUsersCount - 1);
        userConnections.put(connection, clientHandler);
        return connection;
    }

    public synchronized void setName(Connection client, String name) {
        client.setName(name);

        // TODO replace
        System.out.println("Получено имя клиента: " + name);
    }

    public boolean readyToStartGame() {
        boolean readyForGame = true;

        for(var entry : userConnections.entrySet()) {
            var key = entry.getKey();
            readyForGame &= key.startedGame();
        }

        return readyForGame;
    }

    public synchronized void startGame(Connection client) {
        brickGenerator = new BricksRandomGenerator();

        var handler = userConnections.get(client);
        var opponent = getOpponent(client);
        if (opponent == null) {
            // single mode game
            String command = CommandsAPI.buildCommand(CommandsAPI.START_GAME,
                    "",
                    Long.toString(maxSessionTime));
            handler.sendCommand(command);
        } else {
            var opponentHandler = userConnections.get(opponent);

            String commandToOpponent = CommandsAPI.buildCommand(CommandsAPI.START_GAME,
                    client.getName(),
                    Long.toString(maxSessionTime));
            String commandToClient = CommandsAPI.buildCommand(CommandsAPI.START_GAME,
                    opponent.getName(),
                    Long.toString(maxSessionTime));

            handler.sendCommand(commandToClient);
            opponentHandler.sendCommand(commandToOpponent);
        }
    }

    public boolean readyToEndGame() {
        boolean readyEndGame = true;

        for(var entry : userConnections.entrySet()) {
            var key = entry.getKey();
            readyEndGame &= key.endedGame();
        }

        return readyEndGame;
    }

    public synchronized void endGame(Connection client) {
        var handler = userConnections.get(client);
        var opponent = getOpponent(client);
        if (opponent == null) {
            // single mode game
            String command = CommandsAPI.buildCommand(CommandsAPI.END_GAME,
                    "",
                    Long.toString(0L),
                    Integer.toString(0),
                    client.getName());
            handler.sendCommand(command);
        } else {
            var opponentHandler = userConnections.get(opponent);

            String winnerName = getWinnerName(client, opponent);

            String commandToOpponent = CommandsAPI.buildCommand(CommandsAPI.END_GAME,
                    client.getName(),
                    Long.toString(client.getGameSessionDuration()),
                    Integer.toString(client.getBrickPlaced()),
                    winnerName);

            String commandToClient = CommandsAPI.buildCommand(CommandsAPI.END_GAME,
                    opponent.getName(),
                    Long.toString(opponent.getGameSessionDuration()),
                    Integer.toString(opponent.getBrickPlaced()),
                    winnerName);

            handler.sendCommand(commandToClient);
            opponentHandler.sendCommand(commandToOpponent);
        }

        //TODO replace
        System.out.println("Отправляю игрокам конец игры");
    }

    public synchronized void sendWaitingStartGame(Connection client) {
        var handler = userConnections.get(client);
        String command = CommandsAPI.buildCommand(CommandsAPI.WAITING_FOR_NEW_GAME);
        handler.sendCommand(command);

        // TODO replace
        System.out.println("Отправлено ожидание начала игры игроку с именем: " + client.getName());
    }

    public synchronized void sendWaitingEndGame(Connection client) {
        var handler = userConnections.get(client);
        String command = CommandsAPI.buildCommand(CommandsAPI.WAITING_FOR_END_GAME);
        handler.sendCommand(command);

        // TODO replace
        System.out.println("Отправлено ожидание конца игры игроку с именем: " + client.getName());
    }

    public synchronized void sendEndGameWithoutResults(Connection client) {
        var opponent = getOpponent(client);
        if (opponent == null) {
            // in single mode the only one client leaves game.
            return;
        }

        String command = CommandsAPI.buildCommand(CommandsAPI.END_GAME_OPPONENT_LEAVE);
        var opponentHandler = userConnections.get(opponent);
        opponentHandler.sendCommand(command);

        // TODO replace
        System.out.println("Отправлен конец игры без результатов игроку с именем: " + opponent.getName());

        try {
            opponentHandler.close();
        } catch (Exception ex) {
            System.out.println("Crash on closing client handler. It will be shut down automatically.");
        }
    }

    public synchronized void sendNextBrick(Connection client, int indexInSequence) {
        // TODO replace
        if (brickGenerator == null) {
            System.out.println("Не могу отправить брик потому что не начата игра.");
        }

        var handler = userConnections.get(client);

        var brick = brickGenerator.getBrick(indexInSequence);
        String stringBrick = brickGenerator.convertToString(brick);

        String command = CommandsAPI.buildCommand(CommandsAPI.NEXT_BRICK, stringBrick);
        handler.sendCommand(command);
    }

    public synchronized void saveGameSessionResults(Connection client) {
        // dataProvider
    }

    public synchronized void getTopSessions(Connection client, int topNumber) {
        // dataProvider
    }

    private synchronized Connection getOpponent(Connection user) {
        //TODO replace
        assert user != null;

        if (currentUsersCount == 1) {
            return null;
        }

        for (var entry : userConnections.entrySet()) {
            var connection = entry.getKey();
            if (!user.equals(connection)) {
                return connection;
            }
        }

        return null;
    }

    private String getWinnerName(Connection client1, Connection client2) {
        if (client1.getBrickPlaced() == client2.getBrickPlaced()) {
            if (client1.getGameSessionDuration() < client2.getGameSessionDuration()) {
                return client1.getName();
            } else {
                return client2.getName();
            }
        } else {
            if (client1.getBrickPlaced() > client2.getBrickPlaced()) {
                return client1.getName();
            } else {
                return client2.getName();
            }
        }
    }
}
