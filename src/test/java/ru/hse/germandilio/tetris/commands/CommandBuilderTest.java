package ru.hse.germandilio.tetris.commands;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandBuilderTest {

    @Test
    void build() {
        String command = "NAME";
        var arg = "kpo";
        String stringCommand = new CommandBuilder().build(1, command, arg);

        String expected = "NAME kpo";

        assertEquals(expected, stringCommand);
    }
}