Feature: Expense Management
  As a user
  I want to manage my personal expenses
  So that I can track my spending

  Scenario: Add a new expense successfully
    Given the application is started
    When I enter "Lunch" in the description field
    And I enter "15.5" in the amount field
    And I enter "2023-01-01" in the date field
    And I click the Add Expense button
    Then the expense list should contain an expense with description "Lunch"

  Scenario: Add a new expense with missing description shows error
    Given the application is started
    When I enter "15.5" in the amount field
    And I enter "2023-01-01" in the date field
    And I click the Add Expense button
    Then I should see an error message "Expense description cannot be null or empty"

  Scenario: Add a category successfully
    Given the application is started
    When I enter "Food" in the category name field
    And I click the Add Category button
    Then the category list should contain "Food"
