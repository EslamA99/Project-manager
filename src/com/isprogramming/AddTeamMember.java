package com.isprogramming;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AddTeamMember extends JFrame {
    private JTable tasksTable;
    private JTextField cost;
    private JTextField tName;
    private JButton ADDMemberButton;
    private JTextField tit;
    private JSpinner spinner1;
    private JPanel panel1;
    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;
    DefaultTableModel tableModel = new DefaultTableModel();
    int p_id = AddProject.p_id;

    public AddTeamMember() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/isproject?characterEncoding=latin1", "root", "12345678");
        stmt = con.createStatement();
        setTitle("AddTeamMembers");
        setSize(800, 400);
        add(panel1);
        tasksTable.setModel(tableModel);
        tableModel.addColumn("t_id ");
        tableModel.addColumn("t_name  ");
        tableModel.addColumn("numOfHours  ");
        tableModel.addColumn("startDate  ");
        tableModel.addColumn("dueDate  ");
        tableModel.addColumn("p_id ");
        rs = stmt.executeQuery("select * from task where  p_id=" + p_id);
        while (rs.next()) {
            tableModel.insertRow(tableModel.getRowCount(), new Object[]{rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getDate(4).toString(), rs.getDate(5).toString(),rs.getInt(6)});
        }
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        ADDMemberButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    rs = stmt.executeQuery("select t_id from task where t_name=" + "'" + tName.getText() + "'" +
                            "and p_id=" + p_id);
                    if (!rs.next()) {
                        JOptionPane.showMessageDialog(null, "task not found !");
                        return;
                    }
                    int taskID = rs.getInt(1);
                    String query = " insert into teammembers (title,numberOfHours,cost,p_id,t_id)"
                            + " values (?,?,?,?,?)";
                    PreparedStatement preparedStmt = con.prepareStatement(query);
                    preparedStmt.setString(1, tit.getText());
                    preparedStmt.setInt(2, (Integer) spinner1.getValue());
                    preparedStmt.setDouble(3, Double.valueOf(cost.getText()));


                    /*Statement stmt = con.createStatement();
                    ResultSet rss = stmt.executeQuery("select * from task where " + t_name + "= t_name");
                    int taskId = rss.getInt(1);
                    int projectId = rss.getInt(6);*/

                    preparedStmt.setInt(4, p_id);
                    preparedStmt.setInt(5, taskID);

                    preparedStmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
