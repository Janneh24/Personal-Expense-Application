package com.personalexpense.repository.mysql;

import com.personalexpense.model.Category;
import com.personalexpense.model.Expense;
import com.personalexpense.repository.ExpenseRepository;
import com.personalexpense.repository.RepositoryException;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MysqlExpenseRepository implements ExpenseRepository {

    private final DataSource dataSource;

    @Inject
    public MysqlExpenseRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Expense> findAll() {
        String sql = "SELECT * FROM expenses";
        List<Expense> expenses = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Expense expense = mapRowToExpense(rs);
                expense.setCategories(findCategoriesForExpense(expense.getId()));
                expenses.add(expense);
            }
        } catch (SQLException e) {
            throw new RepositoryException("Failed to find all expenses", e);
        }
        return expenses;
    }

    @Override
    public Expense findById(long id) {
        String sql = "SELECT * FROM expenses WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Expense expense = mapRowToExpense(rs);
                    expense.setCategories(findCategoriesForExpense(expense.getId()));
                    return expense;
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Failed to find expense with id " + id, e);
        }
        return null;
    }

    @Override
    public Expense save(Expense expense) {
        String sql = "INSERT INTO expenses (description, amount, date, user_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, expense.getDescription());
            stmt.setDouble(2, expense.getAmount());
            stmt.setString(3, expense.getDate());
            if (expense.getUserId() > 0) {
                stmt.setLong(4, expense.getUserId());
            } else {
                stmt.setNull(4, java.sql.Types.BIGINT);
            }
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    expense.setId(generatedKeys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Failed to save expense", e);
        }
        return expense;
    }

    @Override
    public Expense update(Expense expense) {
        String sql = "UPDATE expenses SET description = ?, amount = ?, date = ?, user_id = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, expense.getDescription());
            stmt.setDouble(2, expense.getAmount());
            stmt.setString(3, expense.getDate());
            if (expense.getUserId() > 0) {
                stmt.setLong(4, expense.getUserId());
            } else {
                stmt.setNull(4, java.sql.Types.BIGINT);
            }
            stmt.setLong(5, expense.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Failed to update expense with id " + expense.getId(), e);
        }
        return expense;
    }

    @Override
    public void delete(long id) {
        String deleteCategoriesSql = "DELETE FROM expense_category WHERE expense_id = ?";
        String deleteExpenseSql = "DELETE FROM expenses WHERE id = ?";
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(deleteCategoriesSql)) {
                stmt.setLong(1, id);
                stmt.executeUpdate();
            }
            try (PreparedStatement stmt = conn.prepareStatement(deleteExpenseSql)) {
                stmt.setLong(1, id);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RepositoryException("Failed to delete expense with id " + id, e);
        }
    }

    @Override
    public void addCategoryToExpense(long expenseId, long categoryId) {
        String sql = "INSERT INTO expense_category (expense_id, category_id) VALUES (?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, expenseId);
            stmt.setLong(2, categoryId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException(
                "Failed to add category " + categoryId + " to expense " + expenseId, e);
        }
    }

    @Override
    public void removeCategoryFromExpense(long expenseId, long categoryId) {
        String sql = "DELETE FROM expense_category WHERE expense_id = ? AND category_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, expenseId);
            stmt.setLong(2, categoryId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException(
                "Failed to remove category " + categoryId + " from expense " + expenseId, e);
        }
    }

    @Override
    public List<Category> findCategoriesForExpense(long expenseId) {
        String sql = "SELECT c.id, c.name FROM categories c "
            + "JOIN expense_category ec ON c.id = ec.category_id "
            + "WHERE ec.expense_id = ?";
        List<Category> categories = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, expenseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    categories.add(new Category(rs.getLong("id"), rs.getString("name")));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException(
                "Failed to find categories for expense " + expenseId, e);
        }
        return categories;
    }

    @Override
    public List<Expense> findByUserId(long userId) {
        String sql = "SELECT * FROM expenses WHERE user_id = ?";
        List<Expense> expenses = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Expense expense = mapRowToExpense(rs);
                    expense.setCategories(findCategoriesForExpense(expense.getId()));
                    expenses.add(expense);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Failed to find expenses for user " + userId, e);
        }
        return expenses;
    }

    private Expense mapRowToExpense(ResultSet rs) throws SQLException {
        Expense expense = new Expense(
            rs.getLong("id"),
            rs.getString("description"),
            rs.getDouble("amount"),
            rs.getString("date")
        );
        expense.setUserId(rs.getLong("user_id"));
        return expense;
    }
}
