package com.personalexpense.service;

import com.personalexpense.model.Category;
import com.personalexpense.model.Expense;
import com.personalexpense.repository.CategoryRepository;
import com.personalexpense.repository.ExpenseRepository;

import javax.inject.Inject;
import java.util.List;

public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;

    @Inject
    public ExpenseService(ExpenseRepository expenseRepository, CategoryRepository categoryRepository) {
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public Expense getExpenseById(long id) {
        Expense expense = expenseRepository.findById(id);
        if (expense == null) {
            throw new IllegalArgumentException("Expense not found with id: " + id);
        }
        return expense;
    }

    public Expense addExpense(Expense expense) {
        validateExpense(expense);
        return expenseRepository.save(expense);
    }

    public Expense updateExpense(Expense expense) {
        if (expense.getId() <= 0) {
            throw new IllegalArgumentException("Expense id must be greater than 0");
        }
        validateExpense(expense);
        return expenseRepository.update(expense);
    }

    public void deleteExpense(long id) {
        expenseRepository.delete(id);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category addCategory(Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }
        return categoryRepository.save(category);
    }

    public Category updateCategory(Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }
        return categoryRepository.update(category);
    }

    public void deleteCategory(long id) {
        categoryRepository.delete(id);
    }

    public void addCategoryToExpense(long expenseId, long categoryId) {
        expenseRepository.addCategoryToExpense(expenseId, categoryId);
    }

    public void removeCategoryFromExpense(long expenseId, long categoryId) {
        expenseRepository.removeCategoryFromExpense(expenseId, categoryId);
    }

    private void validateExpense(Expense expense) {
        if (expense.getDescription() == null || expense.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Expense description cannot be null or empty");
        }
        if (expense.getAmount() <= 0) {
            throw new IllegalArgumentException("Expense amount must be greater than 0");
        }
        if (expense.getDate() == null || expense.getDate().trim().isEmpty()) {
            throw new IllegalArgumentException("Expense date cannot be null or empty");
        }
    }
}
