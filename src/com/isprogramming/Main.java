package com.isprogramming;

import java.sql.*;
import java.util.Calendar;

public class Main {

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/isproject?characterEncoding=latin1", "root", "12345678");
            Statement stmt=con.createStatement();
            /*ResultSet rs=stmt.executeQuery("select * from emp");
            while(rs.next())
                System.out.println(rs.getInt(1)+"  "+rs.getString(2)+"  "+rs.getString(3));*/
            Calendar calendar = Calendar.getInstance();
            java.sql.Date startDate = new java.sql.Date(calendar.getTime().getTime());
            String query = " insert into project (name,numberOfWorkingHours,startingDay)"
                    + " values (?,?,?)";
            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setString (1, "a");
            preparedStmt.setInt (2, 12);
            preparedStmt.setDate (3, startDate);
            /*preparedStmt.setString (4, "a");
            preparedStmt.setString (5, "a");*/
           /* preparedStmt.setString (6, "a");
            preparedStmt.setString (7, "a");*/
            preparedStmt.executeUpdate();
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}

