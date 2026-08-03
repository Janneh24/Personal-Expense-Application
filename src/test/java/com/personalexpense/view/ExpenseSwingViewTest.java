package com.personalexpense.view;

import com.personalexpense.model.Category;
import com.personalexpense.model.Expense;
import com.personalexpense.controller.ExpenseController;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseSwingViewTest {

    @Mock
    private ExpenseController expenseController;

    private FrameFixture window;
    private ExpenseSwingView view;

    @BeforeEach
    void setUp() {
        when(expenseController.getAllExpenses()).thenReturn(Collections.emptyList());
        when(expenseController.getAllCategories()).thenReturn(Collections.emptyList());

        view = GuiActionRunner.execute(() -> new ExpenseSwingView(expenseController));
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
        window.label("errorLabel").requireText(" ");
    }

    @Test
    void testAddExpenseSuccess() {
        Expense e = new Expense(1L, "Lunch", 15.0, "2023-01-01");
        when(expenseController.addExpense(any(Expense.class))).thenReturn(e);
        when(expenseController.getAllExpenses()).thenReturn(Arrays.asList(e));

        window.textBox("descriptionField").setText("Lunch");
        window.textBox("amountField").setText("15.0");
        window.textBox("dateField").setText("2023-01-01");
        GuiActionRunner.execute(() -> window.button("addButton").target().doClick());

        window.label("errorLabel").requireText(" ");
        verify(expenseController).addExpense(any(Expense.class));
        window.table("expenseTable").requireRowCount(1);
    }

    @Test
    void testAddExpenseValidationError() {
        doThrow(new IllegalArgumentException("Description cannot be empty"))
            .when(expenseController).addExpense(any(Expense.class));

        window.textBox("amountField").setText("15.0");
        window.textBox("dateField").setText("2023-01-01");
        GuiActionRunner.execute(() -> window.button("addButton").target().doClick());

        window.label("errorLabel").requireText("Description cannot be empty");
    }

    @Test
    void testDeleteExpense() {
        Expense e = new Expense(1L, "Lunch", 15.0, "2023-01-01");
        lenient().when(expenseController.getAllExpenses()).thenReturn(Arrays.asList(e));
        lenient().when(expenseController.getExpenseById(1L)).thenReturn(e);
        
        // Setup view with one item
        GuiActionRunner.execute(() -> view.refreshExpenseTable());
        
        lenient().when(expenseController.getAllExpenses()).thenReturn(Collections.emptyList());

        GuiActionRunner.execute(() -> window.table("expenseTable").target().setRowSelectionInterval(0, 0));
        GuiActionRunner.execute(() -> window.button("deleteButton").target().doClick());

        verify(expenseController).deleteExpense(1L);
        window.table("expenseTable").requireRowCount(0);
    }
}
