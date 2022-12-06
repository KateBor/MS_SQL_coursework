package ms_sql_coursework.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import lombok.Data;
import ms_sql_coursework.MS_SQL_App;
import ms_sql_coursework.services.AuthService;
import ms_sql_coursework.services.ParamService;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

@Data
public class StaffController {
    MS_SQL_App msSqlApp;
    AuthService authService;
    ParamService paramService;
    Integer staffId;

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
    @FXML
    Label password;

    public StaffController(Connection connection, MS_SQL_App msSqlApp) throws Exception {
        authService = new AuthService(connection);
        paramService = new ParamService(connection);
        this.msSqlApp = msSqlApp;
    }

    public void setStaffInfo(ResultSet res, Integer staffId) {
        this.staffId = staffId;
        try {
            if (res != null && res.next()) {
                email.setText(res.getString(1));
                name.setText(res.getString(3));
                surname.setText(res.getString(4));
                birthdate.setText(res.getString(5));
                phoneNumber.setText(res.getString(6));
                role.setText(authService.getRoleByEmail(email.getText(), res.getString(2)));
                password.setText(authService.decrypt(res.getString(2)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void editStaffInfo() {
        msSqlApp.showParamsWindow("редактировать данные сотрудника", "changeStaffInfo", staffId.toString());
    }

    public void updateStaffWindow(Integer staffId) {
        ResultSet res = paramService.outputStaffMyInfo(staffId);
        try {
            if (res != null && res.next()) {
                email.setText(res.getString(1));
                name.setText(res.getString(3));
                surname.setText(res.getString(4));
                birthdate.setText(res.getString(5));
                phoneNumber.setText(res.getString(6));
                role.setText(authService.getRoleByEmail(email.getText(), res.getString(2)));
                password.setText(authService.decrypt(res.getString(2)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
