package com.personalexpense.view;

import com.personalexpense.model.User;
import com.personalexpense.model.Expense;
import com.personalexpense.service.UserService;
import com.personalexpense.service.ExpenseService;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

public class AdminView extends JFrame {

    private static final long serialVersionUID = 1L;

    private final transient UserService userService;
    private final transient ExpenseService expenseService;
    private final transient Provider<LoginView> loginViewProvider;

    private transient JTable userTable;
    private transient DefaultTableModel tableModel;
    private transient JTextField usernameField;
    private transient JTextField passwordField;
    private transient JComboBox<String> roleCombo;
    private transient JButton createButton;
    private transient JButton updateButton;
    private transient JButton deleteButton;
    private transient JButton disableButton;
    private transient JButton enableButton;
    private transient JButton reportButton;
    private transient JButton logoutButton;
    private transient JLabel errorLabel;

    @Inject
    public AdminView(UserService userService, 
                     ExpenseService expenseService, 
                     Provider<LoginView> loginViewProvider) {
        this.userService = userService;
        this.expenseService = expenseService;
        this.loginViewProvider = loginViewProvider;

        initComponents();
        layoutComponents();
        attachListeners();
        refreshUserTable();
    }

    private void initComponents() {
        setTitle("Admin Panel - User Management");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel(
            new Object[]{"ID", "Username", "Role", "Enabled"}, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(tableModel);
        userTable.setName("userTable");
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        usernameField = new JTextField(15);
        usernameField.setName("usernameField");

        passwordField = new JTextField(15);
        passwordField.setName("passwordField");

        roleCombo = new JComboBox<>(new String[]{"USER", "ADMIN"});
        roleCombo.setName("roleCombo");

        createButton = new JButton("Create User");
        createButton.setName("createButton");

        updateButton = new JButton("Update User");
        updateButton.setName("updateButton");

        deleteButton = new JButton("Delete User");
        deleteButton.setName("deleteButton");

        disableButton = new JButton("Disable User");
        disableButton.setName("disableButton");

        enableButton = new JButton("Enable User");
        enableButton.setName("enableButton");

        reportButton = new JButton("View User Report");
        reportButton.setName("reportButton");

        logoutButton = new JButton("Logout");
        logoutButton.setName("logoutButton");

        errorLabel = new JLabel(" ");
        errorLabel.setName("errorLabel");
        errorLabel.setForeground(java.awt.Color.RED);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(5, 5));

        JScrollPane tableScrollPane = new JScrollPane(userTable);
        add(tableScrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        inputPanel.add(new JLabel("Username:"));
        inputPanel.add(usernameField);
        inputPanel.add(new JLabel("Password:"));
        inputPanel.add(passwordField);
        inputPanel.add(new JLabel("Role:"));
        inputPanel.add(roleCombo);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        actionPanel.add(createButton);
        actionPanel.add(updateButton);
        actionPanel.add(deleteButton);
        actionPanel.add(disableButton);
        actionPanel.add(enableButton);
        actionPanel.add(reportButton);
        actionPanel.add(logoutButton);

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.add(inputPanel);
        topPanel.add(actionPanel);

        add(topPanel, BorderLayout.NORTH);
        add(errorLabel, BorderLayout.SOUTH);
    }

    private void attachListeners() {
        createButton.addActionListener(e -> createUser());
        updateButton.addActionListener(e -> updateUser());
        deleteButton.addActionListener(e -> deleteUser());
        disableButton.addActionListener(e -> disableUser());
        enableButton.addActionListener(e -> enableUser());
        reportButton.addActionListener(e -> viewUserReport());
        logoutButton.addActionListener(e -> logout());

        userTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                populateFieldsFromSelectedRow();
            }
        });
    }

    void createUser() {
        try {
            User user = new User();
            user.setUsername(usernameField.getText());
            user.setPassword(passwordField.getText());
            user.setRole((String) roleCombo.getSelectedItem());
            user.setEnabled(true);
            
            userService.createUser(user);
            refreshUserTable();
            clearInputFields();
            clearError();
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    void updateUser() {
        try {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow < 0) {
                showError("Please select a user to update");
                return;
            }
            long id = (long) tableModel.getValueAt(selectedRow, 0);
            
            User user = new User();
            user.setId(id);
            user.setUsername(usernameField.getText());
            user.setPassword(passwordField.getText());
            user.setRole((String) roleCombo.getSelectedItem());
            
            // Keep existing enabled status
            User existing = userService.getAllUsers().stream()
                    .filter(u -> u.getId() == id)
                    .findFirst().orElse(null);
            if (existing != null) {
                user.setEnabled(existing.isEnabled());
            }

            userService.updateUser(user);
            refreshUserTable();
            clearInputFields();
            clearError();
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    void deleteUser() {
        try {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow < 0) {
                showError("Please select a user to delete");
                return;
            }
            long id = (long) tableModel.getValueAt(selectedRow, 0);
            userService.deleteUser(id);
            refreshUserTable();
            clearInputFields();
            clearError();
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    void disableUser() {
        try {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow < 0) {
                showError("Please select a user to disable");
                return;
            }
            long id = (long) tableModel.getValueAt(selectedRow, 0);
            userService.disableUser(id);
            refreshUserTable();
            clearError();
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    void enableUser() {
        try {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow < 0) {
                showError("Please select a user to enable");
                return;
            }
            long id = (long) tableModel.getValueAt(selectedRow, 0);
            userService.enableUser(id);
            refreshUserTable();
            clearError();
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    void viewUserReport() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow < 0) {
            showError("Please select a user to view their report");
            return;
        }
        long id = (long) tableModel.getValueAt(selectedRow, 0);
        String username = (String) tableModel.getValueAt(selectedRow, 1);
        try {
            String report = expenseService.generateReport(id);
            Object[] options = {"Save as PDF", "Close"};
            int choice = JOptionPane.showOptionDialog(
                this, 
                report, 
                "Expense Report - " + username, 
                JOptionPane.DEFAULT_OPTION, 
                JOptionPane.INFORMATION_MESSAGE, 
                null, 
                options, 
                options[1]
            );
            
            if (choice == 0) {
                List<Expense> expenses = expenseService.getExpensesByUserId(id);
                saveReportToPdf(expenses, username);
            }
            clearError();
        } catch (RuntimeException ex) {
            showError(ex.getMessage());
        }
    }

    private void saveReportToPdf(List<Expense> expenses, String username) {
        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
        fileChooser.setDialogTitle("Save Expense Report as PDF");
        fileChooser.setSelectedFile(new java.io.File("expense_report_" + username + ".pdf"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == javax.swing.JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            String path = fileToSave.getAbsolutePath();
            if (!path.toLowerCase().endsWith(".pdf")) {
                fileToSave = new java.io.File(path + ".pdf");
            }
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(fileToSave)) {
                com.personalexpense.util.PdfReportExporter.exportToPdf(expenses, username, fos);
                JOptionPane.showMessageDialog(this, "Report saved successfully as PDF!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                showError("Failed to save report: " + e.getMessage());
            }
        }
    }

    private void logout() {
        dispose();
        LoginView loginView = loginViewProvider.get();
        loginView.setVisible(true);
    }

    public void refreshUserTable() {
        tableModel.setRowCount(0);
        try {
            List<User> users = userService.getAllUsers();
            for (User user : users) {
                tableModel.addRow(new Object[]{
                    user.getId(),
                    user.getUsername(),
                    user.getRole(),
                    user.isEnabled()
                });
            }
        } catch (RuntimeException ex) {
            showError(ex.getMessage());
        }
    }

    public void clearUserSelection() {
        userTable.clearSelection();
    }

    private void populateFieldsFromSelectedRow() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            usernameField.setText((String) tableModel.getValueAt(selectedRow, 1));
            try {
                long userId = (long) tableModel.getValueAt(selectedRow, 0);
                User user = userService.getAllUsers().stream()
                        .filter(u -> u.getId() == userId)
                        .findFirst().orElse(null);
                if (user != null) {
                    passwordField.setText(user.getPassword());
                } else {
                    passwordField.setText("");
                }
            } catch (RuntimeException ex) {
                passwordField.setText("");
            }
            
            String role = (String) tableModel.getValueAt(selectedRow, 2);
            if (role != null) {
                roleCombo.setSelectedItem(role.toUpperCase());
            }
        }
    }

    private void clearInputFields() {
        usernameField.setText("");
        passwordField.setText("");
    }

    private void showError(String message) {
        errorLabel.setText(message);
    }

    private void clearError() {
        errorLabel.setText(" ");
    }
}
