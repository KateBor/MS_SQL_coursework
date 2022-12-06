package ms_sql_coursework.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lombok.Data;
import ms_sql_coursework.MS_SQL_App;
import ms_sql_coursework.model.ParamRequest;
import ms_sql_coursework.services.AuthService;
import ms_sql_coursework.services.ParamService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static ms_sql_coursework.model.Constants.*;

@Data
public class ParamsController {
    Connection connection;
    MS_SQL_App msSqlApp;
    ParamService paramService;
    AuthService authService;
    Integer staffId;
    Controller controller;
    StaffController staffController;

    @FXML
    public Label label1;
    @FXML
    public Label label2;
    @FXML
    public Label label3;
    @FXML
    public Label label4;
    @FXML
    public Label label5;
    @FXML
    public Label label6;
    @FXML
    public Label label7;
    @FXML
    public Label label8;
    @FXML
    public TextField text1;
    @FXML
    public TextField text2;
    @FXML
    public TextField text3;
    @FXML
    public TextField text4;
    @FXML
    public TextField text5;
    @FXML
    public TextField text6;
    @FXML
    public TextField text7;
    @FXML
    public TextField text8;
    @FXML
    public ComboBox<String> box1;
    @FXML
    public ComboBox<String> box2;
    @FXML
    public ComboBox<String> box3;
    @FXML
    public ComboBox<String> box4;
    @FXML
    public ComboBox<String> box5;
    @FXML
    public ComboBox<String> box6;
    @FXML
    public ComboBox<String> box7;
    @FXML
    public ComboBox<String> box8;
    @FXML
    public Button submitButton;

    private String funcName;

    public ParamsController(Connection conn, MS_SQL_App msSqlApp, String staffId, Controller controller) throws Exception {
        connection = conn;
        this.msSqlApp = msSqlApp;
        paramService = new ParamService(conn);
        funcName = null;
        this.staffId = Integer.parseInt(staffId);
        this.controller = controller;
        authService = new AuthService(conn);
        staffController = new StaffController(conn, msSqlApp);
    }

    public void chooseMethod(String funcName, ParamRequest request) throws SQLException { //47 методов
        this.funcName = funcName;
        switch (funcName) {
            //admin
            case "EXEC add_user ?,?,?,?,?,?,?" -> addUser(request);

            case "EXEC add_worker_position ?,?,?" -> addWorkerPosition(request);
            case "EXEC add_employment ?,?,?,?" -> addEmployment(request);
            case "EXEC change_fee ?,?" -> changeFee(request);
            case "EXEC add_job ?,?" -> addJob(request);

            case "EXEC change_job_name ?,?" -> changeJobName(request);
            case "EXEC add_department ?,?" -> addDepartment(request);
            case "EXEC change_department_name ?,?" -> changeDepartmentName(request);
            case "EXEC output_staff_by_department ?" -> outputStaffByDepartment(request); //?
            case "EXEC change_staff_status ?" -> changeStaffStatus(request);

            case "EXEC output_staff_history ?" -> outputStaffHistory(request);
            case "EXEC add_movie ?,?,?,?" -> addMovie(request);

            case "EXEC add_location ?,?,?" -> addLocation(request);
            case "EXEC Output_movies_history ?" -> outputMoviesHistory(request);
            case "EXEC output_movies_staff ?" -> outputMoviesStaff(request);

            case "EXEC Output_staff_info ?" -> outputStaffInfo(request);

            //manager
            case "EXEC add_filming ?,?,?,?" -> addFilming(request);

            case "EXEC Output_staff_jobs_by_movie ?,?" -> outputStaffJobsByMovie(request);
            case "EXEC Output_movies_history_manager ?" -> outputMoviesHistoryManager(request);
            case "EXEC Output_staff_by_movie ?,?" -> outputStaffByMovie(request);
            case "EXEC add_staff_filming ?,?,?" -> addStaffFilming(request);
            case "EXEC Output_filmings_by_manager ?,?" -> outputFilmingsByManager(request);
            case "EXEC Output_staff_filmings_by_movie ?,?" -> outputStaffFilmingsByMovie(request);

            case "changeMyInfo" -> changeMyInfo(request);
            case "changeMovieInfo" -> changeMovieInfo(request);
            case "changeStaffInfo" -> changeStaffInfo(request);
            case "changeFilmingInfo" -> changeFilmingInfo(request);
        }
    }

