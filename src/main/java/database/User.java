package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class User {
    public static Connection connection = DBConnection.open();
    public static List<String> getAllUsername(){
        ArrayList<String> listUsername = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = (PreparedStatement) connection.prepareStatement("SELECT * FROM user");
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                String username = resultSet.getString(1);
                listUsername.add(username);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
            listUsername = null;
        }
        return  listUsername;

    }

    public static boolean checkPassword(String usrname, String password){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String pw = "";
        try {
            String query = "SELECT password From user where username = '" + usrname +"'";
            System.out.println(query);
            preparedStatement = (PreparedStatement) connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                pw = resultSet.getString(1);
                System.out.println(pw);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            pw = null;
        }
        if(pw.equals(password)){
            return true;
        }
        else {
            return false;
        }
    }

    public static boolean addStudent(String username, String pw){
        PreparedStatement psPreparedStatement = null;
        ResultSet rSet = null;
        try {
            String query= "INSERT INTO user"
                    + " values (?,?,0)";
//			String query2 = "INSERT INTO muontra2 VALUES('" newBorrow.getMaMT()+ "','" + "','" + "','" + "','" + "'";
            //	String query2 = "INSERT INTO "
            psPreparedStatement = (PreparedStatement) connection.prepareStatement(query);
            psPreparedStatement.setString(1, username);
            psPreparedStatement.setString(2, pw);

            System.out.println(query);

            psPreparedStatement.executeUpdate();
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public static void main(String[] args) {
        List<String> lu = getAllUsername();
        System.out.println(lu.size());
        System.out.println(checkPassword("phien","Phien2000"));
        System.out.println(addStudent("tests2","Te"));
    }
}
