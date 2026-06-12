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
        window.label("errorLabel").requireText(" ");
    }

    @Test
    void testAddExpenseSuccess() {
        Expense e = new Expense(1L, "Lunch", 15.0, "2023-01-01");
        when(expenseService.addExpense(any(Expense.class))).thenReturn(e);
        when(expenseService.getAllExpenses()).thenReturn(Arrays.asList(e));

        window.textBox("descriptionField").setText("Lunch");
        window.textBox("amountField").setText("15.0");
        window.textBox("dateField").setText("2023-01-01");
        GuiActionRunner.execute(() -> window.button("addButton").target().doClick());

        window.label("errorLabel").requireText(" ");
        verify(expenseService).addExpense(any(Expense.class));
        window.table("expenseTable").requireRowCount(1);
    }

    @Test
    void testAddExpenseValidationError() {
        doThrow(new IllegalArgumentException("Description cannot be empty"))
            .when(expenseService).addExpense(any(Expense.class));

        window.textBox("amountField").setText("15.0");
        window.textBox("dateField").setText("2023-01-01");
        GuiActionRunner.execute(() -> window.button("addButton").target().doClick());

        window.label("errorLabel").requireText("Description cannot be empty");
    }

    @Test
    void testDeleteExpense() {
        Expense e = new Expense(1L, "Lunch", 15.0, "2023-01-01");
        lenient().when(expenseService.getAllExpenses()).thenReturn(Arrays.asList(e));
        lenient().when(expenseService.getExpenseById(1L)).thenReturn(e);
        
        // Setup view with one item
        GuiActionRunner.execute(() -> view.refreshExpenseTable());
        
        lenient().when(expenseService.getAllExpenses()).thenReturn(Collections.emptyList());

        GuiActionRunner.execute(() -> window.table("expenseTable").target().setRowSelectionInterval(0, 0));
        GuiActionRunner.execute(() -> window.button("deleteButton").target().doClick());

        verify(expenseService).deleteExpense(1L);
        window.table("expenseTable").requireRowCount(0);
    }
}
