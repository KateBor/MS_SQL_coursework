package ms_sql_coursework.controllers;

import javafx.event.ActionEvent;

public interface Controller {
    void onClickFunc(ActionEvent actionEvent);
    void fillButtons(String email);
    void onClickEdit();
    void exit();

    void updateMainWindow(Integer staffId);
}
