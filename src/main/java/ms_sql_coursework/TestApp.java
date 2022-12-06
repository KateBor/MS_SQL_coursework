package ms_sql_coursework;

import ms_sql_coursework.services.AuthService;
import java.sql.*;

public class TestApp {
    public static void main(String[] args) {
        String url = "jdbc:sqlserver://DESKTOP-LR51BMB:1433;databaseName=movieStudio;integratedSecurity=true;encrypt=true;trustServerCertificate=true";
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection conn = DriverManager.getConnection(url);
            System.out.println("Success");

//            String SPsql = "EXEC get_filmings";   // for stored proc taking 2 parameters
//            PreparedStatement ps = conn.prepareStatement(SPsql);
//            ps.setEscapeProcessing(true);
//            //ps.setQueryTimeout(<timeout value>);
//            // ps.setString(1, <param1>);
//            //ps.setString(2, <param2>);
//            ResultSet rs = ps.executeQuery();
//            ResultSetMetaData rsmd = rs.getMetaData();
//            int columnsNumber = rsmd.getColumnCount();
//
//            for (int j = 1; j <= columnsNumber; j++) { //заголовки
//                System.out.print(rsmd.getColumnName(j) + "\t"); //как то выровнять это надо
//            }
//            System.out.println();
//            while (rs.next()) { //данные
//                for (int i = 1; i <= columnsNumber; i++) {
//                    String columnValue = rs.getString(i);
//                    System.out.print(columnValue + ";\t\t");
//                }
//                System.out.println("");
//            }
            AuthService authService = new AuthService(conn);
//            String hash = authService.encrypt("pwd9");
//            System.out.println(hash);
            System.out.println(authService.decrypt("pwd9"));
//
//            hash = authService.encrypt("pwd2");
//            System.out.println(hash);
//            System.out.println(authService.decrypt(hash));


            System.out.println(authService.getRoleByEmail("admin@mail.ru", "pwd1"));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
