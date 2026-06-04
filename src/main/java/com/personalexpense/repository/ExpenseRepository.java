package com.personalexpense.repository;

import com.personalexpense.model.Category;
import com.personalexpense.model.Expense;
import java.util.List;

public interface ExpenseRepository {
    List<Expense> findAll();
    Expense findById(long id);
    Expense save(Expense expense);
    Expense update(Expense expense);
    void delete(long id);
    void addCategoryToExpense(long expenseId, long categoryId);
    void removeCategoryFromExpense(long expenseId, long categoryId);
    List<Category> findCategoriesForExpense(long expenseId);
}
