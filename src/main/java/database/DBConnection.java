package database;

import java.sql.*;

public class DBConnection {
    public static String DEFAULT_DRIVER = "com.mysql.cj.jdbc.Driver";
    public static Connection open() {
        Connection cnn = null;
        try {
            String driverString = DEFAULT_DRIVER;
            String url = "jdbc:mysql://localhost:3306/chat_attt";
            String user = "root";
            String pass = "";
            Class.forName(driverString);
            cnn = (Connection) DriverManager.getConnection(url,user,pass);
        }
        catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return cnn;
    }
    public static void close(ResultSet rs, PreparedStatement ps, Connection cnn) {
        try {
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
            if (ps != null && !ps.isClosed()) {
                ps.close();
            }
            if (cnn != null && !cnn.isClosed()) {
                cnn.close();
            }
        } catch (SQLException ex) {
            System.out.println("Cannot close db access");
        }
    }
}
