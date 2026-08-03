package com.personalexpense.view;

import com.personalexpense.model.User;
import com.personalexpense.controller.UserController;
import com.personalexpense.controller.ExpenseController;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Provider;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminViewTest {

    @Mock
    private UserController userController;

    @Mock
    private ExpenseController expenseController;

    @Mock
    private Provider<LoginView> loginViewProvider;

    @Mock
    private LoginView loginView;

    private FrameFixture window;
    private AdminView adminView;

    @BeforeEach
    void setUp() {
        User u1 = new User(1L, "user1", "pwd1", "USER", true);
        lenient().when(userController.getAllUsers()).thenReturn(Arrays.asList(u1));

        adminView = GuiActionRunner.execute(() -> new AdminView(userController, expenseController, loginViewProvider));
        window = new FrameFixture(adminView);
        window.show();
    }

    @AfterEach
    void tearDown() {
        window.cleanUp();
    }

    @Test
    void testInitialState() {
        window.textBox("usernameField").requireEmpty();
        window.textBox("passwordField").requireEmpty();
        window.table("userTable").requireRowCount(1);
        window.table("userTable").requireContents(new String[][]{{"1", "user1", "USER", "true"}});
        window.label("errorLabel").requireText(" ");
    }

    @Test
    void testCreateUserSuccess() {
        User uSaved = new User(2L, "newuser", "newpwd", "USER", true);
        
        when(userController.createUser(any(User.class))).thenReturn(uSaved);
        
        // Setup after refresh
        User u1 = new User(1L, "user1", "pwd1", "USER", true);
        lenient().when(userController.getAllUsers()).thenReturn(Arrays.asList(u1, uSaved));

        window.textBox("usernameField").setText("newuser");
        window.textBox("passwordField").setText("newpwd");
        window.comboBox("roleCombo").selectItem("USER");
        GuiActionRunner.execute(() -> window.button("createButton").target().doClick());

        verify(userController).createUser(any(User.class));
        window.table("userTable").requireRowCount(2);
        window.table("userTable").requireContents(new String[][]{
            {"1", "user1", "USER", "true"},
            {"2", "newuser", "USER", "true"}
        });
        window.textBox("usernameField").requireEmpty();
        window.textBox("passwordField").requireEmpty();
    }

    @Test
    void testCreateUserValidationError() {
        when(userController.createUser(any(User.class)))
                .thenThrow(new IllegalArgumentException("Username cannot be null or empty"));

        window.textBox("passwordField").setText("pwd");
        GuiActionRunner.execute(() -> window.button("createButton").target().doClick());

        window.label("errorLabel").requireText("Username cannot be null or empty");
    }

    @Test
    void testUpdateUserSuccess() {
        User u1 = new User(1L, "user1", "pwd1", "USER", true);
        User uUpdated = new User(1L, "updateduser", "newpwd", "ADMIN", true);
        
        when(userController.updateUser(any(User.class))).thenReturn(uUpdated);
        lenient().when(userController.getAllUsers()).thenReturn(Arrays.asList(u1)).thenReturn(Arrays.asList(uUpdated));

        GuiActionRunner.execute(() -> window.table("userTable").target().setRowSelectionInterval(0, 0));
        window.textBox("usernameField").setText("updateduser");
        window.textBox("passwordField").setText("newpwd");
        window.comboBox("roleCombo").selectItem("ADMIN");
        GuiActionRunner.execute(() -> window.button("updateButton").target().doClick());

        verify(userController).updateUser(any(User.class));
        window.table("userTable").requireContents(new String[][]{{"1", "updateduser", "ADMIN", "true"}});
    }

    @Test
    void testUpdateUserNoSelectionError() {
        GuiActionRunner.execute(() -> adminView.clearUserSelection());
        GuiActionRunner.execute(() -> adminView.updateUser());
        window.label("errorLabel").requireText("Please select a user to update");
    }

    @Test
    void testDeleteUserSuccess() {
        lenient().when(userController.getAllUsers()).thenReturn(Collections.emptyList());
        GuiActionRunner.execute(() -> window.table("userTable").target().setRowSelectionInterval(0, 0));

        GuiActionRunner.execute(() -> window.button("deleteButton").target().doClick());

        verify(userController).deleteUser(1L);
        window.table("userTable").requireRowCount(0);
    }

    @Test
    void testDeleteUserNoSelectionError() {
        GuiActionRunner.execute(() -> adminView.clearUserSelection());
        GuiActionRunner.execute(() -> adminView.deleteUser());
        window.label("errorLabel").requireText("Please select a user to delete");
    }

    @Test
    void testDisableUserSuccess() {
        User uDisabled = new User(1L, "user1", "pwd1", "USER", false);
        // After disableUser runs, refresh will fetch all users again
        lenient().when(userController.getAllUsers()).thenReturn(Arrays.asList(uDisabled));

        GuiActionRunner.execute(() -> window.table("userTable").target().setRowSelectionInterval(0, 0));
        GuiActionRunner.execute(() -> window.button("disableButton").target().doClick());

        verify(userController).disableUser(1L);
        window.table("userTable").requireContents(new String[][]{{"1", "user1", "USER", "false"}});
    }

    @Test
    void testDisableUserNoSelectionError() {
        GuiActionRunner.execute(() -> adminView.clearUserSelection());
        GuiActionRunner.execute(() -> adminView.disableUser());
        window.label("errorLabel").requireText("Please select a user to disable");
    }

    @Test
    void testEnableUserSuccess() {
        User uEnabled = new User(1L, "user1", "pwd1", "USER", true);
        // After enableUser runs, refresh will fetch all users again
        lenient().when(userController.getAllUsers()).thenReturn(Arrays.asList(uEnabled));

        GuiActionRunner.execute(() -> window.table("userTable").target().setRowSelectionInterval(0, 0));
        GuiActionRunner.execute(() -> window.button("enableButton").target().doClick());

        verify(userController).enableUser(1L);
        window.table("userTable").requireContents(new String[][]{{"1", "user1", "USER", "true"}});
    }

    @Test
    void testEnableUserNoSelectionError() {
        GuiActionRunner.execute(() -> adminView.clearUserSelection());
        GuiActionRunner.execute(() -> adminView.enableUser());
        window.label("errorLabel").requireText("Please select a user to enable");
    }

    @Test
    void testViewUserReportNoSelectionError() {
        GuiActionRunner.execute(() -> adminView.clearUserSelection());
        GuiActionRunner.execute(() -> adminView.viewUserReport());
        window.label("errorLabel").requireText("Please select a user to view their report");
    }

    @Test
    void testLogout() {
        when(loginViewProvider.get()).thenReturn(loginView);

        GuiActionRunner.execute(() -> window.button("logoutButton").target().doClick());

        verify(loginViewProvider).get();
        verify(loginView).setVisible(true);
    }
}
