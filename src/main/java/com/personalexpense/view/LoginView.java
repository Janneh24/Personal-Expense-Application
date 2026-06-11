package com.personalexpense.view;

import com.personalexpense.model.User;
import com.personalexpense.service.UserService;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

public class LoginView extends JFrame {

    private static final long serialVersionUID = 1L;

    private final transient UserService userService;
    private final transient Provider<ExpenseSwingView> expenseViewProvider;
    private final transient Provider<AdminView> adminViewProvider;

    private transient JTextField usernameField;
    private transient JPasswordField passwordField;
    private transient JRadioButton userRadio;
    private transient JRadioButton adminRadio;
    private transient JButton loginButton;
    private transient JLabel errorLabel;

    @Inject
    public LoginView(UserService userService, 
                     Provider<ExpenseSwingView> expenseViewProvider, 
                     Provider<AdminView> adminViewProvider) {
        this.userService = userService;
        this.expenseViewProvider = expenseViewProvider;
        this.adminViewProvider = adminViewProvider;
        
        initComponents();
        layoutComponents();
        attachListeners();
        pack();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("Login - Personal Expense Tracker");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        usernameField = new JTextField(15);
        usernameField.setName("usernameField");

        passwordField = new JPasswordField(15);
        passwordField.setName("passwordField");

        userRadio = new JRadioButton("User", true);
        userRadio.setName("userRadio");

        adminRadio = new JRadioButton("Admin", false);
        adminRadio.setName("adminRadio");

        ButtonGroup roleGroup = new ButtonGroup();
        roleGroup.add(userRadio);
        roleGroup.add(adminRadio);

        loginButton = new JButton("Login");
        loginButton.setName("loginButton");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBackground(new Color(64, 120, 240));
        loginButton.setForeground(Color.WHITE);

        errorLabel = new JLabel(" ", JLabel.CENTER);
        errorLabel.setName("errorLabel");
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);
        
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);

        formPanel.add(new JLabel("Role:"));
        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rolePanel.add(userRadio);
        rolePanel.add(adminRadio);
        formPanel.add(rolePanel);

        formPanel.add(new JLabel());
        formPanel.add(loginButton);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.add(formPanel);
        mainPanel.add(centerWrapper, BorderLayout.CENTER);
        mainPanel.add(errorLabel, BorderLayout.SOUTH);

        // Padding
        JPanel paddingLeft = new JPanel();
        JPanel paddingRight = new JPanel();
        JPanel paddingTop = new JPanel();
        JLabel titleLabel = new JLabel("Log In to Your Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        paddingTop.add(titleLabel);

        add(paddingTop, BorderLayout.NORTH);
        add(paddingLeft, BorderLayout.WEST);
        add(paddingRight, BorderLayout.EAST);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void attachListeners() {
        loginButton.addActionListener(e -> performLogin());
    }

    private void performLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        boolean isAdminSelected = adminRadio.isSelected();
        
        try {
            User user = userService.authenticate(username, password);
            String selectedRole = isAdminSelected ? "ADMIN" : "USER";
            
            if (!user.getRole().equalsIgnoreCase(selectedRole)) {
                showError("Role mismatch. You selected " + selectedRole + " but you are registered as " + user.getRole());
                return;
            }

            // Successfully authenticated
            dispose();
            if (user.getRole().equalsIgnoreCase("ADMIN")) {
                AdminView adminView = adminViewProvider.get();
                adminView.setVisible(true);
            } else {
                ExpenseSwingView expenseView = expenseViewProvider.get();
                expenseView.setCurrentUser(user);
                expenseView.setVisible(true);
            }
            clearError();
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        } catch (Exception ex) {
            showError("System/DB error: " + (ex.getMessage() != null ? ex.getMessage() : ex.toString()));
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
    }

    private void clearError() {
        errorLabel.setText(" ");
    }
}
