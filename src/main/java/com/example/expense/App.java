package com.example.expense;

public class App {
    public static void main(String[] args) {
        // Initialize Guice injector and launch the Swing UI
        com.google.inject.Guice.createInjector(new AppModule()).getInstance(MainFrame.class).setVisible(true);
    }
}
