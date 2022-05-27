package ru.hse.germandilio.tetris.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum CommandsAPI {
    // !----------------------- CLIENT -> SERVER -----------------------!
    /**
     * Send when client wants to start new game with "name" argument.
     *
     * @apiNote CLIENT -> SERVER
     * @implNote Arguments: {@code String} name.
     */
    STARTING_GAME(1),
    /**
     * Prompt for new brick.
     *
     * @apiNote CLIENT -> SERVER
     * @implNote Arguments: {@code Integer} index of next brick.
     */
    GET_NEXT_BRICK(1),
    /**
     * Client ending game.
     *
     * @apiNote CLIENT -> SERVER
     * @implNote Arguments: {@code Integer} brick placed, {@code Long} game session duration (in seconds).
     */
    LEAVE_GAME(2),
    /**
     * Client close application.
     *
     * @apiNote CLIENT -> SERVER
     * @implNote No arguments.
     */
    CLIENT_DISCONNECTED(0),
    /**
     * Get TOP (param) game sessions results.
     *
     * @apiNote CLIENT -> SERVER
     * @implNote Arguments: {@code Integer} number for TOP (ex. TOP 10 - param = 10).
     */
    GET_TOP(1),


    // !----------------------- SERVER -> CLIENT -----------------------!

    /**
     * Client has been successfully registered on server.
     *
     * @apiNote SERVER -> CLIENT
     * @implNote No arguments.
     * */
    CONNECTED(0),
    /**
     * Waiting for new game start.
     *
     * @apiNote SERVER -> CLIENT
     * @implNote No arguments.
     */
    WAITING_FOR_NEW_GAME(0),
    /**
     * Starts game sessions on clients.
     *
     * @apiNote SERVER -> CLIENT
     * @implNote Arguments: {@code String} opponent name, {@code Long} max game session time.
     */
    START_GAME(2),
    /**
     * Starts single game sessions on client.
     *
     * @apiNote SERVER -> CLIENT
     * @implNote Arguments: {@code Long} max game session time.
     */
    START_GAME_SINGLE(1),
    /**
     * Passing new generated brick as parameter.
     *
     * @apiNote SERVER -> CLIENT
     * @implNote Arguments: {@code String} brick matrix.
     */
    NEXT_BRICK(1),
    /**
     * Waiting for opponent end game session.
     *
     * @apiNote SERVER -> CLIENT
     * @implNote No arguments.
     */
    WAITING_FOR_END_GAME(0),
    /**
     * End game on clients.
     *
     * @apiNote SERVER -> CLIENT
     * @implNote Arguments: {@code String} opponent name, {@code Long} opponent time, {@code Integer} opponent bricks placed,
     * {@code String} winnerName.
     */
    END_GAME(4),
    /**
     * End game on client, because opponent leaved game.
     *
     * @apiNote SERVER -> CLIENT
     * @implNote No arguments.
     */
    END_GAME_OPPONENT_LEAVE(0),
    /**
     * Server is shut down. All clients should stop crash the games sessions.
     *
     * @apiNote SERVER -> CLIENT
     * @implNote No arguments.
     */
    SERVER_DISCONNECTED(0),
    /**
     * Results of top 10 game sessions.
     *
     * @apiNote SERVER -> CLIENT
     * @implNote Arguments: {@code String} number of game sessions, ... parameters by each game session in format:
     * {@code String} opponent name, {@code DataTime}  end session date and time, {@code Integer} moves count,
     * {@code Long} session time in seconds.
     */
    TOP_10(1);


    private final int argumentsCount;

    CommandsAPI(int argumentsCount) {
        this.argumentsCount = argumentsCount;
    }

    // !----------------------- UTILITIES -----------------------!

    /**
     * Get command type by {@code String} representation of type.
     *
     * @param stringCommand {@code String} representation
     * @return {@code CommandsAPI}
     */
    public static CommandsAPI getCommandType(String stringCommand) throws IOException {
        return switch (stringCommand) {
            case "STARTING_GAME" -> STARTING_GAME;
            case "GET_NEXT_BRICK" -> GET_NEXT_BRICK;
            case "LEAVE_GAME" -> LEAVE_GAME;
            case "CLIENT_DISCONNECTED" -> CLIENT_DISCONNECTED;
            case "GET_TOP_10" -> GET_TOP;
            case "CONNECTED" -> CONNECTED;
            case "WAITING_FOR_NEW_GAME" -> WAITING_FOR_NEW_GAME;
            case "START_GAME" -> START_GAME;
            case "START_GAME_SINGLE" -> START_GAME_SINGLE;
            case "NEXT_BRICK" -> NEXT_BRICK;
            case "WAITING_FOR_END_GAME" -> WAITING_FOR_END_GAME;
            case "END_GAME" -> END_GAME;
            case "END_GAME_OPPONENT_LEAVE" -> END_GAME_OPPONENT_LEAVE;
            case "SERVER_DISCONNECTED" -> SERVER_DISCONNECTED;
            case "TOP_10" -> TOP_10;
            case "" -> throw new IOException();
            default -> throw new IllegalArgumentException("Unknown command type.");
        };
    }

    /**
     * Retrieves arguments from commands based on their type (internal number of arguments).
     *
     * @param command {@code Command} command type.
     * @param input   String representation of command with arguments.
     *                IMPORTANT: Input string SHOULD contain command name.
     * @return List of String represented arguments
     * @throws IllegalArgumentException If number of argument is lower than required by {@code CommandsAPI} type.
     */
    public static List<String> getArguments(CommandsAPI command, String input) {
        var param = input.split(" ");

        if (param.length - 1 < command.argumentsCount) {
            throw new IllegalArgumentException("Number of parameters is not compatible with command type. (should be greater than "
                    + command.argumentsCount + ").");
        }

        if (param.length == 0) {
            return new ArrayList<>();
        }
        return Arrays.stream(param).skip(1).toList();
    }

    /**
     * Build command by provided type and {@code String} represented arguments.
     *
     * @param command    {@code CommandsAPI} command type.
     * @param parameters {@code String} represented arguments
     * @return {@code String} mounted command.
     * @throws IllegalArgumentException if number of provided arguments mismatching with internal metadata.
     */
    public static String buildCommand(CommandsAPI command, String... parameters) {
        return new CommandBuilder().build(command.argumentsCount, command.toString(), parameters);
    }
}
