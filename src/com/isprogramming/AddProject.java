package com.isprogramming;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddProject extends JFrame {
    private JTextField pName;
    private JTextField day;
    private JTextField month;
    private JTextField year;
    private JButton addProjectButton;
    private JPanel panel1;
    private JSpinner spinner1;
    public static int p_id=0;
    AddProject() {
        setTitle("Add Project");
        setSize(800, 300);

        add(panel1);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addProjectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection con = DriverManager.getConnection(
                            "jdbc:mysql://localhost:3306/isproject?characterEncoding=latin1", "root", "12345678");
                    Calendar calendar = Calendar.getInstance();
                    String sDate1 = "31/12/1998";
                    Date date1 = new SimpleDateFormat("dd/MM/yyyy").parse(day.getText() + "/" + month.getText() + "/" + year.getText());
                    java.sql.Date startDate = new java.sql.Date(date1.getTime());
                    String query = " insert into project (name,numberOfWorkingHours,startingDay)"
                            + " values (?,?,?)";
                    PreparedStatement preparedStmt = con.prepareStatement(query);
                    preparedStmt.setString(1, pName.getText());
                    preparedStmt.setInt(2, (Integer) spinner1.getValue());
                    preparedStmt.setDate(3, startDate);
                    preparedStmt.executeUpdate();
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery("select MAX(p_id) from project");
                    rs.next();
                    p_id=rs.getInt(1);
                    con.close();
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {

                            TaskAndSubTasks taskAndSubTasks = null;
                            try {
                                taskAndSubTasks = new TaskAndSubTasks();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            taskAndSubTasks.setVisible(true);
                        }
                    });
                    dispose();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });
    }

    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                AddProject addProject = new AddProject();
                addProject.setVisible(true);
            }
        });
    }
}