    @FXML
    public void sendParams() throws SQLException {
        ParamRequest request = new ParamRequest();
        request.setFuncName(funcName);
        if (!text1.getText().equals("") && !text1.getText().equals(NULL_FORMAT)) {
            request.setParam1(text1.getText());
        }
        if (!text2.getText().equals("") && !text2.getText().equals(NULL_FORMAT)) {
            request.setParam2(text2.getText());
        }
        if (!text3.getText().equals("") && !text3.getText().equals(NULL_FORMAT)) {
            request.setParam3(text3.getText());
        }
        if (!text4.getText().equals("") && !text4.getText().equals(NULL_FORMAT)) {
            request.setParam4(text4.getText());
        }
        if (!text5.getText().equals("") && !text5.getText().equals(NULL_FORMAT)) {
            request.setParam5(text5.getText());
        }
        if (!text6.getText().equals("") && !text6.getText().equals(NULL_FORMAT)) {
            request.setParam6(text6.getText());
        }
        if (!text7.getText().equals("") && !text7.getText().equals(NULL_FORMAT)) {
            request.setParam7(text7.getText());
        }
        if (!text8.getText().equals("") && !text8.getText().equals(NULL_FORMAT)) {
            request.setParam8(text8.getText());
        }
        chooseMethod(funcName, request);
    }

