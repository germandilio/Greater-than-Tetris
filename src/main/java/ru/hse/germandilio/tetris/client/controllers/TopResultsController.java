package ru.hse.germandilio.tetris.client.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.hse.germandilio.tetris.client.model.ViewGameResult;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class TopResultsController {
    @FXML
    private TableView<ViewGameResult> gameResults;

    @FXML
    public TableColumn<ViewGameResult, String> login;

    @FXML
    public TableColumn<ViewGameResult, LocalDateTime> endSessionTime;

    @FXML
    public TableColumn<ViewGameResult, Integer> movesCount;

    @FXML
    public TableColumn<ViewGameResult, LocalTime> sessionDuration;

    private final ObservableList<ViewGameResult> resultSet = FXCollections.observableArrayList();

    public void setResultsSet(List<ViewGameResult> results) {
        resultSet.clear();
        resultSet.addAll(results);

        // create table
        setCellsValues();
    }

    private void setCellsValues() {
        login.setCellValueFactory(new PropertyValueFactory<>("login"));
        endSessionTime.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        movesCount.setCellValueFactory(new PropertyValueFactory<>("movesCount"));
        sessionDuration.setCellValueFactory(new PropertyValueFactory<>("gameDuration"));

        gameResults.setItems(resultSet);
    }
}
