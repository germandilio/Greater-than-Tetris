package ru.hse.germandilio.tetris.server;

import java.util.Scanner;

public class ConsoleHandler implements InputHandler {
    private static final String CLIENTS_NUMBER_PROMT = "Введите кол-во клиентов";
    private static final String TIMEOUT_PROMT = "Введите максимальное время сессии (в секундах)";

    private final Scanner scanner;

    public ConsoleHandler(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public int getClientsNumber() {
        int amount = -1;
        System.out.println(CLIENTS_NUMBER_PROMT);

        while (amount < 1 || amount > 2) {
            if (scanner.hasNextInt()) {
                amount = scanner.nextInt();
            }
            else if (scanner.hasNext()){
                scanner.next();
                amount = -1;
                System.out.println(CLIENTS_NUMBER_PROMT);
            } else {
                break;
            }
        }

        return amount;    }

    @Override
    public long getTimeout() {
        long timeout = -1;
        System.out.println(TIMEOUT_PROMT);

        while (timeout < 0) {
            if (scanner.hasNextLong()) {
                timeout = scanner.nextLong();
            }
            else if (scanner.hasNext()){
                scanner.next();
                timeout = -1;
                System.out.println(TIMEOUT_PROMT);
            } else {
                break;
            }
        }

        return timeout;
    }
}
