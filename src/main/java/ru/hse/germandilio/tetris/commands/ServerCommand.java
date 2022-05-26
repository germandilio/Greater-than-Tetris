package ru.hse.germandilio.tetris.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum ServerCommand {
    CONNECTED(1),

    WAITING_START_GAME(0),
    WAITING_END_GAME(0),

    START_GAME(1),
    END_GAME(5),

    START_GAME_SINGLE(0),
    END_GAME_SINGLE(0),

    NEXT_BRICK(1);

    private final int argumentsCount;

    ServerCommand(int argumentsCount) {
        this.argumentsCount = argumentsCount;
    }

    public static ServerCommand getCommandType(String stringCommand) {
        return switch (stringCommand) {
            case "CONNECTED" -> CONNECTED;
            case "WAITING_START_GAME" -> WAITING_START_GAME;
            case "WAITING_END_GAME" -> WAITING_END_GAME;
            case "START_GAME" -> START_GAME;
            case "END_GAME" -> END_GAME;
            case "START_GAME_SINGLE" -> START_GAME_SINGLE;
            case "END_GAME_SINGLE" -> END_GAME_SINGLE;
            case "NEXT_BRICK" -> NEXT_BRICK;
            default -> throw new IllegalArgumentException("Unknown command type.");
        };
    }

    public static List<String> getArguments(ServerCommand command, String input) {
        var param = input.split(" ");

        if (param.length - 1 != command.argumentsCount) {
            throw new IllegalArgumentException("Number of parameters is not compatible with command type.");
        }

        if (param.length == 0) {
            return new ArrayList<>();
        }
        return Arrays.stream(param).skip(1).toList();
    }

    public static String buildCommand(ServerCommand command, String... parameters) {
        return new CommandBuilder().build(command.argumentsCount, command.toString(), parameters);
    }
}
