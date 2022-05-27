package ru.hse.germandilio.tetris.client.model.client;

import ru.hse.germandilio.tetris.client.controllers.ActionProvider;
import ru.hse.germandilio.tetris.client.model.GameSessionStats;
import ru.hse.germandilio.tetris.commands.CommandsAPI;
import ru.hse.germandilio.tetris.server.clienthandling.CommandSender;

import java.io.*;
import java.net.Socket;

public class Client implements AutoCloseable, CommandSender {
    private final ClientCommandHandler commandHandler;

    private final Socket socket;
    private final PrintWriter output;
    private final BufferedReader input;

    public Client(String host, int port, ActionProvider gameManager, GameSessionStats userStats) throws IOException {
        socket = new Socket(host, port);

        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

        commandHandler = new ClientCommandHandler(gameManager, userStats);
    }

    public void handle() {
        while (!socket.isClosed()) {
            try {
                String userInput = input.readLine();

                // TODO replace
                System.out.println("Получено от сервера: ");
                System.out.println(userInput);

                String stringCommand = getStringCommand(userInput);

                CommandsAPI command = CommandsAPI.getCommandType(stringCommand);
                var arguments = CommandsAPI.getArguments(command, userInput);

                commandHandler.handle(command, arguments);
            } catch (IOException e) {
                System.out.println("Server connection exception.");
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
        output.println(command);
    }
}
