package com.personalexpense.repository.mysql;

import com.personalexpense.model.Category;
import com.personalexpense.model.Expense;
import com.personalexpense.model.User;
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
class MysqlExpenseRepositoryIT {

    @Container
    @SuppressWarnings("resource")
    public static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("expensesdb")
            .withUsername("user")
            .withPassword("userpwd")
            .withTmpFs(java.util.Collections.singletonMap("/var/lib/mysql", "rw"))
            .withStartupTimeout(Duration.ofMinutes(5))
            .withConnectTimeoutSeconds(300);

    private MysqlExpenseRepository repository;
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

        repository = new MysqlExpenseRepository(dataSource);
    }

    @Test
    void testSaveAndFindAll() {
        Expense e1 = new Expense(0L, "Lunch", 15.5, "2023-01-01");
        Expense saved = repository.save(e1);
        assertThat(saved.getId()).isGreaterThan(0L);

        List<Expense> expenses = repository.findAll();
        assertThat(expenses).hasSize(1);
        assertThat(expenses.get(0).getDescription()).isEqualTo("Lunch");
    }

    @Test
    void testFindById() {
        Expense e1 = new Expense(0L, "Lunch", 15.5, "2023-01-01");
        Expense saved = repository.save(e1);

        Expense found = repository.findById(saved.getId());
        assertThat(found).isNotNull();
        assertThat(found.getDescription()).isEqualTo("Lunch");
    }

    @Test
    void testUpdate() {
        Expense e1 = new Expense(0L, "Lunch", 15.5, "2023-01-01");
        Expense saved = repository.save(e1);

        saved.setDescription("Dinner");
        saved.setAmount(25.0);
        repository.update(saved);

        Expense found = repository.findById(saved.getId());
        assertThat(found.getDescription()).isEqualTo("Dinner");
        assertThat(found.getAmount()).isEqualTo(25.0);
    }

    @Test
    void testDelete() {
        Expense e1 = new Expense(0L, "Lunch", 15.5, "2023-01-01");
        Expense saved = repository.save(e1);

        repository.delete(saved.getId());
        List<Expense> expenses = repository.findAll();
        assertThat(expenses).isEmpty();
    }

    @Test
    void testCategoriesAssociation() {
        Expense e1 = new Expense(0L, "Lunch", 15.5, "2023-01-01");
        Expense savedExpense = repository.save(e1);

        MysqlCategoryRepository catRepo = new MysqlCategoryRepository(dataSource);
        Category c1 = new Category(0L, "Food");
        Category savedCat = catRepo.save(c1);

        repository.addCategoryToExpense(savedExpense.getId(), savedCat.getId());

        List<Category> categories = repository.findCategoriesForExpense(savedExpense.getId());
        assertThat(categories).hasSize(1);
        assertThat(categories.get(0).getName()).isEqualTo("Food");

        repository.removeCategoryFromExpense(savedExpense.getId(), savedCat.getId());
        categories = repository.findCategoriesForExpense(savedExpense.getId());
        assertThat(categories).isEmpty();
    }

    @Test
    void testFindByUserId() {
        MysqlUserRepository userRepo = new MysqlUserRepository(dataSource);
        User user = new User(0L, "testuser", "pwd", "USER", true);
        User savedUser = userRepo.save(user);

        Expense e1 = new Expense(0L, "Lunch", 15.5, "2023-01-01", savedUser.getId());
        Expense e2 = new Expense(0L, "Dinner", 25.0, "2023-01-02", savedUser.getId());
        Expense e3 = new Expense(0L, "Books", 50.0, "2023-01-03", 0L);

        repository.save(e1);
        repository.save(e2);
        repository.save(e3);

        List<Expense> userExpenses = repository.findByUserId(savedUser.getId());
        assertThat(userExpenses).hasSize(2);
        assertThat(userExpenses).extracting(Expense::getDescription).containsExactlyInAnyOrder("Lunch", "Dinner");
    }
}
