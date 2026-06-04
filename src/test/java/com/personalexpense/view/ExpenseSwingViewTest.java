package com.personalexpense.view;

import com.personalexpense.model.Category;
import com.personalexpense.model.Expense;
import com.personalexpense.service.ExpenseService;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseSwingViewTest {

    @Mock
    private ExpenseService expenseService;

    private FrameFixture window;
    private ExpenseSwingView view;

    @BeforeEach
    void setUp() {
        when(expenseService.getAllExpenses()).thenReturn(Collections.emptyList());
        when(expenseService.getAllCategories()).thenReturn(Collections.emptyList());

        view = GuiActionRunner.execute(() -> new ExpenseSwingView(expenseService));
        window = new FrameFixture(view);
        window.show(); // shows the frame to test
    }

    @AfterEach
    void tearDown() {
        window.cleanUp();
    }

    @Test
    void testInitialState() {
        window.textBox("descriptionField").requireEmpty();
        window.textBox("amountField").requireEmpty();
        window.textBox("dateField").requireEmpty();
        window.table("expenseTable").requireRowCount(0);
        window.label("errorLabel").requireText("");
    }

    @Test
    void testAddExpenseSuccess() {
        Expense e = new Expense(1L, "Lunch", 15.0, "2023-01-01");
        when(expenseService.addExpense(any(Expense.class))).thenReturn(e);
        when(expenseService.getAllExpenses()).thenReturn(Arrays.asList(e));

        window.textBox("descriptionField").enterText("Lunch");
        window.textBox("amountField").enterText("15.0");
        window.textBox("dateField").enterText("2023-01-01");
        window.button("addButton").click();

        window.table("expenseTable").requireRowCount(1);
        verify(expenseService).addExpense(any(Expense.class));
    }

    @Test
    void testAddExpenseValidationError() {
        doThrow(new IllegalArgumentException("Description cannot be empty"))
            .when(expenseService).addExpense(any(Expense.class));

        window.textBox("amountField").enterText("15.0");
        window.textBox("dateField").enterText("2023-01-01");
        window.button("addButton").click();

        window.label("errorLabel").requireText("Description cannot be empty");
    }

    @Test
    void testDeleteExpense() {
        Expense e = new Expense(1L, "Lunch", 15.0, "2023-01-01");
        when(expenseService.getAllExpenses()).thenReturn(Arrays.asList(e));
        
        // Setup view with one item
        GuiActionRunner.execute(() -> view.refreshTables());
        
        when(expenseService.getAllExpenses()).thenReturn(Collections.emptyList());

        window.table("expenseTable").selectRows(0);
        window.button("deleteButton").click();

        verify(expenseService).deleteExpense(1L);
        window.table("expenseTable").requireRowCount(0);
    }
}
