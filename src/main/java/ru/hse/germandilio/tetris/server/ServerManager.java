package ru.hse.germandilio.tetris.server;

import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;

public class ServerManager {
    private static final String QUIT_COMMAND_LOWER = "quit";
    private final Scanner scanner;
    private Server server;

    public ServerManager(Scanner scanner, int maxClients, long timeout) throws IOException {
        this.scanner = scanner;

        server = new Server(maxClients, timeout);
    }

    public void launch() {
        System.out.println("Waiting for connections...");

        try {
            server.launch();
        } catch (IOException e) {
            System.out.println("Ошибка ввода вывода");
            System.out.println(e.getMessage());
        } catch(Exception ex) {
            System.out.println("Poo-pi-poop. Something went wrong.");
        }

        System.out.println("Чтобы прервать работу сервера введите \"quit\"");
        handleConsoleCommands();
    }

    private void handleConsoleCommands() {
        while (scanner.hasNext()) {
            String input = scanner.next();
            if (QUIT_COMMAND_LOWER.equals(input.toLowerCase(Locale.ROOT))) {
                break;
            }
        }
    }
}
