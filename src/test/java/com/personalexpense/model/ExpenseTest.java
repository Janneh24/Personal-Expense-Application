package com.personalexpense.model;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExpenseTest {
    @Test
    void testExpenseProperties() {
        Expense expense = new Expense(1L, "Test", 10.5, "2023-01-01", 42L);
        assertThat(expense.getId()).isEqualTo(1L);
        assertThat(expense.getDescription()).isEqualTo("Test");
        assertThat(expense.getAmount()).isEqualTo(10.5);
        assertThat(expense.getDate()).isEqualTo("2023-01-01");
        assertThat(expense.getUserId()).isEqualTo(42L);
        assertThat(expense.getCategories()).isEmpty();

        expense.setUserId(99L);
        assertThat(expense.getUserId()).isEqualTo(99L);
    }

    @Test
    void testAddRemoveCategory() {
        Expense expense = new Expense();
        Category category = new Category(1L, "Food");
        expense.addCategory(category);
        assertThat(expense.getCategories()).containsExactly(category);

        expense.removeCategory(category);
        assertThat(expense.getCategories()).isEmpty();
    }

    @Test
    void testGetCategoriesIsUnmodifiable() {
        Expense expense = new Expense();
        Category category = new Category(1L, "Food");
        List<Category> categories = expense.getCategories();
        assertThatThrownBy(() -> categories.add(category)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void testEqualsAndHashCode() {
        Expense e1 = new Expense(1L, "Test", 10.5, "2023-01-01", 42L);
        Expense e2 = new Expense(1L, "Test", 10.5, "2023-01-01", 42L);
        Expense e3 = new Expense(1L, "Test", 10.5, "2023-01-01", 99L);
        Expense e4 = new Expense(2L, "Test2", 20.0, "2023-01-02", 42L);

        assertThat(e1).isEqualTo(e2);
        assertThat(e1).isNotEqualTo(e3);
        assertThat(e1).isNotEqualTo(e4);
        assertThat(e1).isNotEqualTo(null);
        assertThat(e1).isNotEqualTo(new Object());
        assertThat(e1.hashCode()).isEqualTo(e2.hashCode());
        assertThat(e1.hashCode()).isNotEqualTo(e3.hashCode());
        assertThat(e1.hashCode()).isNotEqualTo(e4.hashCode());
    }

    @Test
    void testToString() {
        Expense expense = new Expense(1L, "Test", 10.5, "2023-01-01", 42L);
        assertThat(expense.toString()).contains("1", "Test", "10.5", "2023-01-01", "42");
    }
}
