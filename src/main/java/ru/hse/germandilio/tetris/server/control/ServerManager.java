package ru.hse.germandilio.tetris.server.control;

import ru.hse.germandilio.tetris.server.Server;

import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;

import static ru.hse.germandilio.tetris.server.Server.DEFAULT_PORT;

public class ServerManager {
    public final static boolean LOG_INPUT_COMMANDS = false;
    public final static boolean LOG_OUTPUT_COMMANDS = false;

    private static final String QUIT_COMMAND_LOWER = "quit";

    private final Scanner scanner;
    private final Server server;

    public ServerManager(Scanner scanner, int maxClients, long timeout) throws IOException {
        this.scanner = scanner;

        server = new Server(maxClients, timeout);
    }

    public void launch() {
        System.out.println("Server launched on port=" + DEFAULT_PORT);
        System.out.println("Waiting for connections...");

        try {
            server.launch();
        } catch (IllegalStateException ex) {
            System.out.println(ex.getMessage());
            return;
        } catch (Exception ex) {
            System.out.println("Poo-pi-poop. Something went wrong.");
            return;
        }

        System.out.println("To stop server use command: \"quit\"");
        if (handleConsoleCommands()) {
            try {
                server.close();
            } catch (Exception ex) {
                System.out.println("On closing server some errors appears. Server will shut down automatically.");
            }
        }
    }

    private boolean handleConsoleCommands() {
        try {
            while (scanner.hasNext()) {
                String input = scanner.next();
                if (QUIT_COMMAND_LOWER.equals(input.toLowerCase(Locale.ROOT))) {
                    return true;
                }
            }
        } catch (IllegalStateException ex) {
            System.out.println("Console input handling error. To stop server close application.");
        }

        return false;
    }
}
