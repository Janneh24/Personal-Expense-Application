package com.personalexpense.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("all")
public class Expense {
    private long id;
    private String description;
    private double amount;
    private String date;
    private long userId;
    private List<Category> categories;

    public Expense() {
        this.categories = new ArrayList<>();
    }

    public Expense(long id, String description, double amount, String date) {
        this(id, description, amount, date, 0L);
    }

    public Expense(long id, String description, double amount, String date, long userId) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.userId = userId;
        this.categories = new ArrayList<>();
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }
    public List<Category> getCategories() { return Collections.unmodifiableList(categories); }
    public void setCategories(List<Category> categories) { this.categories = new ArrayList<>(categories); }
    public void addCategory(Category category) { this.categories.add(category); }
    public void removeCategory(Category category) { this.categories.remove(category); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expense expense = (Expense) o;
        return id == expense.id
            && Double.compare(expense.amount, amount) == 0
            && userId == expense.userId
            && Objects.equals(description, expense.description)
            && Objects.equals(date, expense.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, amount, date, userId);
    }

    @Override
    public String toString() {
        return "Expense{id=" + id + ", description='" + description + "', amount=" + amount + ", date='" + date + "', userId=" + userId + "}";
    }
}
