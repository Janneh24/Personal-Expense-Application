package com.personalexpense.app;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.personalexpense.module.ExpenseModule;
import com.personalexpense.view.LoginView;
import javax.swing.SwingUtilities;

public class ExpenseApp {
    private ExpenseApp() {
        // Utility class
    }

    public static void main(String[] args) {
        String host = System.getProperty("db.host", "localhost");
        int port = Integer.parseInt(System.getProperty("db.port", "3306"));
        String database = System.getProperty("db.name", "expensesdb");
        String user = System.getProperty("db.user", "user");
        String password = System.getProperty("db.password", "userpwd");

        Injector injector = Guice.createInjector(
            new ExpenseModule(host, port, database, user, password)
        );

        // Auto-initialize database schema if it doesn't exist
        javax.sql.DataSource dataSource = injector.getInstance(javax.sql.DataSource.class);
        try {
            initializeDatabase(dataSource);
        } catch (Exception e) {
            System.err.println("Warning: Database auto-initialization failed: " + e.getMessage());
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            LoginView view = injector.getInstance(LoginView.class);
            view.setVisible(true);
        });
    }

    private static void initializeDatabase(javax.sql.DataSource dataSource) throws Exception {
        try (java.sql.Connection conn = dataSource.getConnection()) {
            System.out.println("Checking and initializing database schema from init.sql...");
            try (java.io.InputStream is = ExpenseApp.class.getResourceAsStream("/db/init.sql")) {
                if (is == null) {
                    throw new java.io.FileNotFoundException("init.sql not found in resources");
                }
                try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(is, java.nio.charset.StandardCharsets.UTF_8))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.trim().startsWith("--") || line.trim().isEmpty()) {
                            continue;
                        }
                        sb.append(line).append("\n");
                    }

                    String[] statements = sb.toString().split(";");
                    try (java.sql.Statement stmt = conn.createStatement()) {
                        for (String sql : statements) {
                            if (!sql.trim().isEmpty()) {
                                stmt.execute(sql.trim());
                            }
                        }
                    }
                }
            }
            System.out.println("Database initialization completed successfully.");
        }
    }
}
