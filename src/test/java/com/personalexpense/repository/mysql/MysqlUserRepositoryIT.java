package com.personalexpense.repository.mysql;

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
class MysqlUserRepositoryIT {

    @Container
    public static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("expensesdb")
            .withUsername("user")
            .withPassword("userpwd")
            .withTmpFs(java.util.Collections.singletonMap("/var/lib/mysql", "rw"))
            .withStartupTimeout(Duration.ofMinutes(5));

    private MysqlUserRepository repository;
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
            stmt.execute("DELETE FROM users");
        }

        repository = new MysqlUserRepository(dataSource);
    }

    @Test
    void testSaveAndFindAll() {
        User u = new User(0L, "user1", "pwd1", "USER", true);
        User saved = repository.save(u);
        assertThat(saved.getId()).isGreaterThan(0L);

        List<User> users = repository.findAll();
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getUsername()).isEqualTo("user1");
        assertThat(users.get(0).getRole()).isEqualTo("USER");
        assertThat(users.get(0).isEnabled()).isTrue();
    }

    @Test
    void testFindById() {
        User u = new User(0L, "user1", "pwd1", "USER", true);
        User saved = repository.save(u);

        User found = repository.findById(saved.getId());
        assertThat(found).isNotNull();
        assertThat(found.getUsername()).isEqualTo("user1");
    }

    @Test
    void testFindByUsername() {
        User u = new User(0L, "user1", "pwd1", "USER", true);
        repository.save(u);

        User found = repository.findByUsername("user1");
        assertThat(found).isNotNull();
        assertThat(found.getPassword()).isEqualTo("pwd1");

        User notFound = repository.findByUsername("nonexistent");
        assertThat(notFound).isNull();
    }

    @Test
    void testUpdate() {
        User u = new User(0L, "user1", "pwd1", "USER", true);
        User saved = repository.save(u);

        saved.setUsername("updateduser");
        saved.setRole("ADMIN");
        saved.setEnabled(false);
        repository.update(saved);

        User found = repository.findById(saved.getId());
        assertThat(found.getUsername()).isEqualTo("updateduser");
        assertThat(found.getRole()).isEqualTo("ADMIN");
        assertThat(found.isEnabled()).isFalse();
    }

    @Test
    void testDelete() {
        User u = new User(0L, "user1", "pwd1", "USER", true);
        User saved = repository.save(u);

        repository.delete(saved.getId());
        List<User> users = repository.findAll();
        assertThat(users).isEmpty();
    }
}
