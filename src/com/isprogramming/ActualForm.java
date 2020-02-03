package com.isprogramming;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ActualForm extends JFrame{
    int p_id = AddProject.p_id;
    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;
    private JPanel panel1;
    private JTable tasksTable;
    private JTable taskAndHourTable;
    private JButton addTaskNameButton;
    private JButton removeTaskNameButton;
    private JButton confirmButton;
    private JTable resultTable;
    DefaultTableModel tableModel1 = new DefaultTableModel();
    DefaultTableModel tableModel2 = new DefaultTableModel();
    DefaultTableModel tableModel3 = new DefaultTableModel();
    ActualForm() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/isproject?characterEncoding=latin1", "root", "12345678");
        stmt = con.createStatement();
        setTitle("Actual Form");
        setSize(800, 600);
        add(panel1);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        tasksTable.setModel(tableModel1);
        tasksTable.setEnabled(false);
        tableModel1.addColumn("t_id ");
        tableModel1.addColumn("t_name  ");
        tableModel1.addColumn("numOfHours  ");
        tableModel1.addColumn("startDate  ");
        tableModel1.addColumn("dueDate  ");
        tableModel1.addColumn("p_id ");
        rs=stmt.executeQuery("select * from task where p_id="+p_id);
        while (rs.next()){
            tableModel1.insertRow(tableModel1.getRowCount(), new Object[]{rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getDate(4).toString(), rs.getDate(5).toString(),rs.getInt(6)});
        }
        taskAndHourTable.setModel(tableModel2);
        tableModel2.addColumn("task name");
        tableModel2.addColumn("task actual hour");
        resultTable.setModel(tableModel3);
        tableModel3.addColumn("task ID");
        tableModel3.addColumn("task name");
        tableModel3.addColumn("task hours");
        tableModel3.addColumn("is actual");
        addTaskNameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                tableModel2.insertRow(tableModel2.getRowCount(), new Object[]{"t",0});
            }
        });
        removeTaskNameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int[] rows = taskAndHourTable.getSelectedRows();
                for (int i = 0; i < rows.length; i++) {
                    tableModel2.removeRow(rows[i] - i);
                }
            }
        });
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(tableModel2.getRowCount()!=0){
                    try{
                        for(int i=0;i<taskAndHourTable.getRowCount();i++){
                            rs=stmt.executeQuery("select t_id,numOfHours from task where t_name = "+"'"+tableModel2.getValueAt(i,0).toString().trim()+"'"
                                    +"and p_id="+p_id);
                            if(!rs.next()){
                                JOptionPane.showMessageDialog(null, "task not found !");
                                return;
                            }

                        }
                        for(int i=0;i<taskAndHourTable.getRowCount();i++){
                            rs=stmt.executeQuery("select t_id,numOfHours,t_name from task where t_name = "+"'"+tableModel2.getValueAt(i,0).toString().trim()+"'"
                                    +"and p_id="+p_id);
                            rs.next();
                            int t_id=rs.getInt(1);
                            int hours=rs.getInt(2);
                            String t_name=rs.getString(3);
                            String queryy = " insert into actualtasks (t_id,actualHours)"
                                    + " values (?,?)";
                            PreparedStatement preparedStmtt = con.prepareStatement(queryy);
                            preparedStmtt.setInt(1,t_id);//from gui
                            preparedStmtt.setInt(2,hours);
                            preparedStmtt.executeUpdate();
                            if(Integer.parseInt(tableModel2.getValueAt(i,1).toString())>hours){
                                tableModel3.insertRow(tableModel3.getRowCount(), new Object[]{t_id,t_name,hours,"false"});
                            }
                            else {
                                tableModel3.insertRow(tableModel3.getRowCount(), new Object[]{t_id,t_name,hours,"true"});
                            }

                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
