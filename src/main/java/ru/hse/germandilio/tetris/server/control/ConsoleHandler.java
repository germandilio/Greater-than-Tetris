package ru.hse.germandilio.tetris.server.control;

import java.util.Scanner;

public class ConsoleHandler implements InputHandler {
    private static final String CLIENTS_NUMBER_PROMPT = "Enter max clients number (1 or 2)";
    private static final String TIMEOUT_PROMPT = "Enter max game session time (in seconds)";

    private final Scanner scanner;

    public ConsoleHandler(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public int getClientsNumber() {
        int amount = -1;
        System.out.println(CLIENTS_NUMBER_PROMPT);

        while (amount < 1 || amount > 2) {
            if (scanner.hasNextInt()) {
                amount = scanner.nextInt();
            }
            else if (scanner.hasNext()){
                scanner.next();
                amount = -1;
                System.out.println(CLIENTS_NUMBER_PROMPT);
            } else {
                break;
            }
        }

        return amount;    }

    @Override
    public long getTimeout() {
        long timeout = -1;
        System.out.println(TIMEOUT_PROMPT);

        while (timeout < 0) {
            if (scanner.hasNextLong()) {
                timeout = scanner.nextLong();
            }
            else if (scanner.hasNext()){
                scanner.next();
                timeout = -1;
                System.out.println(TIMEOUT_PROMPT);
            } else {
                break;
            }
        }

        return timeout;
    }
}
