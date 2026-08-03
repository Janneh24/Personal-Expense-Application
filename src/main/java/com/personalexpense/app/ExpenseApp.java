package com.personalexpense.app;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.personalexpense.module.ExpenseModule;
import com.personalexpense.view.LoginView;

import javax.sql.DataSource;
import javax.swing.SwingUtilities;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExpenseApp {

    private static final Logger LOGGER = Logger.getLogger(ExpenseApp.class.getName());

    private ExpenseApp() {
        // Utility class
    }

    public static void main(String[] args) {
        String host = System.getProperty("db.host", "localhost");
        int port = Integer.parseInt(System.getProperty("db.port", "3306"));
        String database = System.getProperty("db.name", "expensesdb");
        String user = System.getProperty("db.user", "user");
        String password = System.getProperty("db.password", "");

        Injector injector = Guice.createInjector(
            new ExpenseModule(host, port, database, user, password)
        );

        // Auto-initialize database schema if it doesn't exist
        DataSource dataSource = injector.getInstance(DataSource.class);
        try {
            initializeDatabase(dataSource);
        } catch (SQLException | IOException e) {
            LOGGER.log(Level.WARNING, "Database auto-initialization failed", e);
        }

        SwingUtilities.invokeLater(() -> {
            LoginView view = injector.getInstance(LoginView.class);
            view.setVisible(true);
        });
    }

    private static void initializeDatabase(DataSource dataSource) throws SQLException, IOException {
        try (Connection conn = dataSource.getConnection()) {
            LOGGER.info("Checking and initializing database schema from init.sql...");
            try (InputStream is = ExpenseApp.class.getResourceAsStream("/db/init.sql")) {
                if (is == null) {
                    throw new FileNotFoundException("init.sql not found in resources");
                }
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.trim().startsWith("--") || line.trim().isEmpty()) {
                            continue;
                        }
                        sb.append(line).append("\n");
                    }

                    String[] statements = sb.toString().split(";");
                    try (Statement stmt = conn.createStatement()) {
                        for (String sql : statements) {
                            if (!sql.trim().isEmpty()) {
                                stmt.execute(sql.trim());
                            }
                        }
                    }
                }
            }
            LOGGER.info("Database initialization completed successfully.");
        }
    }
}
