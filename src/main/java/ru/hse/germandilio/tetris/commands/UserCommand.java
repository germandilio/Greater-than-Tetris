package ru.hse.germandilio.tetris.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum UserCommand {
    NAME(1),
    GET_NEXT_BRICK(0),

    END_GAME(1),
    QUIT(0);

    private final int argumentsCount;

    UserCommand(int argumentsCount) {
        this.argumentsCount = argumentsCount;
    }

    public static UserCommand getCommandType(String stringCommand) {
        return switch (stringCommand) {
            case "NAME" -> NAME;
            case "GET_NEXT_BRICK" -> GET_NEXT_BRICK;
            case "END_GAME" -> END_GAME;
            case "QUIT" -> QUIT;
            default -> throw new IllegalArgumentException("Unknown command type.");
        };
    }

    public static List<String> getArguments(UserCommand command, String input) {
        var param = input.split(" ");

        if (param.length - 1 != command.argumentsCount) {
            throw new IllegalArgumentException("Number of parameters is not compatible with command type.");
        }

        if (param.length == 0) {
            return new ArrayList<>();
        }
        return Arrays.stream(param).skip(1).toList();
    }

    public static String buildCommand(UserCommand command, String... parameters) {
        return new CommandBuilder().build(command.argumentsCount, command.toString(), parameters);
    }
}
