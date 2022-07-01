package ru.hse.germandilio.tetris.commands;

import org.junit.jupiter.api.Test;
import ru.hse.germandilio.tetris.shared.commands.CommandBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommandBuilderTest {

    @Test
    void build() {
        String command = "STARTING_GAME";
        var arg = "greenkrug";
        String stringCommand = new CommandBuilder().build(1, command, arg);

        String expected = "STARTING_GAME greenkrug";

        assertEquals(expected, stringCommand);
    }
}