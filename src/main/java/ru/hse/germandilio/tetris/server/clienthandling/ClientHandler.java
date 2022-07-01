package ru.hse.germandilio.tetris.server.clienthandling;

import ru.hse.germandilio.tetris.server.game.GameManager;
import ru.hse.germandilio.tetris.shared.commands.CommandsAPI;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

import static ru.hse.germandilio.tetris.server.control.ServerManager.LOG_INPUT_COMMANDS;
import static ru.hse.germandilio.tetris.server.control.ServerManager.LOG_OUTPUT_COMMANDS;

public class ClientHandler implements Runnable, AutoCloseable, CommandSender {
    private final Socket socket;
    private final BufferedReader input;
    private final PrintWriter output;

    private final CommandHandler commandHandler;

    public ClientHandler(Socket socket, GameManager serverGame) throws IOException {
        this.socket = socket;

        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

        var connection = serverGame.playerConnected(this);
        commandHandler = new CommandHandler(serverGame, connection);
    }

    @Override
    public void run() {
        try (socket; input; output) {
            // send successful registration
            String command = CommandsAPI.buildCommand(CommandsAPI.CONNECTED);
            sendCommand(command);

            // handling input
            handlingInput();
        } catch (Exception e) {
            System.out.println("Can't close socket");
        }
    }

    private void handlingInput() throws Exception {
        try (socket) {
            while (!socket.isClosed()) {
                try {
                    String userInput = input.readLine();

                    // log input command from client
                    if (LOG_INPUT_COMMANDS) {
                        System.out.println("From client: ");
                        System.out.println(userInput);
                    }

                    String stringCommand = getStringCommand(userInput);
                    CommandsAPI command = CommandsAPI.getCommandType(stringCommand);
                    var arguments = CommandsAPI.getArguments(command, userInput);

                    commandHandler.handle(command, arguments);
                } catch (IOException e) {
                    System.out.println("Client connection error.");
                    break;
                } catch (IllegalArgumentException | NullPointerException ex) {
                    // wrong command
                    System.out.println(ex.getMessage());
                    break;
                } catch (IllegalStateException ex) {
                    // generator is null
                    System.out.println("Bricks generator is null");
                    System.out.println(ex.getMessage());
                    break;
                }
            }
        } catch (SocketException ex) {
            System.out.println("Client connection error.");
        }
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
    public synchronized void sendCommand(String command) {
        // log
        if (LOG_OUTPUT_COMMANDS) {
            System.out.println("Send to client with socket: " + socket.getInetAddress());
            System.out.println(command);
        }

        output.println(command);
    }
}
