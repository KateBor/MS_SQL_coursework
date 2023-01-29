package ms_sql_coursework.services;

import ms_sql_coursework.model.ParamRequest;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ms_sql_coursework.model.Constants.*;

public class ParamService {
    Connection connection;
    AuthService authService;

    public ParamService(Connection connection) throws Exception {
        this.connection = connection;
        authService = new AuthService(connection);
    }

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }
    public String addUser(ParamRequest request) {
        try {
            if (!validate(request.getParam2())) {
                return "Неправильный формат почты. ";
            }
            PreparedStatement ps = connection.prepareStatement(request.getFuncName());
            ps.setString(1, request.getParam1());
            ps.setString(2, request.getParam2());
            ps.setString(3, authService.encrypt(request.getParam3()));
            ps.setString(4, request.getParam4());
            ps.setString(5, request.getParam5());
            ps.setString(6, request.getParam6());
            ps.setString(7, request.getParam7());
            ps.execute();
            return SUCCESS;
        } catch (SQLException e) {
            return "Вероятно, ошибка в почте, номере телефона или дате рождения";
        }
    }

    public String addWorkerPosition(ParamRequest request) {
        try {
            PreparedStatement ps = connection.prepareStatement(request.getFuncName());
            Integer staffId = authService.getStaffIdByEmail(request.getParam1().split(", ")[1]);
            Integer departmentId = getDepartmentIdByName(request.getParam2());
            Integer jobId = getJobIdByName(request.getParam3());

            ps.setInt(1, staffId);
            ps.setInt(2, departmentId);
            ps.setInt(3, jobId);
            ps.execute();
            return SUCCESS;
        } catch (SQLException e) {
            return "Вероятно, ошибка в сотруднике, отделе или должности";
        }
    }

    public String addEmployment(ParamRequest request) { //изменить
        try { //movie, wp, fee
            PreparedStatement ps = connection.prepareStatement(request.getFuncName());
            String[] parts = request.getParam2().split(", "); //имя фамилия, почта, должность
            Integer staffId = authService.getStaffIdByEmail(parts[1]);
            Integer jobId = getJobIdByName(parts[2]);
            Integer movieId = getMovieIdByTitle(request.getParam1());

            ps.setInt(1, staffId);
            ps.setInt(2, jobId);
            ps.setInt(3, movieId);
            if (request.getParam4() == null) {
                ps.setBigDecimal(4, null);
            } else {
                ps.setBigDecimal(4, BigDecimal.valueOf(Double.parseDouble(request.getParam4())));
            }
            ps.execute();
            return SUCCESS;
        } catch (SQLException e) {
            return "Вероятно, ошибка в формате гонорара";
        }
    }

    public String changeFee(ParamRequest request) {
        //movie, wp, fee
        try {
            PreparedStatement ps = connection.prepareStatement(request.getFuncName());
            Integer movieId = getMovieIdByTitle(request.getParam1());

            String[] parts = request.getParam2().split(", "); //имя фамилия, почта, должность
            Integer staffId = authService.getStaffIdByEmail(parts[1]);
            Integer jobId = getJobIdByName(parts[2]);
            Integer workerPosId = getWorkPosId(staffId, jobId);

            Integer employmentId = getEmploymentId(movieId, workerPosId);
            ps.setInt(1, employmentId);
            if (request.getParam3() == null) {
                ps.setBigDecimal(2, null);
            } else {
                ps.setBigDecimal(2, BigDecimal.valueOf(Double.parseDouble(request.getParam3())));
            }
            ps.execute();
            return SUCCESS;
        } catch (SQLException e) {
            return "Этот сотрудник не участвует в фильме!";
        }
    }

    public String addJob(ParamRequest request) {
        try {
            PreparedStatement ps = connection.prepareStatement(request.getFuncName());
            ps.setString(1, request.getParam1());
            if (request.getParam2().equals(NULL_FORMAT)) {
                ps.setString(2, null);
            } else {
                ps.setString(2, request.getParam2());
            }
            ps.execute();
            return SUCCESS;
        } catch (SQLException e) {
            return "Вероятно, ошибка в названии";
        }
    }

    public String changeJobName(ParamRequest request) {
        try {
            PreparedStatement ps = connection.prepareStatement(request.getFuncName());
            Integer jobId = getJobIdByName(request.getParam1());
            ps.setInt(1, jobId);
            ps.setString(2, request.getParam2());
            ps.execute();
            return SUCCESS;
        } catch (SQLException e) {
            return "Вероятно, ошибка в названии должности";
        }
    }

    public String addDepartment(ParamRequest request) {
        try {
            PreparedStatement ps = connection.prepareStatement(request.getFuncName());
            ps.setString(1, request.getParam1());
            if (request.getParam2().equals(NULL_FORMAT)) {
                ps.setString(2, null);
            } else {
                ps.setString(2, request.getParam2());
            }
            ps.execute();
            return SUCCESS;
        } catch (SQLException e) {
            return "Вероятно, ошибка в названии";
        }
    }

    public String changeDepartmentName(ParamRequest request) {
        try {
            PreparedStatement ps = connection.prepareStatement(request.getFuncName());
            Integer departmentId = getDepartmentIdByName(request.getParam1());
            ps.setInt(1, departmentId);
            ps.setString(2, request.getParam2());
            ps.execute();
            return SUCCESS;
        } catch (SQLException e) {
            return "Не выбран отдел. ";
        }
    }

    public ResultSet outputStaffByDepartment(ParamRequest request) {
        try {
            PreparedStatement ps = connection.prepareStatement(request.getFuncName());
            Integer departmentId = getDepartmentIdByName(request.getParam1());
            ps.setInt(1, departmentId);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String changeStaffStatus(ParamRequest request) {
        try {
            PreparedStatement ps = connection.prepareStatement(request.getFuncName());
            String[] parts = request.getParam1().split(", "); //имя фамилия, почта, должность
            Integer staffId = authService.getStaffIdByEmail(parts[1]);
            Integer jobId = getJobIdByName(parts[2]);
            Integer workerPosId = getWorkPosId(staffId, jobId);
            ps.setInt(1, workerPosId);
            ps.execute();
            return SUCCESS;
        } catch (SQLException e) {
            return EXCEPTION;
        }
    }


    public ResultSet outputStaffHistory(ParamRequest request) {
        try {
            PreparedStatement ps = connection.prepareStatement(request.getFuncName());
            Integer staffId = authService.getStaffIdByEmail(request.getParam1().split(", ")[1]);
            ps.setInt(1, staffId);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String addMovie(ParamRequest request) {
        try {
            PreparedStatement ps = connection.prepareStatement(request.getFuncName());
            ps.setString(1, request.getParam1());
            ps.setString(2, request.getParam2());
            if (request.getParam3() != null) {
                ps.setBigDecimal(3, BigDecimal.valueOf(Double.parseDouble(request.getParam3())));
            } else {
                ps.setBigDecimal(3, null);
            }
            if (request.getParam4() != null) {
                ps.setBigDecimal(4, BigDecimal.valueOf(Double.parseDouble(request.getParam4())));
            } else {
                ps.setBigDecimal(4, null);
            }
            ps.execute();
            return SUCCESS;
        } catch (SQLException e) {
            return "Нет названия фильма. ";
        }
    }


    public String addLocation(ParamRequest request) {
        try {
            PreparedStatement ps = connection.prepareStatement(request.getFuncName());
            ps.setString(1, request.getParam1());
            ps.setString(2, request.getParam2());
            ps.setString(3, request.getParam3());
            ps.execute();
            return SUCCESS;
        } catch (SQLException e) {
            return "Не все поля заполнены. ";
        }
    }

    public ResultSet outputMoviesHistory(ParamRequest request) {
        try {
            PreparedStatement ps = connection.prepareStatement(request.getFuncName());
            Integer movieId = getMovieIdByTitle(request.getParam1());
            ps.setInt(1, movieId);
            return ps.executeQuery();
        } catch (SQLException e) {
            return null;
        }
    }

    public ResultSet outputMoviesStaff(ParamRequest request) {
        try {
            PreparedStatement ps = connection.prepareStatement(request.getFuncName());
            Integer movieId = getMovieIdByTitle(request.getParam1());
            ps.setInt(1, movieId);
            return ps.executeQuery();
        } catch (SQLException e) {
            return null;
        }
    }

    public String addFilming(ParamRequest request) {
        try {
            PreparedStatement ps = connection.prepareStatement(request.getFuncName());
            Integer movieId = getMovieIdByTitle(request.getParam1());
            String[] parts = request.getParam2().split(", ");
            Integer locationId = getLocationId(parts[0], parts[1], parts[2]);
            ps.setInt(1, movieId);
            ps.setInt(2, locationId);
            ps.setString(3, request.getParam3());
            ps.setInt(4, Integer.parseInt(request.getParam8()));
            ps.execute();
            return SUCCESS;
        } catch (SQLException e) {
            return "Не все поля заполнены. ";
        }
    }

    public ResultSet outputStaffJobsByMovie(ParamRequest request) {
        try {
            PreparedStatement ps = connection.prepareStatement(request.getFuncName());
            Integer movieId = getMovieIdByTitle(request.getParam1());
            ps.setInt(1, movieId);
            ps.setInt(2, Integer.parseInt(request.getParam8()));
            return ps.executeQuery();
        } catch (SQLException e) {
            return null;
        }
    }

    public ResultSet outputStaffByMovie(ParamRequest request) {
        try {
            PreparedStatement ps = connection.prepareStatement(request.getFuncName());
            Integer movieId = getMovieIdByTitle(request.getParam1());
            ps.setInt(1, movieId);
            ps.setInt(2, Integer.parseInt(request.getParam8()));
            return ps.executeQuery();
        } catch (SQLException e) {
            return null;
        }
    }

    public String addStaffFilming(ParamRequest request) {
        try { //wp, filming (movie, date)
            PreparedStatement ps = connection.prepareStatement(request.getFuncName());

            String[] parts = request.getParam1().split("; "); //название фильма, дата !!!
            String[] parts2 = request.getParam2().split(", "); //имя фамилия, почта, должность

            Integer staffId = authService.getStaffIdByEmail(parts2[1]);
            Integer jobId = getJobIdByName(parts2[2]);
            Integer workerPosId = getWorkPosId(staffId, jobId);

            Integer movieId = getMovieIdByTitle(parts[0]);
            Integer filmingId = getFilmingId(movieId, parts[1]);

            //если человек уже занят в эту дату то его нельзя на ту же дату записать
            if (findStaffFilming(staffId, parts[1])!= null && findStaffFilming(staffId, parts[1]).next()) {
                return "Сотрудник уже занят в эту дату.";
            }

            ps.setInt(1, workerPosId);
            ps.setInt(2, filmingId);
            ps.setInt(3, Integer.parseInt(request.getParam8()));
            ps.execute();
            return SUCCESS;
        } catch (SQLException e) {
            return "Не все поля заполнены. ";
        }
    }

    private ResultSet findStaffFilming(Integer staffId, String date) {
        try { //wp, filming (movie, date)
            PreparedStatement ps = connection.prepareStatement("EXEC find_staff_filming ?,?");
            ps.setInt(1, staffId);
            ps.setString(2, date);
            return ps.executeQuery();
        } catch (SQLException e) {
            return null;
        }
    }

    public ResultSet outputFilmingsByManager(ParamRequest request) {
        try {
            PreparedStatement ps = connection.prepareStatement(request.getFuncName());
            Integer movieId = getMovieIdByTitle(request.getParam1());
            ps.setInt(1, movieId);
            ps.setInt(2, Integer.parseInt(request.getParam8()));
            return ps.executeQuery();
        } catch (SQLException e) {
            return null;
        }
    }


    public ResultSet outputStaffFilmingsByMovie(ParamRequest request) {
        Integer movieId = getMovieIdByTitle(request.getParam1());
        try {
            PreparedStatement ps = connection.prepareStatement(request.getFuncName());
            ps.setInt(1, movieId);
            ps.setInt(2, Integer.parseInt(request.getParam8()));
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public ResultSet outputStaffMyInfo(Integer staffId) {
        try {
            PreparedStatement ps = connection.prepareStatement("EXEC output_staff_my_info ?");
            ps.setInt(1, staffId);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String changeMyInfo(ParamRequest request) {
        String answer = "";
        Integer stId = Integer.parseInt(request.getParam8());
        if (request.getParam1() != null) { //пароль, имя, фамилия, дата, номер
            try {
                PreparedStatement ps = connection.prepareStatement("EXEC change_staff_password_hash ?,?");
                ps.setInt(1, stId);
                ps.setString(2, authService.encrypt(request.getParam1()));
                ps.execute();
            } catch (SQLException e) {
                answer += "Ошибка в пароле. ";
            }
        }
        if (request.getParam2() != null) {
            try {
                PreparedStatement ps = connection.prepareStatement("EXEC change_staff_first_name ?,?");
                ps.setInt(1, stId);
                ps.setString(2, request.getParam2());
                ps.execute();
            } catch (SQLException e) {
                answer += "Ошибка в имени. ";
            }
        }
        if (request.getParam3() != null) {
            try {
                PreparedStatement ps = connection.prepareStatement("EXEC change_staff_last_name ?,?");
                ps.setInt(1, stId);
                ps.setString(2, request.getParam3());
                ps.execute();
            } catch (SQLException e) {
                answer += "Ошибка в фамилии. ";
            }
        }
        if (request.getParam4() != null) {
            try {
                PreparedStatement ps = connection.prepareStatement("EXEC change_staff_birthdate ?,?");
                ps.setInt(1, stId);
                ps.setString(2, request.getParam4());
                ps.execute();
            } catch (SQLException e) {
                answer += "Ошибка в дне рождения. ";
            }
        }
        if (request.getParam5() != null && !request.getParam5().equals(PHONE_FORMAT)) {
            try {
                PreparedStatement ps = connection.prepareStatement("EXEC change_staff_phone_number ?,?");
                ps.setInt(1, stId);
                ps.setString(2, request.getParam5());
                ps.execute();
            } catch (SQLException e) {
                answer += "Ошибка в номере телефона. ";
            }
        }
        return answer.equals("") ? SUCCESS : answer;
    }

    public String changeMovieInfo(ParamRequest request) {
        String answer = "";
        if (request.getParam1() == null) {
            return "Не выбран фильм. ";
        }
        Integer movieId = getMovieIdByTitle(request.getParam1());
        //название, премьера, расходы, доходы, Статус, дата
        if (request.getParam2() != null) {
            try {
                PreparedStatement ps = connection.prepareStatement("EXEC change_movie_name ?,?");
                ps.setInt(1, movieId);
                ps.setString(2, request.getParam2());
                ps.execute();
            } catch (SQLException e) {
                answer += "Ошибка в названии. ";
            }
        }
        if (request.getParam3() != null) {
            try {
                PreparedStatement ps = connection.prepareStatement("EXEC change_movie_premiere ?,?");
                ps.setInt(1, movieId);
                ps.setString(2, request.getParam3());
                ps.execute();
            } catch (SQLException e) {
                answer += "Ошибка в дате премьеры. ";
            }
        }
        if (request.getParam4() != null) {
            try {
                PreparedStatement ps = connection.prepareStatement("EXEC change_movie_expenses ?,?");
                ps.setInt(1, movieId);
                ps.setString(2, request.getParam4());
                ps.execute();
            } catch (SQLException e) {
                answer += "Ошибка в расходе. ";
            }
        }
        if (request.getParam5() != null) {
            try {
                PreparedStatement ps = connection.prepareStatement("EXEC change_movie_revenue ?,?");
                ps.setInt(1, movieId);
                ps.setString(2, request.getParam5());
                ps.execute();
            } catch (SQLException e) {
                answer += "Ошибка в доходе. ";
            }
        }

        if (request.getParam6() != null) {
            if (request.getParam7() == null) {
                answer += "Не выбрана дата изменения статуса. ";
            } else {
                try {
                    PreparedStatement ps = connection.prepareStatement("EXEC change_movie_status ?,?,?");
                    ps.setInt(1, movieId);
                    ps.setString(2, request.getParam7());
                    ps.setString(3, request.getParam6());
                    ps.execute();
                } catch (SQLException e) {
                    answer += "Ошибка в номере телефона. ";
                }
            }
        }
        return answer.equals("") ? SUCCESS : answer;
    }

    public String changeFilmingInfo(ParamRequest request) {
        String answer = "";
        if (request.getParam1() == null) {
            return "Не выбрана съёмка. ";
        }
        String[] parts = request.getParam1().split("; ");
        Integer movieId = getMovieIdByTitle(parts[0]);

        Integer filmingId = getFilmingId(movieId, parts[1]);

        if (request.getParam4().equals("Да")) {
            try {
                PreparedStatement ps = connection.prepareStatement("EXEC delete_filming ?,?");
                ps.setInt(1, filmingId);
                ps.setInt(2, Integer.parseInt(request.getParam8()));
                ps.execute();
                return SUCCESS;
            } catch (SQLException e) {
                answer += EXCEPTION;
            }
        }
        if (request.getParam2() != null) {
            try {
                PreparedStatement ps = connection.prepareStatement("EXEC  change_filming_location ?,?,?");
                ps.setInt(1, filmingId);
                ps.setInt(2, Integer.parseInt(request.getParam8()));
                String[] parts2 = request.getParam2().split(", ");
                Integer locationId = getLocationId(parts2[0], parts2[1], parts2[2]);
                ps.setInt(3, locationId);
                ps.execute();
            } catch (SQLException e) {
                answer += EXCEPTION;
            }
        }
        if (request.getParam3() != null) {
            try {
                PreparedStatement ps = connection.prepareStatement("EXEC change_filming_date ?,?,?");
                ps.setInt(1, filmingId);
                ps.setInt(2, Integer.parseInt(request.getParam8()));
                ps.setString(3, request.getParam3());
                ps.execute();
            } catch (SQLException e) {
                answer += EXCEPTION;
            }
        }
        return answer.equals("") ? SUCCESS : answer;
    }

    //------------------------------------------------------------------------------------------------------------------

    public ResultSet chooseWithoutParamFunc(String funcName, String staffId) {
        Integer stId = Integer.parseInt(staffId);
        try {
            PreparedStatement ps = connection.prepareStatement(funcName);
            if (funcName.contains("?")) {
                ps.setInt(1, stId);
            }
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer getMovieIdByTitle(String title) {
        try {
            PreparedStatement ps = connection.prepareStatement("EXEC get_movie_id_by_name ?");
            ps.setString(1, title);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Integer.parseInt(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Integer getJobIdByName(String name) {
        try {
            PreparedStatement ps = connection.prepareStatement("EXEC get_job_id_by_name ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Integer.parseInt(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Integer getDepartmentIdByName(String name) {
        try {
            PreparedStatement ps = connection.prepareStatement("EXEC get_department_id_by_name ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Integer.parseInt(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Integer getLocationId(String country, String city, String address) {
        try {
            PreparedStatement ps = connection.prepareStatement("EXEC get_location_id ?,?,?");
            ps.setString(1, country);
            ps.setString(2, city);
            ps.setString(3, address);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Integer.parseInt(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Integer getWorkPosId(Integer staffId, Integer jobId) {
        try {
            PreparedStatement ps = connection.prepareStatement("EXEC get_worker_position_id ?,?");
            ps.setInt(1, staffId);
            ps.setInt(2, jobId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Integer.parseInt(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Integer getEmploymentId(Integer movieId, Integer workerPosId) {
        try {
            PreparedStatement ps = connection.prepareStatement("EXEC get_employment_id ?,?");
            ps.setInt(1, movieId);
            ps.setInt(2, workerPosId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Integer.parseInt(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Integer getFilmingId(Integer movieId, String date) {
        try {
            PreparedStatement ps = connection.prepareStatement("EXEC get_filming_id ?,?");
            ps.setInt(1, movieId);
            ps.setString(2, date);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Integer.parseInt(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
