package com.personalexpense.view;

import com.personalexpense.model.Category;
import com.personalexpense.model.Expense;
import com.personalexpense.service.ExpenseService;

import javax.inject.Inject;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
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

    private transient JTable expenseTable;
    private transient DefaultTableModel tableModel;
    private transient JTextField descriptionField;
    private transient JTextField amountField;
    private transient JTextField dateField;
    private transient JButton addButton;
    private transient JButton updateButton;
    private transient JButton deleteButton;
    private transient JComboBox<Category> categoryCombo;
    private transient JButton addCategoryButton;
    private transient JButton assignCategoryButton;
    private transient JTextField categoryNameField;
    private transient JList<Category> categoryList;
    private transient DefaultListModel<Category> categoryListModel;
    private transient JLabel errorLabel;

    @Inject
    public ExpenseSwingView(ExpenseService expenseService) {
        this.expenseService = expenseService;
        initComponents();
        layoutComponents();
        attachListeners();
        refreshExpenseTable();
        refreshCategoryCombo();
    }

    private void initComponents() {
        setTitle("Personal Expense Application");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);

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

        descriptionField = new JTextField(15);
        descriptionField.setName("descriptionField");

        amountField = new JTextField(10);
        amountField.setName("amountField");

        dateField = new JTextField(10);
        dateField.setName("dateField");

        addButton = new JButton("Add Expense");
        addButton.setName("addButton");

        updateButton = new JButton("Update Expense");
        updateButton.setName("updateButton");

        deleteButton = new JButton("Delete Expense");
        deleteButton.setName("deleteButton");

        categoryCombo = new JComboBox<>();
        categoryCombo.setName("categoryCombo");

        addCategoryButton = new JButton("Add Category");
        addCategoryButton.setName("addCategoryButton");

        assignCategoryButton = new JButton("Assign Category");
        assignCategoryButton.setName("assignCategoryButton");

        categoryNameField = new JTextField(15);
        categoryNameField.setName("categoryNameField");

        categoryListModel = new DefaultListModel<>();
        categoryList = new JList<>(categoryListModel);
        categoryList.setName("categoryList");

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
        inputPanel.add(new JLabel("Date:"));
        inputPanel.add(dateField);
        inputPanel.add(addButton);
        inputPanel.add(updateButton);
        inputPanel.add(deleteButton);

        JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        categoryPanel.add(new JLabel("Category Name:"));
        categoryPanel.add(categoryNameField);
        categoryPanel.add(addCategoryButton);
        categoryPanel.add(new JLabel("Category:"));
        categoryPanel.add(categoryCombo);
        categoryPanel.add(assignCategoryButton);

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.add(inputPanel);
        topPanel.add(categoryPanel);

        add(topPanel, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        JScrollPane categoryScrollPane = new JScrollPane(categoryList);
        bottomPanel.add(new JLabel("Categories for selected expense:"), BorderLayout.NORTH);
        bottomPanel.add(categoryScrollPane, BorderLayout.CENTER);
        bottomPanel.add(errorLabel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void attachListeners() {
        addButton.addActionListener(e -> addExpense());
        updateButton.addActionListener(e -> updateExpense());
        deleteButton.addActionListener(e -> deleteExpense());
        addCategoryButton.addActionListener(e -> addCategory());
        assignCategoryButton.addActionListener(e -> assignCategory());

        expenseTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                refreshCategoryList();
            }
        });
    }

    private void addExpense() {
        try {
            Expense expense = new Expense();
            expense.setDescription(descriptionField.getText());
            expense.setAmount(parseAmount());
            expense.setDate(dateField.getText());
            expenseService.addExpense(expense);
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
            expenseService.updateExpense(expense);
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
        tableModel.setRowCount(0);
        try {
            List<Expense> expenses = expenseService.getAllExpenses();
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
        try {
            List<Category> categories = expenseService.getAllCategories();
            for (Category category : categories) {
                categoryCombo.addItem(category);
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

    private void clearInputFields() {
        descriptionField.setText("");
        amountField.setText("");
        dateField.setText("");
    }

    private void showError(String message) {
        errorLabel.setText(message);
    }

    private void clearError() {
        errorLabel.setText(" ");
    }
}
