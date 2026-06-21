package com.personalexpense.repository.mysql;

import com.personalexpense.model.Category;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.Statement;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
class MysqlCategoryRepositoryIT {

    @Container
    @SuppressWarnings("resource")
    public static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("expensesdb")
            .withUsername("user")
            .withPassword("userpwd")
            .withTmpFs(java.util.Collections.singletonMap("/var/lib/mysql", "rw"))
            .withStartupTimeout(Duration.ofMinutes(5))
            .withConnectTimeoutSeconds(300);

    private MysqlCategoryRepository repository;
    private MysqlDataSource dataSource;

    @BeforeEach
    void setUp() throws Exception {
        dataSource = new MysqlDataSource();
        dataSource.setURL(mysql.getJdbcUrl());
        dataSource.setUser(mysql.getUsername());
        dataSource.setPassword(mysql.getPassword());

        String initSql = new String(Files.readAllBytes(Paths.get("src/main/resources/db/init.sql")));
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            String[] commands = initSql.split(";");
            for (String command : commands) {
                if (!command.trim().isEmpty()) {
                    stmt.execute(command);
                }
            }
            stmt.execute("DELETE FROM expense_category");
            stmt.execute("DELETE FROM expenses");
            stmt.execute("DELETE FROM categories");
        }

        repository = new MysqlCategoryRepository(dataSource);
    }

    @Test
    void testSaveAndFindAll() {
        Category c1 = new Category(0L, "Food");
        Category saved = repository.save(c1);
        assertThat(saved.getId()).isGreaterThan(0L);

        List<Category> categories = repository.findAll();
        assertThat(categories).hasSize(1);
        assertThat(categories.get(0).getName()).isEqualTo("Food");
    }

    @Test
    void testFindById() {
        Category c1 = new Category(0L, "Food");
        Category saved = repository.save(c1);

        Category found = repository.findById(saved.getId());
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Food");
    }

    @Test
    void testUpdate() {
        Category c1 = new Category(0L, "Food");
        Category saved = repository.save(c1);

        saved.setName("Travel");
        repository.update(saved);

        Category found = repository.findById(saved.getId());
        assertThat(found.getName()).isEqualTo("Travel");
    }

    @Test
    void testDelete() {
        Category c1 = new Category(0L, "Food");
        Category saved = repository.save(c1);

        repository.delete(saved.getId());
        List<Category> categories = repository.findAll();
        assertThat(categories).isEmpty();
    }
}
