package ms_sql_coursework.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lombok.Data;
import lombok.Setter;
import ms_sql_coursework.MS_SQL_App;
import ms_sql_coursework.model.RolesContainer;
import ms_sql_coursework.services.AuthService;

@Data
@Setter
public class LoginController {
    @FXML
    public TextField email;
    @FXML
    public TextField password;
    @FXML
    public Label error;

    AuthService authService;
    MS_SQL_App msSqlApp;
    RolesContainer rolesContainer;

    @FXML
    public void signIn() {
        String hash = authService.encrypt(password.getText());
        String role = authService.getRoleByEmail(email.getText(), hash);
        if (role != null) {
            msSqlApp.showMainWindow(rolesContainer.getController(role), email.getText());
        } else {
            error.setText("Неправильная почта или пароль, попробуйте снова");
        }
    }

}
