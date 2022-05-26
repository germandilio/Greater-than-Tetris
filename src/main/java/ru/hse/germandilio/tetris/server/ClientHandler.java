package ru.hse.germandilio.tetris.server;

import ru.hse.germandilio.tetris.commands.CommandSender;
import ru.hse.germandilio.tetris.commands.GameStatus;
import ru.hse.germandilio.tetris.commands.ServerCommand;
import ru.hse.germandilio.tetris.commands.UserCommand;
import ru.hse.germandilio.tetris.server.generator.BricksRandomGenerator;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;


public class ClientHandler implements Runnable, AutoCloseable, CommandSender {
    private final Socket socket;
    private final BufferedReader input;
    private final PrintWriter output;

    private final UserInfo userInfo;
    private final long timeout;

    private final ServerCommandHandler commandHandler;

    public ClientHandler(CallbackProvider statusController, Socket socket, long timeout) throws IOException {
        this.socket = socket;
        this.timeout = timeout;

        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

        userInfo = new UserInfo();
        commandHandler = new ServerCommandHandler(this, statusController, userInfo);
    }

    @Override
    public void run() {
        try (socket; input; output) {
            userInfo.setStatus(GameStatus.CONNECTED);
            // send Connected
            String command = ServerCommand.buildCommand(ServerCommand.CONNECTED, Long.toString(timeout));
            sendCommand(command);

            // handling input
            handlingInput();

        } catch (Exception e) {
            System.out.println("Can't close socket");
        }
    }

    private void handlingInput() throws Exception {
        try (socket) {
            while (userInfo.getStatus() != GameStatus.DISCONNECTED) {
                try {
                    String userInput = input.readLine();
                    String stringCommand = getStringCommand(userInput);

                    UserCommand command = UserCommand.getCommandType(stringCommand);
                    var arguments = UserCommand.getArguments(command, userInput);

                    commandHandler.handle(command, arguments);
                } catch (IOException e) {
                    System.out.println("Connection error");
                    break;
                } catch (IllegalArgumentException ex) {
                    System.out.println(ex.getMessage());
                    // неверная команда
                    break;
                } catch (IllegalStateException ex) {
                    // generator is null
                    System.out.println("Generator is null");
                    break;
                } catch (NullPointerException ex) {
                    break;
                }
            }
        } catch (SocketException ex) {
            System.out.println("Connection error");
        }
    }

    private String getWinnerName(ClientHandler client1, ClientHandler client2) {
        String winnerName;
        if (client1.getUserMoves() > client2.getUserMoves()) {
            winnerName = client1.getUserName();
        } else {
            winnerName = client2.getUserName();
        }
        return winnerName;
    }

    public void startGame(ClientHandler opponent, BricksRandomGenerator generator) {
        commandHandler.setBrickGenerator(generator);

        String response = ServerCommand.buildCommand(ServerCommand.START_GAME, opponent.getUserName());
        sendCommand(response);
    }

    public void endGame(ClientHandler opponent, boolean partnerQuited) {
        userInfo.setStatus(GameStatus.END_GAME);

        String response;
        if (partnerQuited) {
            response = ServerCommand.buildCommand(ServerCommand.END_GAME_SINGLE);
        } else {
            String winnerName = getWinnerName(this, opponent);

            response = ServerCommand.buildCommand(ServerCommand.END_GAME,
                    Integer.toString(opponent.getUserMoves()),
                    Long.toString(opponent.getUserTime()),
                    Integer.toString(this.getUserMoves()),
                    Long.toString(this.getUserTime()),
                    winnerName);
        }

        sendCommand(response);
    }

    /**
     * SINGLE MODE
     *
     * @param generator
     */
    public void startGame(BricksRandomGenerator generator) {
        commandHandler.setBrickGenerator(generator);

        String response = ServerCommand.buildCommand(ServerCommand.START_GAME_SINGLE);
        sendCommand(response);
    }

    /**
     * SINGLE MODE
     */
    public void endGame() {
        userInfo.setStatus(GameStatus.END_GAME);

        String response = ServerCommand.buildCommand(ServerCommand.END_GAME_SINGLE);
        sendCommand(response);
    }

    public GameStatus getUserStatus() {
        return userInfo.getStatus();
    }

    public long getUserTime() {
        return userInfo.getGameDuration();
    }

    public String getUserName() {
        return userInfo.getName();
    }

    public int getUserMoves() {
        if (userInfo.getMovesCount() == 0) {
            return 0;
        }
        return userInfo.getMovesCount() - 1;
    }

    private String getStringCommand(String input) {
        if (input == null) {
            return "";
        }

        String[] arguments = input.split(" ");
        if (arguments.length < 1) return "";
        return arguments[0];
    }

    @Override
    public void close() throws Exception {
        if (!socket.isClosed()) {
            socket.close();
        }

        input.close();
        output.close();
    }

    @Override
    public void sendCommand(String command) {
        output.println(command);
    }
}
