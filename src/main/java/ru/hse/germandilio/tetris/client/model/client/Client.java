package ru.hse.germandilio.tetris.client.model.client;

import ru.hse.germandilio.tetris.client.controllers.ActionProvider;
import ru.hse.germandilio.tetris.client.model.GameSessionStats;
import ru.hse.germandilio.tetris.server.clienthandling.CommandSender;
import ru.hse.germandilio.tetris.shared.commands.CommandsAPI;

import java.io.*;
import java.net.Socket;

public class Client implements AutoCloseable, CommandSender {
    public static boolean LOG_COMMANDS_FROM_SERVER = true;
    public static boolean LOG_COMMANDS_TO_SERVER = false;

    private final ClientCommandHandler commandHandler;
    private final ActionProvider gameManager;

    private final Socket socket;
    private final PrintWriter output;
    private final BufferedReader input;


    public Client(String host, int port, ActionProvider gameManager, GameSessionStats userStats) throws IOException {
        socket = new Socket(host, port);

        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

        this.gameManager = gameManager;
        commandHandler = new ClientCommandHandler(gameManager, userStats);
    }

    public void handle() {
        while (!socket.isClosed()) {
            try {
                String userInput = input.readLine();

                if (LOG_COMMANDS_FROM_SERVER) {
                    System.out.println("Received from server:");
                    System.out.println(userInput);
                }

                String stringCommand = getStringCommand(userInput);
                CommandsAPI command = CommandsAPI.getCommandType(stringCommand);
                var arguments = CommandsAPI.getArguments(command, userInput);

                commandHandler.handle(command, arguments);
            } catch (IOException e) {
                gameManager.forceEndGame();
                break;
            } catch (IllegalArgumentException ex) {
                System.out.println(ex.getMessage());
                break;
            } catch (IllegalStateException ex) {
                System.out.println("Generator is null. Check connection and restart the game.");
                break;
            } catch (NullPointerException ex) {
                System.out.println("Input exception. Try again.");
                break;
            } catch (Exception ex) {
                System.out.println("Error:");
                System.out.println(ex.getMessage());
                break;
            }
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
    public void close() throws IOException {
        if (socket != null) {
            socket.close();
        }
    }

    @Override
    public void sendCommand(String command) {
        if (LOG_COMMANDS_TO_SERVER) {
            System.out.println("Send to server:");
            System.out.println(command);
        }

        output.println(command);
    }
}
