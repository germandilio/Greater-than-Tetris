package ru.hse.germandilio.tetris.server;

import java.io.IOException;
import java.util.Scanner;

public class ServerApplication {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        InputHandler inputHandler = new ConsoleHandler(scanner);

        int count = inputHandler.getClientsNumber();
        long timeout = inputHandler.getTimeout();

        try {
            ServerManager manager = new ServerManager(scanner, count, timeout);
            manager.launch();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            System.out.println("Poo-pi-poop. Something went wrong.");
        }
    }
}
