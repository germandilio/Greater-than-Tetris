package ru.hse.germandilio.tetris.server.database;

import ru.hse.germandilio.tetris.shared.GameResult;

import java.sql.SQLException;
import java.util.List;

public interface DataProvider {
    void save(GameResult gameResult) throws SQLException;

    List<GameResult> findTop(int topCount) throws SQLException;
}
