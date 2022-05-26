package ru.hse.germandilio.tetris.client.model.client;

import ru.hse.germandilio.tetris.client.controllers.ActionProvider;
import ru.hse.germandilio.tetris.commands.CommandSender;
import ru.hse.germandilio.tetris.commands.GameStatus;
import ru.hse.germandilio.tetris.commands.ServerCommand;
import ru.hse.germandilio.tetris.commands.UserCommand;

import java.io.*;
import java.net.Socket;

public class Client implements AutoCloseable, CommandSender {
    private final ActionProvider gameManager;
    private final ClientCommandHandler commandHandler;

    private final Socket socket;
    private final PrintWriter output;
    private final BufferedReader input;

    public Client(String host, int port, ActionProvider gameManager) throws IOException {
        socket = new Socket(host, port);

        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

        this.gameManager = gameManager;
        commandHandler = new ClientCommandHandler(this, gameManager);

        gameManager.setStatus(GameStatus.NON_INITIALIZED);
    }

    public void waitingForNewGame() {
        while (gameManager.getStatus() != GameStatus.READY_TO_START_GAME) {
            handleCommand();
        }
    }

    public void startNewGame() {
        while (gameManager.getStatus() != GameStatus.IN_GAME) {
            handleCommand();
        }
    }

    public void waitForNextBrick() {
        handleCommand();
    }

    private String getStringCommand(String input) {
        if (input == null) {
            return "";
        }

        String[] arguments = input.split(" ");
        if (arguments.length < 1) return "";
        return arguments[0];
    }


    public void endGame(long gameTime) {
        String command = UserCommand.buildCommand(UserCommand.END_GAME, Long.toString(gameTime));
        sendCommand(command);
    }

    public void getNextBrick() {
        String command = UserCommand.buildCommand(UserCommand.GET_NEXT_BRICK);
        sendCommand(command);
    }


    public void quit() {
        String command = UserCommand.buildCommand(UserCommand.QUIT);
        sendCommand(command);
    }

    public void sendCommand(String command) {
        output.println(command);
    }

    @Override
    public void close() throws IOException {
        if (socket != null) {
            socket.close();
        }
    }

    private void handleCommand() {
        try {
            String userInput = input.readLine();
            String stringCommand = getStringCommand(userInput);

            ServerCommand command = ServerCommand.getCommandType(stringCommand);
            var arguments = ServerCommand.getArguments(command, userInput);

            commandHandler.handle(command, arguments);
        } catch (IOException e) {
            System.out.println("Server connection exception.");
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
        } catch (IllegalStateException ex) {
            System.out.println("Generator is null. Check connection and restart the game.");
        } catch(NullPointerException ex) {
            System.out.println("Input exception. Try again.");
        } catch (Exception ex) {
            System.out.println("Error:");
            System.out.println(ex.getMessage());
        }
    }

    public void waitForEndGame() {
        while (gameManager.getStatus() != GameStatus.END_GAME) {
            handleCommand();
        }
    }
}
