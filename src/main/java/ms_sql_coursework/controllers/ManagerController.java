package ms_sql_coursework.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import ms_sql_coursework.MS_SQL_App;
import ms_sql_coursework.services.AuthService;
import ms_sql_coursework.services.ParamService;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static ms_sql_coursework.model.Constants.*;

public class ManagerController implements Controller {

    Connection connection;
    MS_SQL_App msSqlApp;
    AuthService authService;
    ParamService paramService;
    String staffId;

    @FXML
    ComboBox<String> comboBox;
    @FXML
    Label role;
    @FXML
    Label name;
    @FXML
    Label surname;
    @FXML
    Label email;
    @FXML
    Label birthdate;
    @FXML
    Label phoneNumber;
    @FXML
    Button editButton;

    public ManagerController(Connection conn, MS_SQL_App app) throws Exception {
        connection = conn;
        msSqlApp = app;
        authService = new AuthService(conn);
        paramService = new ParamService(conn);

    }
    @Override
    public void onClickFunc(ActionEvent actionEvent) {
        String name = comboBox.getSelectionModel().getSelectedItem();
        String funcName = managerMap.get(name);
        if (withoutParams.contains(funcName)) {
            ResultSet resultSet = paramService.chooseWithoutParamFunc(funcName, staffId);
            msSqlApp.showResultWindow(name, resultSet);
        } else {
            msSqlApp.showParamsWindow(name, funcName, staffId, null);
        }
    }

    @Override
    public void fillButtons(String email) {
        comboBox.getItems().clear();
        for (var i: managerMap.keySet().stream().sorted().toList()) {
            comboBox.getItems().add(i);
        }
        Integer stId = authService.getStaffIdByEmail(email);
        staffId = stId.toString();
        ResultSet res = paramService.outputStaffMyInfo(stId);
        try {
            if (res != null && res.next()) {
                this.email.setText(res.getString(1));
                name.setText(res.getString(3));
                surname.setText(res.getString(4));
                birthdate.setText(res.getString(5));
                phoneNumber.setText(res.getString(6));
                role.setText(authService.getRoleByEmail(email, res.getString(2)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    @Override
    public void onClickEdit() {
        msSqlApp.showParamsWindow("редактировать свои данные", "changeMyInfo", staffId, null);
    }

    @Override
    public void exit() {
        msSqlApp.showBaseWindow();
    }

    @Override
    public void updateMainWindow(Integer staffId) {
        ResultSet res = paramService.outputStaffMyInfo(staffId);
        try {
            if (res != null && res.next()) {
                this.email.setText(res.getString(1));
                name.setText(res.getString(3));
                surname.setText(res.getString(4));
                birthdate.setText(res.getString(5));
                phoneNumber.setText(res.getString(6));
                role.setText(authService.getRoleByEmail(email.getText(), res.getString(2)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