    public void addUser(ParamRequest request) {
        if (request != null) { //отправка данных и получение результата
            String answer = paramService.addUser(request);
            if (answer.equals(SUCCESS)) {
                MS_SQL_App.showSuccessWindow();
            } else {
                MS_SQL_App.showExceptionWindow(answer);
            }
        } else { //заполнение полей
            text1.setVisible(true);
            text2.setVisible(true);
            text3.setVisible(true);
            text4.setVisible(true);
            text5.setVisible(true);
            text6.setVisible(true);
            text7.setVisible(true);
            text7.setText(PHONE_FORMAT);
            label1.setText("Роль"); //список
            label2.setText("Почта");
            label3.setText("Пароль");
            label4.setText("Имя");
            label5.setText("Фамилия");
            label6.setText("Дата рождения");
            label7.setText("Номер телефона");

            box1.setVisible(true);
            text1.setEditable(false);
            box1.getItems().clear();
            box1.getItems().addAll("Менеджер съёмок", "Сотрудник");
            box1.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> text1.setText(newValue));
        }
    }

    private void addWorkerPosition(ParamRequest request) throws SQLException {
        if (request != null) { //отправка данных и получение результата
            String answer = paramService.addWorkerPosition(request);
            if (answer.equals(SUCCESS)) {
                MS_SQL_App.showSuccessWindow();
            } else {
                MS_SQL_App.showExceptionWindow(answer);
            }
        } else { //заполнение полей
            text1.setVisible(true);
            text2.setVisible(true);
            text3.setVisible(true);
            label1.setText("Сотрудник"); //список
            label2.setText("Отдел"); //список
            label3.setText("Должность"); //список
            setStaff();
            setDepartments(2);
            setJobs(3);
        }
    }

    private void addEmployment(ParamRequest request) throws SQLException {
        if (request != null) { //отправка данных и получение результата
            String answer = paramService.addEmployment(request);
            if (answer.equals(SUCCESS)) {
                MS_SQL_App.showSuccessWindow();
            } else {
                MS_SQL_App.showExceptionWindow(answer);
            }
        } else { //заполнение полей
            text1.setVisible(true);
            text2.setVisible(true);
            text3.setVisible(true);
            label1.setText("Фильм"); //список
            label2.setText("Сотрудник"); //список с должностями
            label3.setText("Гонорар");
            setListMovies();
            setWorkerPositions(2);
        }
    }

    private void changeFee(ParamRequest request) throws SQLException {
        if (request != null) { //отправка данных и получение результата
            String answer = paramService.changeFee(request);
            if (answer.equals(SUCCESS)) {
                MS_SQL_App.showSuccessWindow();
            } else {
                MS_SQL_App.showExceptionWindow(answer);
            }
        } else { //заполнение полей
            text1.setVisible(true);
            text2.setVisible(true);
            text3.setVisible(true);
            label1.setText("Фильм"); //список
            label2.setText("Сотрудник"); //список c должностями
            label3.setText("Гонорар");
            setListMovies();
            setWorkerPositions(2);
            box1.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                text1.setText(newValue);
                try {
                    setWorkerPositionsByMovie(2, newValue);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void addJob(ParamRequest request) {
        if (request != null) { //отправка данных и получение результата
            String answer = paramService.addJob(request);
            if (answer.equals(SUCCESS)) {
                MS_SQL_App.showSuccessWindow();
            } else {
                MS_SQL_App.showExceptionWindow(answer);
            }
        } else { //заполнение полей
            text1.setVisible(true);
            text2.setVisible(true);
            label1.setText("Название должности");
            label2.setText("Описание");
            text2.setText(NULL_FORMAT);
        }
    }

    private void changeJobName(ParamRequest request) throws SQLException {
        if (request != null) { //отправка данных и получение результата
            String answer = paramService.changeJobName(request);
            if (answer.equals(SUCCESS)) {
                MS_SQL_App.showSuccessWindow();
            } else {
                MS_SQL_App.showExceptionWindow(answer);
            }
        } else { //заполнение полей
            text1.setVisible(true);
            text2.setVisible(true);
            label1.setText("Должность"); //список
            text1.setEditable(false);
            label2.setText("Новое название");
            setJobs(1);

        }
    }

    private void addDepartment(ParamRequest request) {
        if (request != null) { //отправка данных и получение результата
            String answer = paramService.addDepartment(request);
            if (answer.equals(SUCCESS)) {
                MS_SQL_App.showSuccessWindow();
            } else {
                MS_SQL_App.showExceptionWindow(answer);
            }
        } else { //заполнение полей
            text1.setVisible(true);
            text2.setVisible(true);
            label1.setText("Название отдела");
            label2.setText("Описание");
            text2.setText(NULL_FORMAT);
        }
    }

    private void changeDepartmentName(ParamRequest request) throws SQLException {
        if (request != null) { //отправка данных и получение результата
            String answer = paramService.changeDepartmentName(request);
            if (answer.equals(SUCCESS)) {
                MS_SQL_App.showSuccessWindow();
            } else {
                MS_SQL_App.showExceptionWindow(answer);
            }
        } else { //заполнение полей
            text1.setVisible(true);
            text2.setVisible(true);
            label1.setText("Отдел"); //список
            label2.setText("Новое название");
            setDepartments(1);

        }
    }

    private void outputStaffByDepartment(ParamRequest request) throws SQLException {
        if (request != null) { //отправка данных и получение результата
            ResultSet answer = paramService.outputStaffByDepartment(request);
            msSqlApp.showResultWindow(null, answer);
        } else { //заполнение полей
            text1.setVisible(true);
            label1.setText("Отдел"); //список
            setDepartments(1);

        }
    }

    private void changeStaffStatus(ParamRequest request) throws SQLException {
        if (request != null) { //отправка данных и получение результата
            String answer = paramService.changeStaffStatus(request);
            if (answer.equals(SUCCESS)) {
                MS_SQL_App.showSuccessWindow();
            } else {
                MS_SQL_App.showExceptionWindow(answer);
            }
        } else { //заполнение полей
            text1.setVisible(true);
            label1.setText("Сотрудник"); //список c должностями!!
            setWorkerPositions(1);
        }
    }

    private void outputStaffHistory(ParamRequest request) throws SQLException {
        if (request != null) { //отправка данных и получение результата
            ResultSet answer = paramService.outputStaffHistory(request);
            msSqlApp.showResultWindow(null, answer);
        } else { //заполнение полей
            text1.setVisible(true);
            label1.setText("Сотрудник"); //список
            text1.setEditable(false);
            setStaff();
        }
    }

    private void addMovie(ParamRequest request) {
        if (request != null) { //отправка данных и получение результата
            String answer = paramService.addMovie(request);
            if (answer.equals(SUCCESS)) {
                MS_SQL_App.showSuccessWindow();
            } else {
                MS_SQL_App.showExceptionWindow(answer);
            }
        } else { //заполнение полей
            text1.setVisible(true);
            text2.setVisible(true);
            text3.setVisible(true);
            text3.setText(NULL_FORMAT);
            text4.setVisible(true);
            text4.setText(NULL_FORMAT);
            label1.setText("Название");
            label2.setText("Дата премьеры");
            label3.setText("Расходы");
            label4.setText("Доходы");
        }
    }

    private void addLocation(ParamRequest request) {
        if (request != null) { //отправка данных и получение результата
            String answer = paramService.addLocation(request);
            if (answer.equals(SUCCESS)) {
                MS_SQL_App.showSuccessWindow();
            } else {
                MS_SQL_App.showExceptionWindow(answer);
            }
        } else { //заполнение полей
            text1.setVisible(true);
            text2.setVisible(true);
            text3.setVisible(true);
            label1.setText("Страна");
            label2.setText("Город");
            label3.setText("Адрес");
        }
    }

    private void outputMoviesHistory(ParamRequest request) throws SQLException {
        if (request != null) { //отправка данных и получение результата
            ResultSet answer = paramService.outputMoviesHistory(request);
            msSqlApp.showResultWindow(null, answer);
        } else { //заполнение полей
            text1.setVisible(true); //список
            label1.setText("Фильм");
            setListMovies();
        }
    }

    private void outputMoviesStaff(ParamRequest request) throws SQLException {
        if (request != null) { //отправка данных и получение результата
            ResultSet answer = paramService.outputMoviesStaff(request);
            msSqlApp.showResultWindow(null, answer);
        } else { //заполнение полей
            text1.setVisible(true); //список
            label1.setText("Фильм");
            setListMovies();
        }
    }

    private void addFilming(ParamRequest request) throws SQLException {
        if (request != null) { //отправка данных и получение результата
            String answer = paramService.addFilming(request);
            if (answer.equals(SUCCESS)) {
                MS_SQL_App.showSuccessWindow();
            } else {
                MS_SQL_App.showExceptionWindow(answer);
            }
        } else { //заполнение полей
            text1.setVisible(true);
            text2.setVisible(true);
            text3.setVisible(true);
            label1.setText("Фильм"); //список
            label2.setText("Локация");  //список
            label3.setText("Дата");
            box2.getItems().clear();

            setListMoviesByManager();
            setLocations();

            text8.setText(staffId.toString());
        }
    }

    private void outputStaffJobsByMovie(ParamRequest request) throws SQLException {
        if (request != null) { //отправка данных и получение результата
            ResultSet answer = paramService.outputStaffJobsByMovie(request);
            msSqlApp.showResultWindow(null, answer);
        } else { //заполнение полей
            text1.setVisible(true);
            label1.setText("Фильм"); //список
            setListMoviesByManager();
            text8.setText(staffId.toString());
        }
    }

    private void outputMoviesHistoryManager(ParamRequest request) throws SQLException {
        if (request != null) { //отправка данных и получение результата
            request.setFuncName("EXEC Output_movies_history ?");
            ResultSet answer = paramService.outputMoviesHistory(request);
            msSqlApp.showResultWindow(null, answer);
        } else { //заполнение полей
            text1.setVisible(true);
            label1.setText("Фильм"); //список
            setListMoviesByManager();
        }
    }

    private void outputStaffByMovie(ParamRequest request) throws SQLException {
        if (request != null) { //отправка данных и получение результата
            ResultSet answer = paramService.outputStaffByMovie(request);
            msSqlApp.showResultWindow(null, answer);
        } else { //заполнение полей
            text1.setVisible(true);
            label1.setText("Фильм"); //список
            setListMoviesByManager();
            text8.setText(staffId.toString());
        }
    }

    private void addStaffFilming(ParamRequest request) throws SQLException {
        if (request != null) { //отправка данных и получение результата
            String answer = paramService.addStaffFilming(request);
            if (answer.equals(SUCCESS)) {
                MS_SQL_App.showSuccessWindow();
            } else {
                MS_SQL_App.showExceptionWindow(answer);
            }
        } else { //заполнение полей
            text1.setVisible(true);
            text2.setVisible(true);
            label1.setText("Съёмка"); // список фильм + дата
            label2.setText("Сотрудник"); //список c должностями
            setFilmings(1);
            setWorkerPositions(2);
            box1.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                text1.setText(newValue);
                try {
                    String[] parts = newValue.split("; ");
                    setWorkerPositionsByMovie(2, parts[0]);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            text8.setText(staffId.toString());
        }
    }

    private void outputFilmingsByManager(ParamRequest request) throws SQLException {
        if (request != null) { //отправка данных и получение результата
            ResultSet answer = paramService.outputFilmingsByManager(request);
            msSqlApp.showResultWindow(null, answer);
        } else { //заполнение полей
            text1.setVisible(true);
            label1.setText("Фильм"); //список
            setListMoviesByManager();
            text8.setText(staffId.toString());

        }
    }

    private void outputStaffFilmingsByMovie(ParamRequest request) throws SQLException {
        if (request != null) { //отправка данных и получение результата
            ResultSet answer = paramService.outputStaffFilmingsByMovie(request);
            msSqlApp.showResultWindow(null, answer);
        } else { //заполнение полей
            text1.setVisible(true);
            label1.setText("Фильм"); //список
            setListMoviesByManager();
            text8.setText(staffId.toString());
        }
    }

    private void changeMyInfo(ParamRequest request) {
        if (request != null) { //отправка данных и получение результата
            String answer = paramService.changeMyInfo(request);
            if (answer.equals(SUCCESS)) {
                MS_SQL_App.showSuccessWindow();
                controller.updateMainWindow(staffId);
            } else {
                MS_SQL_App.showExceptionWindow(answer);
            }
        } else {
            text1.setVisible(true);
            text2.setVisible(true);
            text3.setVisible(true);
            text4.setVisible(true);
            text5.setVisible(true);
            text5.setText(PHONE_FORMAT);
            label1.setText("Пароль");
            label2.setText("Имя");
            label3.setText("Фамилия");
            label4.setText("Дата рождения");
            label5.setText("Номер телефона");
            text8.setText(staffId.toString());
        }
    }

    private void changeMovieInfo(ParamRequest request) throws SQLException {
        if (request != null) { //отправка данных и получение результата
            String answer = paramService.changeMovieInfo(request);
            if (answer.equals(SUCCESS)) {
                MS_SQL_App.showSuccessWindow();
                controller.updateMainWindow(staffId);
            } else {
                MS_SQL_App.showExceptionWindow(answer);
            }
        } else {
            text1.setVisible(true);
            text2.setVisible(true);
            text3.setVisible(true);
            text4.setVisible(true);
            text5.setVisible(true);
            text6.setVisible(true);
            text7.setVisible(true);
            label1.setText("Фильм"); //список
            label2.setText("Название");
            label3.setText("Дата премьеры");
            label4.setText("Расходы");
            label5.setText("Доходы");
            label6.setText("Новый статус"); //список
            text6.setEditable(false);
            label7.setText("Дата изменения статуса");

            setListMovies();
            box6.getItems().clear();
            box6.setVisible(true);
            box6.getItems().addAll("Начат", "Заморожен", "Разморожен", "Закрыт", "Снят");
            box6.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> text6.setText(newValue));
        }
    }

    private void outputStaffInfo(ParamRequest request) throws SQLException {
        if (request != null) { //отправка данных и получение результата
            String[] parts = request.getParam1().split(", ");
            Integer staffId = authService.getStaffIdByEmail(parts[1]);
            ResultSet answer = paramService.outputStaffMyInfo(staffId);
            msSqlApp.showStaffInfoWindow(answer, staffId);
        } else { //заполнение полей
            text1.setVisible(true); //список
            label1.setText("Сотрудник");
            setStaff();
        }
    }


    private void changeStaffInfo(ParamRequest request) {
        if (request != null) { //отправка данных и получение результата
            String answer = paramService.changeMyInfo(request);
            if (answer.equals(SUCCESS)) {
                MS_SQL_App.showSuccessWindow();
                //staffController.updateStaffWindow(staffId);
            } else {
                MS_SQL_App.showExceptionWindow(answer);
            }
        } else {
            text1.setVisible(true);
            text2.setVisible(true);
            text3.setVisible(true);
            text4.setVisible(true);
            text5.setVisible(true);
            text5.setText(PHONE_FORMAT);
            label1.setText("Пароль");
            label2.setText("Имя");
            label3.setText("Фамилия");
            label4.setText("Дата рождения");
            label5.setText("Номер телефона");
            text8.setText(staffId.toString());
        }
    }

    private void changeFilmingInfo(ParamRequest request) throws SQLException {
        if (request != null) { //отправка данных и получение результата
            String answer = paramService.changeFilmingInfo(request);
            if (answer.equals(SUCCESS)) {
                MS_SQL_App.showSuccessWindow();
                //staffController.updateStaffWindow(staffId);
            } else {
                MS_SQL_App.showExceptionWindow(answer);
            }
        } else {
            text1.setVisible(true);
            text2.setVisible(true);
            text3.setVisible(true);
            text4.setVisible(true); //удалить? нет
            text4.setText("Нет");

            label1.setText("Съёмка"); //список
            label2.setText("Локация"); //cgbcjr
            label3.setText("Дата");
            label4.setText("Отменить съёмку?"); //cgbcjr

            box4.setVisible(true);
            box4.getItems().clear();
            box4.getItems().addAll("Да", "Нет");
            setFilmings(1);
            setLocations();
            text8.setText(staffId.toString());
        }
    }


    //------------------------------------------------------------------------------------------------------------------

    private void setListMoviesByManager() throws SQLException {
        box1.setVisible(true);
        box1.getItems().clear();
        ResultSet resultSet = paramService.chooseWithoutParamFunc("EXEC Output_movies_by_manager ?", staffId.toString());
        if (resultSet != null) {
            while (resultSet.next()) {
                box1.getItems().add(resultSet.getString(1));
            }
            box1.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> text1.setText(newValue));
        }
    }


    private void setListMovies() throws SQLException {
        text1.setEditable(false);
        box1.setVisible(true);
        box1.getItems().clear();
        ResultSet resultSet = paramService.chooseWithoutParamFunc("EXEC get_movies", staffId.toString());
        if (resultSet != null) {
            while (resultSet.next()) {
                box1.getItems().add(resultSet.getString(1));
            }
            box1.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> text1.setText(newValue));
        }
    }

    private void setStaff() throws SQLException {
        text1.setEditable(false);
        box1.setVisible(true);
        box1.getItems().clear();
        ResultSet resultSet = paramService.chooseWithoutParamFunc("EXEC get_staff", staffId.toString());
        if (resultSet != null) {
            while (resultSet.next()) {
                box1.getItems().add(resultSet.getString(1) + " " +
                        resultSet.getString(2) + ", " + resultSet.getString(3));
            }
            box1.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> text1.setText(newValue));
        }
    }

    private void setJobs(int index) throws SQLException {
        ResultSet resultSet = paramService.chooseWithoutParamFunc("EXEC get_jobs", staffId.toString());
        if (index == 2) {
            text2.setEditable(false);
            box2.setVisible(true);
            box2.getItems().clear();
            if (resultSet != null) {
                while (resultSet.next()) {
                    box2.getItems().add(resultSet.getString(1));
                }
                box2.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> text2.setText(newValue));
            }
        } else if (index == 1) {
            text1.setEditable(false);
            box1.setVisible(true);
            box1.getItems().clear();
            if (resultSet != null) {
                while (resultSet.next()) {
                    box1.getItems().add(resultSet.getString(1));
                }
                box1.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> text1.setText(newValue));
            }
        } else if (index == 3) {
            text3.setEditable(false);
            box3.setVisible(true);
            box3.getItems().clear();
            if (resultSet != null) {
                while (resultSet.next()) {
                    box3.getItems().add(resultSet.getString(1));
                }
                box3.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> text3.setText(newValue));
            }
        }

    }


    private void setDepartments(int index) throws SQLException {
        ResultSet resultSet = paramService.chooseWithoutParamFunc("EXEC get_departments", staffId.toString());
        if (index == 2) {
            text2.setEditable(false);
            box2.setVisible(true);
            box2.getItems().clear();
            if (resultSet != null) {
                while (resultSet.next()) {
                    box2.getItems().add(resultSet.getString(1));
                }
                box2.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> text2.setText(newValue));
            }
        } else if (index == 1) {
            text1.setEditable(false);
            box1.setVisible(true);
            box1.getItems().clear();
            if (resultSet != null) {
                while (resultSet.next()) {
                    box1.getItems().add(resultSet.getString(1));
                }
                box1.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> text1.setText(newValue));
            }
        } else if (index == 3) {
            text3.setEditable(false);
            box3.setVisible(true);
            box3.getItems().clear();
            if (resultSet != null) {
                while (resultSet.next()) {
                    box3.getItems().add(resultSet.getString(1));
                }
                box3.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> text3.setText(newValue));
            }
        }
    }

    private void setLocations() throws SQLException {
        ResultSet resultSet = paramService.chooseWithoutParamFunc("EXEC get_locations", staffId.toString());
        text2.setEditable(false);
        box2.setVisible(true);
        box2.getItems().clear();
        if (resultSet != null) {
            while (resultSet.next()) {
                box2.getItems().add(resultSet.getString(1) + ", " +
                        resultSet.getString(2) + ", " + resultSet.getString(3));
            }
            box2.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> text2.setText(newValue));
        }
    }

    private void setWorkerPositions(Integer index) throws SQLException {
        ResultSet resultSet = paramService.chooseWithoutParamFunc("EXEC get_staff_job", staffId.toString());
        if (index == 1) {
            text1.setEditable(false);
            box1.setVisible(true);
            box1.getItems().clear();
            if (resultSet != null) {
                while (resultSet.next()) {
                    box1.getItems().add(resultSet.getString(1) + " " +
                            resultSet.getString(2) + ", " + resultSet.getString(3) + ", " + resultSet.getString(4));
                }
                box1.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> text1.setText(newValue));
            }
        } else if (index == 2) {
            text2.setEditable(false);
            box2.setVisible(true);
            box2.getItems().clear();
            if (resultSet != null) {
                while (resultSet.next()) {
                    box2.getItems().add(resultSet.getString(1) + " " +
                            resultSet.getString(2) + ", " + resultSet.getString(3) + ", " + resultSet.getString(4));
                }
                box2.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> text2.setText(newValue));
            }
        }
    }


    private void setWorkerPositionsByMovie(int index, String title) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("EXEC output_movies_staff ?");
        Integer movieId = paramService.getMovieIdByTitle(title);
        ps.setInt(1, movieId);
        ResultSet resultSet = ps.executeQuery();
        if (index == 1) {
            text1.setEditable(false);
            box1.setVisible(true);
            box1.getItems().clear();
            if (resultSet != null) {
                while (resultSet.next()) {
                    box1.getItems().add(resultSet.getString(2) + " " +
                            resultSet.getString(3) + ", " + resultSet.getString(1) + ", " + resultSet.getString(4));
                }
                box1.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> text1.setText(newValue));
            }
        } else if (index == 2) {
            text2.setEditable(false);
            box2.setVisible(true);
            box2.getItems().clear();
            if (resultSet != null) {
                while (resultSet.next()) {
                    box2.getItems().add(resultSet.getString(2) + " " +
                            resultSet.getString(3) + ", " + resultSet.getString(1) + ", " + resultSet.getString(4));
                }
                box2.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> text2.setText(newValue));
            }
        }
    }

    private void setFilmings(Integer index) throws SQLException {
        ResultSet resultSet = paramService.chooseWithoutParamFunc("EXEC get_filmings_by_manager ?", staffId.toString());
        if (index == 2) {
            text2.setEditable(false);
            box2.setVisible(true);
            box2.getItems().clear();
            if (resultSet != null) {
                while (resultSet.next()) {
                    box2.getItems().add(resultSet.getString(1) + "; " +
                            resultSet.getString(5));
                }
                box2.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> text2.setText(newValue));
            }
        } else if (index == 1) {
            text1.setEditable(false);
            box1.setVisible(true);
            box1.getItems().clear();
            if (resultSet != null) {
                while (resultSet.next()) {
                    box1.getItems().add(resultSet.getString(1) + "; " +
                            resultSet.getString(5));
                }
                box1.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> text1.setText(newValue));
            }
        }
    }
}

