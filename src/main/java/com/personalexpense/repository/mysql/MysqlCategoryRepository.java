package com.personalexpense.repository.mysql;

import com.personalexpense.model.Category;
import com.personalexpense.repository.CategoryRepository;
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

public class MysqlCategoryRepository implements CategoryRepository {

    private final DataSource dataSource;

    @Inject
    public MysqlCategoryRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Category> findAll() {
        String sql = "SELECT * FROM categories";
        List<Category> categories = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                categories.add(mapRowToCategory(rs));
            }
        } catch (SQLException e) {
            throw new RepositoryException("Failed to find all categories", e);
        }
        return categories;
    }

    @Override
    public Category findById(long id) {
        String sql = "SELECT * FROM categories WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToCategory(rs);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Failed to find category with id " + id, e);
        }
        return null;
    }

    @Override
    public Category save(Category category) {
        String sql = "INSERT INTO categories (name) VALUES (?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, category.getName());
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    category.setId(generatedKeys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Failed to save category", e);
        }
        return category;
    }

    @Override
    public Category update(Category category) {
        String sql = "UPDATE categories SET name = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, category.getName());
            stmt.setLong(2, category.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Failed to update category with id " + category.getId(), e);
        }
        return category;
    }

    @Override
    public void delete(long id) {
        String sql = "DELETE FROM categories WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Failed to delete category with id " + id, e);
        }
    }

    private Category mapRowToCategory(ResultSet rs) throws SQLException {
        return new Category(rs.getLong("id"), rs.getString("name"));
    }
}
