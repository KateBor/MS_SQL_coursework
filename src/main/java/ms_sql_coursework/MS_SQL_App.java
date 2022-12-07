package ms_sql_coursework;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.Data;
import ms_sql_coursework.controllers.*;
import ms_sql_coursework.model.ParamRequest;
import ms_sql_coursework.model.RolesContainer;
import ms_sql_coursework.services.AuthService;

import java.io.IOException;
import java.sql.*;

@Data
public class MS_SQL_App extends Application {
    private Stage primaryStage;
    private AnchorPane rootLayout;
    static Connection conn;
    private Controller contr;

    public static void main(String[] args) {

        String url = "jdbc:sqlserver://DESKTOP-LR51BMB:1433;databaseName=movieStudio;integratedSecurity=true;encrypt=true;trustServerCertificate=true";
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(url);
            System.out.println("Success");

            launch(args);

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        showBaseWindow();
    }

    public void showBaseWindow() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MS_SQL_App.class.getResource("/pages/Login.fxml"));
            rootLayout = loader.load();
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

            LoginController controller = loader.getController();
            controller.setMsSqlApp(this);
            controller.setRolesContainer(new RolesContainer(conn, this));
            controller.setAuthService(new AuthService(conn));

            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showMainWindow(Controller controller, String email) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MS_SQL_App.class.getResource("/pages/Main.fxml"));
        loader.setController(controller);
        contr = controller;
        try {
            AnchorPane page = loader.load();
            Scene scene = new Scene(page);
            primaryStage.setScene(scene);

        } catch (IOException e) {
            e.printStackTrace();
        }
        primaryStage.setTitle("Main");
        primaryStage.show();
        controller.fillButtons(email);
    }

    public void showParamsWindow(String name, String funcName, String staffId, ParamRequest oldInfo) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MS_SQL_App.class.getResource("/functionPages/Params.fxml"));
        try {
            ParamsController controller = new ParamsController(conn, this, staffId, contr);
            loader.setController(controller);
            AnchorPane page = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle(name);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            dialogStage.show();
            controller.chooseMethod(funcName, null, oldInfo);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showSuccessWindow() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MS_SQL_App.class.getResource("/functionPages/Success.fxml"));
        try {
            AnchorPane page = loader.load();
            Stage dialogStage = new Stage();
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            dialogStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showExceptionWindow(String message) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MS_SQL_App.class.getResource("/functionPages/Exception.fxml"));
        try {
            AnchorPane page = loader.load();
            ExceptionController controller = loader.getController();
            Stage dialogStage = new Stage();
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            dialogStage.show();
            controller.setMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showResultWindow(String name, ResultSet resultSet) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MS_SQL_App.class.getResource("/functionPages/Result.fxml"));
        try {
            AnchorPane page = loader.load();
            ResultController controller = loader.getController();
            Stage dialogStage = new Stage();
            dialogStage.setTitle(name);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            dialogStage.initOwner(primaryStage);
            dialogStage.show();
            controller.fillTable(resultSet, name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showStaffInfoWindow(ResultSet answer, Integer staffId) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MS_SQL_App.class.getResource("/functionPages/staffInfo.fxml"));
        try {
            StaffController controller = new StaffController(conn, this);
            loader.setController(controller);

            AnchorPane page = loader.load();
            Stage dialogStage = new Stage();
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            dialogStage.show();
            controller.setStaffInfo(answer, staffId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
