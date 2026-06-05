package com.personalexpense.app;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.personalexpense.module.ExpenseModule;
import com.personalexpense.view.ExpenseSwingView;
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

        SwingUtilities.invokeLater(() -> {
            ExpenseSwingView view = injector.getInstance(ExpenseSwingView.class);
            view.setVisible(true);
        });
    }
}
