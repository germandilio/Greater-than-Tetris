package ru.hse.germandilio.tetris.shared.commands;

public class CommandBuilder {
    public String build(int argCount, String command, String... arguments) {
        if (argCount > arguments.length) {
            throw new IllegalArgumentException("Number of parameters is not compatible with command type.");
        }

        StringBuilder sb = new StringBuilder();

        sb.append(command);
        for (String parameter : arguments) {
            sb.append(' ');
            sb.append(parameter);
        }
        return sb.toString();
    }
}
