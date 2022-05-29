package ru.hse.germandilio.tetris.client.model;

public class ViewGameResult {
    private final String login;
    private final String endTime;
    private final String movesCount;
    private final String gameDuration;

    public ViewGameResult(String login, String endTime, String movesCount, String gameDuration) {
        this.login = login;
        this.endTime = endTime;
        this.movesCount = movesCount;
        this.gameDuration = gameDuration;
    }

    public String getLogin() {
        return login;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getMovesCount() {
        return movesCount;
    }

    public String getGameDuration() {
        return gameDuration;
    }
}
