package ms_sql_coursework.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.Data;

@Data
public class ExceptionController {
    @FXML
    Label message;

    public void setMessage(String mes) {
        message.setText(mes);
    }
}
