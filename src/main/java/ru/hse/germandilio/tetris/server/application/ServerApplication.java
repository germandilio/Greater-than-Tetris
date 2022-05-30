package ru.hse.germandilio.tetris.server.application;

import ru.hse.germandilio.tetris.server.control.ConsoleHandler;
import ru.hse.germandilio.tetris.server.control.InputHandler;
import ru.hse.germandilio.tetris.server.control.ServerManager;

import java.io.IOException;
import java.util.Scanner;

public class ServerApplication {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            InputHandler inputHandler = new ConsoleHandler(scanner);

            int count = inputHandler.getClientsNumber();
            long timeout = inputHandler.getTimeout();

            try {
                ServerManager manager = new ServerManager(scanner, count, timeout);
                manager.launch();

            } catch (IOException e) {
                System.out.println(e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Wrong server configuration. Max clients should be 1 or 2" +
                        " and game duration greater than zero");
                System.out.println(e.getMessage());
            } catch (Exception ex) {
                System.out.println("Poo-pi-poop. Something went wrong.");
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
