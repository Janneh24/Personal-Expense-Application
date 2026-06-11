package com.personalexpense.view;

import com.personalexpense.model.Category;
import com.personalexpense.model.Expense;
import com.personalexpense.model.User;
import com.personalexpense.service.ExpenseService;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
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

public class ExpenseSwingView extends JFrame {

    private static final long serialVersionUID = 1L;

    private final transient ExpenseService expenseService;
    private final transient Provider<LoginView> loginViewProvider;
    private transient User currentUser;

    private transient JTable expenseTable;
    private transient DefaultTableModel tableModel;
    private transient JTextField descriptionField;
    private transient JTextField amountField;
    private transient JTextField dateField;
    private transient JButton addButton;
    private transient JButton updateButton;
    private transient JButton deleteButton;
    private transient JButton reportButton;
    private transient JButton logoutButton;
    private transient JComboBox<Category> categoryCombo;
    private transient JButton addCategoryButton;
    private transient JButton assignCategoryButton;
    private transient JTextField categoryNameField;
    private transient JList<Category> categoryList;
    private transient DefaultListModel<Category> categoryListModel;
    private transient JLabel errorLabel;

    @Inject
    public ExpenseSwingView(ExpenseService expenseService, Provider<LoginView> loginViewProvider) {
        this.expenseService = expenseService;
        this.loginViewProvider = loginViewProvider;
        initComponents();
        layoutComponents();
        attachListeners();
        refreshExpenseTable();
        refreshCategoryCombo();
    }

    public ExpenseSwingView(ExpenseService expenseService) {
        this(expenseService, null);
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            setTitle("Personal Expense Application - " + user.getUsername());
        } else {
            setTitle("Personal Expense Application");
        }
        refreshExpenseTable();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    private void initComponents() {
        setTitle("Personal Expense Application");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(880, 600);

        tableModel = new DefaultTableModel(
            new Object[]{"ID", "Description", "Amount", "Date"}, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        expenseTable = new JTable(tableModel);
        expenseTable.setName("expenseTable");
        expenseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        descriptionField = new JTextField(12);
        descriptionField.setName("descriptionField");

        amountField = new JTextField(8);
        amountField.setName("amountField");

        dateField = new JTextField(8);
        dateField.setName("dateField");

        addButton = new JButton("Add Expense");
        addButton.setName("addButton");

        updateButton = new JButton("Update Expense");
        updateButton.setName("updateButton");

        deleteButton = new JButton("Delete Expense");
        deleteButton.setName("deleteButton");

        reportButton = new JButton("Generate Report");
        reportButton.setName("reportButton");

        logoutButton = new JButton("Logout");
        logoutButton.setName("logoutButton");

        categoryCombo = new JComboBox<>();
        categoryCombo.setName("categoryCombo");
        categoryCombo.setRenderer(new javax.swing.ListCellRenderer<Category>() {
            private final javax.swing.DefaultListCellRenderer defaultRenderer = new javax.swing.DefaultListCellRenderer();
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<? extends Category> list, Category value, int index, boolean isSelected, boolean cellHasFocus) {
                java.awt.Component c = defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (c instanceof javax.swing.JLabel && value != null) {
                    ((javax.swing.JLabel) c).setText(value.getName());
                }
                return c;
            }
        });

        addCategoryButton = new JButton("Add Category");
        addCategoryButton.setName("addCategoryButton");

        assignCategoryButton = new JButton("Assign Category");
        assignCategoryButton.setName("assignCategoryButton");

        categoryNameField = new JTextField(12);
        categoryNameField.setName("categoryNameField");

        categoryListModel = new DefaultListModel<>();
        categoryList = new JList<>(categoryListModel);
        categoryList.setName("categoryList");
        categoryList.setCellRenderer(new javax.swing.ListCellRenderer<Category>() {
            private final javax.swing.DefaultListCellRenderer defaultRenderer = new javax.swing.DefaultListCellRenderer();
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<? extends Category> list, Category value, int index, boolean isSelected, boolean cellHasFocus) {
                java.awt.Component c = defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (c instanceof javax.swing.JLabel && value != null) {
                    ((javax.swing.JLabel) c).setText(value.getName());
                }
                return c;
            }
        });

