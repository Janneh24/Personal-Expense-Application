package com.personalexpense.bdd;

import com.personalexpense.app.ExpenseApp;
import com.personalexpense.module.ExpenseModule;
import com.personalexpense.view.ExpenseSwingView;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.testcontainers.containers.MySQLContainer;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import java.time.Duration;

public class ExpenseManagementSteps {

    private static MySQLContainer<?> mysql;
    private FrameFixture window;

    @Before
    public void setUp() throws Exception {
        if (mysql == null) {
            mysql = new MySQLContainer<>("mysql:8.0")
                    .withDatabaseName("expensesdb")
                    .withUsername("user")
                    .withPassword("userpwd")
                    .withTmpFs(java.util.Collections.singletonMap("/var/lib/mysql", "rw"))
                    .withStartupTimeout(Duration.ofMinutes(5));
            mysql.start();
        }

        // Init DB
        String initSql = new String(Files.readAllBytes(Paths.get("src/main/resources/db/init.sql")));
        try (Connection conn = DriverManager.getConnection(mysql.getJdbcUrl(), mysql.getUsername(), mysql.getPassword());
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

        Injector injector = Guice.createInjector(
                new ExpenseModule(mysql.getHost(), mysql.getFirstMappedPort(), mysql.getDatabaseName(), mysql.getUsername(), mysql.getPassword())
        );

        ExpenseSwingView view = GuiActionRunner.execute(() -> injector.getInstance(ExpenseSwingView.class));
        window = new FrameFixture(view);
        window.show();
    }

    @After
    public void tearDown() {
        if (window != null) {
            window.cleanUp();
        }
    }

    @Given("the application is started")
    public void the_application_is_started() {
        window.requireVisible();
    }

    @When("I enter {string} in the description field")
    public void i_enter_in_the_description_field(String description) {
        window.textBox("descriptionField").enterText(description);
    }

    @When("I enter {string} in the amount field")
    public void i_enter_in_the_amount_field(String amount) {
        window.textBox("amountField").enterText(amount);
    }

    @When("I enter {string} in the date field")
    public void i_enter_in_the_date_field(String date) {
        window.textBox("dateField").enterText(date);
    }

    @When("I click the Add Expense button")
    public void i_click_the_add_expense_button() {
        window.button("addButton").click();
    }

    @Then("the expense list should contain an expense with description {string}")
    public void the_expense_list_should_contain_an_expense_with_description(String description) {
        String value = window.table("expenseTable").valueAt(org.assertj.swing.data.TableCell.row(0).column(1));
        org.assertj.core.api.Assertions.assertThat(value).isEqualTo(description);
    }

    @Then("I should see an error message {string}")
    public void i_should_see_an_error_message(String message) {
        window.label("errorLabel").requireText(message);
    }

    @When("I enter {string} in the category name field")
    public void i_enter_in_the_category_name_field(String name) {
        window.textBox("categoryNameField").enterText(name);
    }

    @When("I click the Add Category button")
    public void i_click_the_add_category_button() {
        window.button("addCategoryButton").click();
    }

    @Then("the category list should contain {string}")
    public void the_category_list_should_contain(String name) {
        String[] contents = window.comboBox("categoryCombo").contents();
        org.assertj.core.api.Assertions.assertThat(contents).contains(name);
    }
}
