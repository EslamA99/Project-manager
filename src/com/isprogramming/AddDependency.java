package com.isprogramming;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class AddDependency extends JFrame {
    int p_id = AddProject.p_id;
    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;
    private JPanel panel1;
    private JTable tasksTable;
    private JTextField taskName;
    private JButton confirmButton;
    private JTable dependantTable;
    private JButton addDependantButton;
    private JButton removeDependantButton;
    private JButton confirmButton1;
    DefaultTableModel tableModel1 = new DefaultTableModel();
    DefaultTableModel tableModel2 = new DefaultTableModel();
    int taskID=0;
    AddDependency() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/isproject?characterEncoding=latin1", "root", "12345678");
        stmt = con.createStatement();
        setTitle("Tasks And SubTasks");
        setSize(800, 600);
        add(panel1);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addDependantButton.setEnabled(false);
        removeDependantButton.setEnabled(false);
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
        dependantTable.setModel(tableModel2);
        tableModel2.addColumn("Task Name");

        addDependantButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                tableModel2.insertRow(tableModel2.getRowCount(), new Object[]{"t"});
            }
        });
        removeDependantButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int[] rows = dependantTable.getSelectedRows();
                for (int i = 0; i < rows.length; i++) {
                    tableModel2.removeRow(rows[i] - i);
                }
            }
        });
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    if(taskName.getText().trim().isEmpty()){
                        JOptionPane.showMessageDialog(null, "task is empty !");
                        return;
                    }
                    rs=stmt.executeQuery("select t_id from task where t_name = "+"'"+taskName.getText().trim()+"'"
                    +"and p_id="+p_id);
                    if(!rs.next()){
                        JOptionPane.showMessageDialog(null, "task not found !");
                        return;
                    }
                    taskID=rs.getInt(1);
                    confirmButton.setEnabled(false);
                    taskName.setEnabled(false);
                    confirmButton1.setEnabled(true);
                    addDependantButton.setEnabled(true);
                    removeDependantButton.setEnabled(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        confirmButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    ArrayList<Integer>dIds=new ArrayList<>();

                    for(int i=0;i<dependantTable.getRowCount();i++){
                        rs=stmt.executeQuery("select t_id from task where t_name = "+"'"+tableModel2.getValueAt(i,0).toString().trim()+"'"
                        +"and p_id="+p_id);
                        if(!rs.next()){
                            JOptionPane.showMessageDialog(null, "task not found !");
                            return;
                        }
                        int id=rs.getInt(1);
                        if(taskID==id){
                            JOptionPane.showMessageDialog(null, "u cannot make dep for same task !");
                            return;
                        }
                        dIds.add(id);
                    }

                    /*ResultSet rsss = stmt.executeQuery("select t_name from task where t_name="+"'"+taskName.getText().trim()+"'");
                    int dId=rsss.getInt(1);*/

                    for(int i=0;i<dIds.size();i++){
                        String queryy = " insert into dependent (t_id,d_id)"
                                + " values (?,?)";
                        PreparedStatement preparedStmtt = con.prepareStatement(queryy);
                        preparedStmtt.setInt(1,taskID);//from gui
                        preparedStmtt.setInt(2,dIds.get(i));
                        preparedStmtt.executeUpdate();
                    }
                    confirmButton.setEnabled(true);
                    taskName.setEnabled(true);
                    confirmButton1.setEnabled(false);
                    addDependantButton.setEnabled(false);
                    removeDependantButton.setEnabled(false);
                    int rowCount = tableModel2.getRowCount();
                    for (int i = rowCount - 1; i >= 0; i--) {
                        tableModel2.removeRow(i);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