        errorLabel = new JLabel(" ");
        errorLabel.setName("errorLabel");
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(5, 5));

        JScrollPane tableScrollPane = new JScrollPane(expenseTable);
        add(tableScrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        inputPanel.add(new JLabel("Description:"));
        inputPanel.add(descriptionField);
        inputPanel.add(new JLabel("Amount:"));
        inputPanel.add(amountField);
        inputPanel.add(new JLabel("Date (yyyy-MM-dd):"));
        inputPanel.add(dateField);
        inputPanel.add(new JLabel("Category:"));
        inputPanel.add(categoryCombo);
        inputPanel.add(addButton);
        inputPanel.add(updateButton);
        inputPanel.add(deleteButton);
        inputPanel.add(reportButton);
        inputPanel.add(logoutButton);

        JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        categoryPanel.add(new JLabel("Create Custom Category:"));
        categoryPanel.add(categoryNameField);
        categoryPanel.add(addCategoryButton);
        categoryPanel.add(assignCategoryButton); // Keep for backwards compatibility

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.add(inputPanel);
        topPanel.add(categoryPanel);

        add(topPanel, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        JScrollPane categoryScrollPane = new JScrollPane(categoryList);
        bottomPanel.add(new JLabel("Active Categories on Selected Expense:"), BorderLayout.NORTH);
        bottomPanel.add(categoryScrollPane, BorderLayout.CENTER);
        bottomPanel.add(errorLabel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void attachListeners() {
        addButton.addActionListener(e -> addExpense());
        updateButton.addActionListener(e -> updateExpense());
        deleteButton.addActionListener(e -> deleteExpense());
        reportButton.addActionListener(e -> generateReport());
        addCategoryButton.addActionListener(e -> addCategory());
        assignCategoryButton.addActionListener(e -> assignCategory());
        logoutButton.addActionListener(e -> logout());

        expenseTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                refreshCategoryList();
                populateFieldsFromSelectedRow();
            }
        });
    }

    private void addExpense() {
        try {
            Expense expense = new Expense();
            expense.setDescription(descriptionField.getText());
            expense.setAmount(parseAmount());
            expense.setDate(dateField.getText());
            if (currentUser != null) {
                expense.setUserId(currentUser.getId());
            }
            Expense saved = expenseService.addExpense(expense);

            Category selectedCategory = (Category) categoryCombo.getSelectedItem();
            if (selectedCategory != null && selectedCategory.getId() > 0) {
                expenseService.addCategoryToExpense(saved.getId(), selectedCategory.getId());
            }

            refreshExpenseTable();
            clearInputFields();
            clearError();
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void updateExpense() {
        try {
            int selectedRow = expenseTable.getSelectedRow();
            if (selectedRow < 0) {
                showError("Please select an expense to update");
                return;
            }
            long id = (long) tableModel.getValueAt(selectedRow, 0);
            Expense expense = new Expense();
            expense.setId(id);
            expense.setDescription(descriptionField.getText());
            expense.setAmount(parseAmount());
            expense.setDate(dateField.getText());
            if (currentUser != null) {
                expense.setUserId(currentUser.getId());
            }
            expenseService.updateExpense(expense);

            Expense existing = expenseService.getExpenseById(id);
            for (Category cat : existing.getCategories()) {
                expenseService.removeCategoryFromExpense(id, cat.getId());
            }

            Category selectedCategory = (Category) categoryCombo.getSelectedItem();
            if (selectedCategory != null && selectedCategory.getId() > 0) {
                expenseService.addCategoryToExpense(id, selectedCategory.getId());
            }

            refreshExpenseTable();
            clearInputFields();
            clearError();
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void deleteExpense() {
        try {
            int selectedRow = expenseTable.getSelectedRow();
            if (selectedRow < 0) {
                showError("Please select an expense to delete");
                return;
            }
            long id = (long) tableModel.getValueAt(selectedRow, 0);
            expenseService.deleteExpense(id);
            refreshExpenseTable();
            clearInputFields();
            clearError();
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void generateReport() {
        long userId = (currentUser != null) ? currentUser.getId() : 0L;
        try {
            String report = expenseService.generateReport(userId);
            Object[] options = {"Save as PDF", "Close"};
            int choice = JOptionPane.showOptionDialog(
                this, 
                report, 
                "Expense Report", 
                JOptionPane.DEFAULT_OPTION, 
                JOptionPane.INFORMATION_MESSAGE, 
                null, 
                options, 
                options[1]
            );
            
            if (choice == 0) {
                List<Expense> expenses;
                if (currentUser != null) {
                    expenses = expenseService.getExpensesByUserId(currentUser.getId());
                } else {
                    expenses = expenseService.getAllExpenses();
                }
                saveReportToPdf(expenses, currentUser != null ? currentUser.getUsername() : "user");
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

    private void addCategory() {
        try {
            Category category = new Category();
            category.setName(categoryNameField.getText());
            expenseService.addCategory(category);
            refreshCategoryCombo();
            categoryNameField.setText("");
            clearError();
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void assignCategory() {
        try {
            int selectedRow = expenseTable.getSelectedRow();
            if (selectedRow < 0) {
                showError("Please select an expense to assign a category");
                return;
            }
            Category selectedCategory = (Category) categoryCombo.getSelectedItem();
            if (selectedCategory == null) {
                showError("Please select a category to assign");
                return;
            }
            long expenseId = (long) tableModel.getValueAt(selectedRow, 0);
            expenseService.addCategoryToExpense(expenseId, selectedCategory.getId());
            refreshCategoryList();
            clearError();
        } catch (RuntimeException ex) {
            showError(ex.getMessage());
        }
    }

    public void refreshExpenseTable() {
        if (tableModel == null) return;
        tableModel.setRowCount(0);
        try {
            List<Expense> expenses;
            if (currentUser != null) {
                expenses = expenseService.getExpensesByUserId(currentUser.getId());
            } else {
                expenses = expenseService.getAllExpenses();
            }
            for (Expense expense : expenses) {
                tableModel.addRow(new Object[]{
                    expense.getId(),
                    expense.getDescription(),
                    expense.getAmount(),
                    expense.getDate()
                });
            }
        } catch (RuntimeException ex) {
            showError(ex.getMessage());
        }
    }

    private void refreshCategoryCombo() {
        categoryCombo.removeAllItems();
        categoryCombo.addItem(new Category(0L, "None"));
        try {
            List<Category> categories = expenseService.getAllCategories();
            for (Category category : categories) {
                if (category.getId() > 0) {
                    categoryCombo.addItem(category);
                }
            }
        } catch (RuntimeException ex) {
            showError(ex.getMessage());
        }
    }

    private void refreshCategoryList() {
        categoryListModel.clear();
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                long expenseId = (long) tableModel.getValueAt(selectedRow, 0);
                Expense expense = expenseService.getExpenseById(expenseId);
                for (Category category : expense.getCategories()) {
                    categoryListModel.addElement(category);
                }
            } catch (RuntimeException ex) {
                showError(ex.getMessage());
            }
        }
    }

    private double parseAmount() {
        try {
            return Double.parseDouble(amountField.getText());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Amount must be a valid number");
        }
    }

    private void logout() {
        if (loginViewProvider != null) {
            dispose();
            LoginView loginView = loginViewProvider.get();
            loginView.setVisible(true);
        }
    }

    private void populateFieldsFromSelectedRow() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow >= 0) {
            descriptionField.setText((String) tableModel.getValueAt(selectedRow, 1));
            amountField.setText(tableModel.getValueAt(selectedRow, 2).toString());
            dateField.setText((String) tableModel.getValueAt(selectedRow, 3));

            try {
                long expenseId = (long) tableModel.getValueAt(selectedRow, 0);
                Expense expense = expenseService.getExpenseById(expenseId);
                List<Category> categories = expense.getCategories();
                if (!categories.isEmpty()) {
                    Category cat = categories.get(0);
                    for (int i = 0; i < categoryCombo.getItemCount(); i++) {
                        Category item = categoryCombo.getItemAt(i);
                        if (item != null && item.getId() == cat.getId()) {
                            categoryCombo.setSelectedIndex(i);
                            break;
                        }
                    }
                } else {
                    categoryCombo.setSelectedIndex(0);
                }
            } catch (RuntimeException ex) {
                // ignore
            }
        }
    }

    private void clearInputFields() {
        descriptionField.setText("");
        amountField.setText("");
        dateField.setText("");
        if (categoryCombo.getItemCount() > 0) {
            categoryCombo.setSelectedIndex(0);
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
    }

    private void clearError() {
        errorLabel.setText(" ");
    }
}
