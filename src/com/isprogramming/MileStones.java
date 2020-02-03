package com.isprogramming;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class MileStones extends JFrame {
    private JTable mileStonesTable;
    private JLabel mileStonesCount;
    private JPanel panel1;
    DefaultTableModel tableModel = new DefaultTableModel();
    int p_id = AddProject.p_id;
    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;

    MileStones() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/isproject?characterEncoding=latin1", "root", "12345678");
        stmt = con.createStatement();
        setTitle("Tasks And SubTasks");
        setSize(800, 400);
        add(panel1);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mileStonesTable.setEnabled(false);
        mileStonesTable.setModel(tableModel);
        tableModel.addColumn("t_id ");
        tableModel.addColumn("t_name  ");
        tableModel.addColumn("numOfHours  ");
        tableModel.addColumn("startDate  ");
        tableModel.addColumn("dueDate  ");
        tableModel.addColumn("p_id ");
        rs=stmt.executeQuery("select * from task where startDate=dueDate and p_id="+p_id);
        while (rs.next()){
            tableModel.insertRow(tableModel.getRowCount(), new Object[]{rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getDate(4).toString(), rs.getDate(5).toString(),rs.getInt(6)});
        }
        mileStonesCount.setText(String.valueOf(mileStonesTable.getRowCount()));
    }
}
