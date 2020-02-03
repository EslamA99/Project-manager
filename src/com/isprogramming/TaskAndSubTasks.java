package com.isprogramming;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TaskAndSubTasks extends JFrame {
    private JPanel panel1;
    private JTextField tStartDate;
    private JTextField tDueDate;
    private JTextField taskName;
    private JSpinner taskDays;
    private JButton addTaskButton;
    private JTable supTaskTable;
    private JButton addSupTaskButton;
    private JButton removeSupTaskButton;
    private JButton confirmButton;
    private JButton addTeamMembersButton;
    private JButton getMileStonesButton;
    private JButton dependancyButton;
    private JButton actualButton;
    private JButton showChartButton;
    DefaultTableModel tableModel = new DefaultTableModel();
    int p_id = AddProject.p_id;
    int task_id = 0;
    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;

    TaskAndSubTasks() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/isproject?characterEncoding=latin1", "root", "12345678");
        stmt = con.createStatement();
        setTitle("Tasks And SubTasks");
        setSize(800, 600);
        add(panel1);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addSupTaskButton.setEnabled(false);
        removeSupTaskButton.setEnabled(false);
        confirmButton.setEnabled(false);
        addTeamMembersButton.setEnabled(false);
        supTaskTable.setModel(tableModel);
        tableModel.addColumn("Name");
        tableModel.addColumn("Days");
        tableModel.addColumn("startDate");
        tableModel.addColumn("dueDate");
       /* UtilDateModel model = new UtilDateModel();
        JDatePanelImpl datePanel = new JDatePanelImpl(model);
        JDatePickerImpl datePicker = new JDatePickerImpl(datePanel);

        frame.add(datePicker);*/
        addSupTaskButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                tableModel.insertRow(tableModel.getRowCount(), new Object[]{"t", 0, "1/1/2000", "1/1/2000"});
            }
        });
        removeSupTaskButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int[] rows = supTaskTable.getSelectedRows();
                for (int i = 0; i < rows.length; i++) {
                    tableModel.removeRow(rows[i] - i);
                }
            }
        });
        addTaskButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    if (taskName.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Name couldn't be empty");
                        return;
                    }
                    rs = stmt.executeQuery("select t_name from task where t_name=" + "'" + taskName.getText().trim() + "'" +
                            "and p_id=" + p_id);
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(null, "this task found before");
                        return;
                    }
                    Date date1 = new SimpleDateFormat("dd/MM/yyyy").parse(tStartDate.getText());
                    Date date2 = new SimpleDateFormat("dd/MM/yyyy").parse(tDueDate.getText());
                    java.sql.Date startDate = new java.sql.Date(date1.getTime());
                    java.sql.Date endDate = new java.sql.Date(date2.getTime());

                    String query = " insert into task (t_name,numOfHours,startDate,dueDate,p_id)"
                            + " values (?,?,?,?,?)";
                    PreparedStatement preparedStmt = con.prepareStatement(query);
                    preparedStmt.setString(1, taskName.getText());
                    int hours = (Integer) taskDays.getValue() * 24;
                    preparedStmt.setInt(2, hours);
                    preparedStmt.setDate(3, startDate);
                    preparedStmt.setDate(4, endDate);
                    preparedStmt.setInt(5, p_id);
                    preparedStmt.executeUpdate();
                    //stmt = con.createStatement();
                    rs = stmt.executeQuery("select MAX(t_id) from task");
                    rs.next();
                    task_id = rs.getInt(1);
                    //con.close();
                    addSupTaskButton.setEnabled(true);
                    removeSupTaskButton.setEnabled(true);
                    confirmButton.setEnabled(true);
                    addTeamMembersButton.setEnabled(true);
                    addTaskButton.setEnabled(false);
                    tStartDate.setEnabled(false);
                    tDueDate.setEnabled(false);
                    taskName.setEnabled(false);
                    taskDays.setEnabled(false);

                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Date parentSDate = null;
                Date parentDueDate = null;
                Date supSDate = null;
                Date supDueDate = null;
                try {
                    parentSDate = new SimpleDateFormat("dd/MM/yyyy").parse(tStartDate.getText());
                    parentDueDate = new SimpleDateFormat("dd/MM/yyyy").parse(tDueDate.getText());
                    ArrayList<String> subTasksName = new ArrayList<>();
                    if (tableModel.getRowCount() != 0) {
                        int totalDays = 0;
                        for (int i = 0; i < tableModel.getRowCount(); i++) {
                            supSDate = new SimpleDateFormat("dd/MM/yyyy").parse(tableModel.getValueAt(i, 2).toString());
                            supDueDate = new SimpleDateFormat("dd/MM/yyyy").parse(tableModel.getValueAt(i, 3).toString());
                            if (supDueDate.compareTo(supSDate) < 0) {
                                JOptionPane.showMessageDialog(null, "start date of sup task occurs after it's duedate !");
                                return;
                            }
                            if (supSDate.compareTo(parentSDate) < 0) {
                                JOptionPane.showMessageDialog(null, "start date of sup task occurs before main task !");
                                return;
                            }
                            if (supDueDate.compareTo(parentDueDate) > 0) {
                                JOptionPane.showMessageDialog(null, "due date of sup task occurs after main task !");
                                return;
                            }
                            if (tableModel.getValueAt(i, 0).toString().equals(taskName.toString())) {
                                JOptionPane.showMessageDialog(null, "tasks shouldn't have same name !");
                                return;
                            }
                            totalDays += Integer.parseInt(tableModel.getValueAt(i, 1).toString());
                            subTasksName.add(tableModel.getValueAt(i, 0).toString());
                        }
                        if (totalDays > Integer.parseInt(taskDays.getValue().toString())) {
                            JOptionPane.showMessageDialog(null, "the sum of the time required to complete the sub-tasks cannot exceed the time needed for the parent   !");
                            return;
                        }
                        for (int i = 0; i < subTasksName.size(); i++) {
                            String t = subTasksName.get(i);
                            subTasksName.remove(i--);
                            if (subTasksName.contains(t)) {
                                JOptionPane.showMessageDialog(null, "tasks shouldn't have same name !");
                                return;
                            }
                            rs = stmt.executeQuery("select t_name from task where t_name=" + "'" + t + "'" +
                                    "and p_id=" + p_id);
                            if (rs.next()) {
                                JOptionPane.showMessageDialog(null, "tasks shouldn't have same name !");
                                return;
                            }
                        }
                        for (int i = 0; i < tableModel.getRowCount(); i++) {
                            supSDate = new SimpleDateFormat("dd/MM/yyyy").parse(tableModel.getValueAt(i, 2).toString());
                            supDueDate = new SimpleDateFormat("dd/MM/yyyy").parse(tableModel.getValueAt(i, 3).toString());
                            java.sql.Date startDate = new java.sql.Date(supSDate.getTime());
                            java.sql.Date endDate = new java.sql.Date(supDueDate.getTime());
                            String query = " insert into task (t_name,numOfHours,startDate,dueDate,p_id)"
                                    + " values (?,?,?,?,?)";
                            PreparedStatement preparedStmt = con.prepareStatement(query);
                            preparedStmt.setString(1, tableModel.getValueAt(i, 0).toString());
                            int hours = Integer.parseInt(tableModel.getValueAt(i, 1).toString()) * 24;
                            preparedStmt.setInt(2, hours);
                            preparedStmt.setDate(3, startDate);
                            preparedStmt.setDate(4, endDate);
                            //-----------------------------
                            preparedStmt.setInt(5, p_id);
                            preparedStmt.executeUpdate();
                            //stmt = con.createStatement();
                            rs = stmt.executeQuery("select MAX(t_id) from task");
                            rs.next();
                            int supTaskID = rs.getInt(1);
                            query = " insert into suptasks (t_id,supTask_id )"
                                    + " values (?,?)";
                            System.out.println(task_id + " " + supTaskID);
                            PreparedStatement preparedStmt1 = con.prepareStatement(query);
                            preparedStmt1.setInt(1, task_id);
                            preparedStmt1.setInt(2, supTaskID);
                            preparedStmt1.executeUpdate();
                        }
                        addSupTaskButton.setEnabled(false);
                        removeSupTaskButton.setEnabled(false);
                        confirmButton.setEnabled(false);
                        int rowCount = tableModel.getRowCount();
                        for (int i = rowCount - 1; i >= 0; i--) {
                            tableModel.removeRow(i);
                        }
                        addTaskButton.setEnabled(true);
                        tStartDate.setEnabled(true);
                        tDueDate.setEnabled(true);
                        taskName.setEnabled(true);
                        taskDays.setEnabled(true);
                    }
                    else{
                        addSupTaskButton.setEnabled(false);
                        removeSupTaskButton.setEnabled(false);
                        addTaskButton.setEnabled(true);
                        tStartDate.setEnabled(true);
                        tDueDate.setEnabled(true);
                        taskName.setEnabled(true);
                        taskDays.setEnabled(true);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "some error occur !");
                }

            }
        });
        addTeamMembersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (UnsupportedLookAndFeelException e) {
                    e.printStackTrace();
                }
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {

                        AddTeamMember addTeamMember = null;
                        try {
                            addTeamMember = new AddTeamMember();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        addTeamMember.setVisible(true);
                    }
                });
            }
        });
        getMileStonesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (UnsupportedLookAndFeelException e) {
                    e.printStackTrace();
                }
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {

                        MileStones mileStones = null;
                        try {
                            mileStones = new MileStones();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        mileStones.setVisible(true);
                    }
                });
            }
        });
        dependancyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (UnsupportedLookAndFeelException e) {
                    e.printStackTrace();
                }
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {

                        AddDependency addDependency = null;
                        try {
                            addDependency = new AddDependency();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        addDependency.setVisible(true);
                    }
                });
            }
        });
        actualButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (UnsupportedLookAndFeelException e) {
                    e.printStackTrace();
                }
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {

                        ActualForm actualForm = null;
                        try {
                            actualForm = new ActualForm();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        actualForm.setVisible(true);
                    }
                });
            }
        });
        showChartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (UnsupportedLookAndFeelException e) {
                    e.printStackTrace();
                }
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {

                        Chart chart = null;
                        try {
                            chart = new Chart();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        chart.setVisible(true);
                    }
                });
            }
        });
    }
}
