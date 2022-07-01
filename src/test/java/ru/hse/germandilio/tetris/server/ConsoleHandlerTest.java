package ru.hse.germandilio.tetris.server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.hse.germandilio.tetris.server.control.ConsoleHandler;

import java.util.Scanner;

class ConsoleHandlerTest {

    @Test
    void getClientsNumber() {
        Scanner scanner = new Scanner("1");
        ConsoleHandler console = new ConsoleHandler(scanner);

        Assertions.assertEquals(1, console.getClientsNumber());
    }

    @Test
    void getClientsNumber_RepeatedInput() {
        Scanner scanner = new Scanner("   \n4\n2");
        ConsoleHandler console = new ConsoleHandler(scanner);

        Assertions.assertEquals(2, console.getClientsNumber());
    }


    @Test
    void getTimeout() {
        Scanner scanner = new Scanner("100");
        ConsoleHandler console = new ConsoleHandler(scanner);

        Assertions.assertEquals(100, console.getTimeout());
    }

    @Test
    void getTimeout_RepeatedInput() {
        Scanner scanner = new Scanner("  gds\n10");
        ConsoleHandler console = new ConsoleHandler(scanner);

        Assertions.assertEquals(10, console.getTimeout());
    }
}