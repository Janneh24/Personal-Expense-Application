package com.personalexpense.service;

import com.personalexpense.model.Category;
import com.personalexpense.model.Expense;
import com.personalexpense.repository.CategoryRepository;
import com.personalexpense.repository.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ExpenseService expenseService;

    @Test
    void testGetAllExpenses() {
        Expense e1 = new Expense(1L, "Desc", 10.0, "2023-01-01");
        when(expenseRepository.findAll()).thenReturn(Arrays.asList(e1));
        List<Expense> expenses = expenseService.getAllExpenses();
        assertThat(expenses).containsExactly(e1);
        verify(expenseRepository).findAll();
    }

    @Test
    void testGetExpenseById() {
        Expense e1 = new Expense(1L, "Desc", 10.0, "2023-01-01");
        when(expenseRepository.findById(1L)).thenReturn(e1);
        Expense found = expenseService.getExpenseById(1L);
        assertThat(found).isEqualTo(e1);
    }

    @Test
    void testGetExpenseByIdNotFound() {
        when(expenseRepository.findById(1L)).thenReturn(null);
        assertThatThrownBy(() -> expenseService.getExpenseById(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void testAddExpenseValid() {
        Expense e1 = new Expense(0L, "Desc", 10.0, "2023-01-01");
        Expense saved = new Expense(1L, "Desc", 10.0, "2023-01-01");
        when(expenseRepository.save(any(Expense.class))).thenReturn(saved);

        Expense result = expenseService.addExpense(e1);
        assertThat(result.getId()).isEqualTo(1L);
        verify(expenseRepository).save(e1);
    }

    @Test
    void testAddExpenseInvalidDescription() {
        Expense e1 = new Expense(0L, "", 10.0, "2023-01-01");
        assertThatThrownBy(() -> expenseService.addExpense(e1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testAddExpenseInvalidAmount() {
        Expense e1 = new Expense(0L, "Desc", -5.0, "2023-01-01");
        assertThatThrownBy(() -> expenseService.addExpense(e1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testAddExpenseInvalidAmountZero() {
        Expense e1 = new Expense(0L, "Desc", 0.0, "2023-01-01");
        assertThatThrownBy(() -> expenseService.addExpense(e1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testAddExpenseInvalidDate() {
        Expense e1 = new Expense(0L, "Desc", 10.0, "");
        assertThatThrownBy(() -> expenseService.addExpense(e1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testUpdateExpenseValid() {
        Expense e1 = new Expense(1L, "Desc", 10.0, "2023-01-01");
        when(expenseRepository.update(any(Expense.class))).thenReturn(e1);

        Expense result = expenseService.updateExpense(e1);
        assertThat(result).isEqualTo(e1);
        verify(expenseRepository).update(e1);
    }

    @Test
    void testUpdateExpenseInvalidId() {
        Expense e1 = new Expense(0L, "Desc", 10.0, "2023-01-01");
        assertThatThrownBy(() -> expenseService.updateExpense(e1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testUpdateExpenseInvalidData() {
        Expense e1 = new Expense(1L, "", 10.0, "2023-01-01");
        assertThatThrownBy(() -> expenseService.updateExpense(e1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testDeleteExpense() {
        expenseService.deleteExpense(1L);
        verify(expenseRepository).delete(1L);
    }

    @Test
    void testGetAllCategories() {
        Category c1 = new Category(1L, "Food");
        when(categoryRepository.findAll()).thenReturn(Arrays.asList(c1));
        List<Category> categories = expenseService.getAllCategories();
        assertThat(categories).containsExactly(c1);
    }

    @Test
    void testAddCategoryValid() {
        Category c1 = new Category(0L, "Food");
        Category saved = new Category(1L, "Food");
        when(categoryRepository.save(any(Category.class))).thenReturn(saved);

        Category result = expenseService.addCategory(c1);
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void testAddCategoryInvalid() {
        Category c1 = new Category(0L, "");
        assertThatThrownBy(() -> expenseService.addCategory(c1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testUpdateCategory() {
        Category c1 = new Category(1L, "Food");
        when(categoryRepository.update(any(Category.class))).thenReturn(c1);
        Category result = expenseService.updateCategory(c1);
        assertThat(result).isEqualTo(c1);
    }

    @Test
    void testDeleteCategory() {
        expenseService.deleteCategory(1L);
        verify(categoryRepository).delete(1L);
    }

    @Test
    void testAddCategoryToExpense() {
        expenseService.addCategoryToExpense(1L, 2L);
        verify(expenseRepository).addCategoryToExpense(1L, 2L);
    }

    @Test
    void testRemoveCategoryFromExpense() {
        expenseService.removeCategoryFromExpense(1L, 2L);
        verify(expenseRepository).removeCategoryFromExpense(1L, 2L);
    }
}
