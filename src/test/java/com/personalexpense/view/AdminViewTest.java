package com.personalexpense.view;

import com.personalexpense.model.User;
import com.personalexpense.service.UserService;
import com.personalexpense.service.ExpenseService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminViewTest {

    @Mock
    private UserService userService;

    @Mock
    private ExpenseService expenseService;

    @Mock
    private Provider<LoginView> loginViewProvider;

    @Mock
    private LoginView loginView;

    private FrameFixture window;
    private AdminView adminView;

    @BeforeEach
    void setUp() {
        User u1 = new User(1L, "user1", "pwd1", "USER", true);
        lenient().when(userService.getAllUsers()).thenReturn(Arrays.asList(u1));

        adminView = GuiActionRunner.execute(() -> new AdminView(userService, expenseService, loginViewProvider));
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
        User uNew = new User(0L, "newuser", "newpwd", "USER", true);
        User uSaved = new User(2L, "newuser", "newpwd", "USER", true);
        
        when(userService.createUser(any(User.class))).thenReturn(uSaved);
        
        // Setup after refresh
        User u1 = new User(1L, "user1", "pwd1", "USER", true);
        lenient().when(userService.getAllUsers()).thenReturn(Arrays.asList(u1, uSaved));

        window.textBox("usernameField").setText("newuser");
        window.textBox("passwordField").setText("newpwd");
        window.comboBox("roleCombo").selectItem("USER");
        window.button("createButton").click();

        verify(userService).createUser(any(User.class));
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
        when(userService.createUser(any(User.class)))
                .thenThrow(new IllegalArgumentException("Username cannot be null or empty"));

        window.textBox("passwordField").setText("pwd");
        window.button("createButton").click();

        window.label("errorLabel").requireText("Username cannot be null or empty");
    }

    @Test
    void testUpdateUserSuccess() {
        User u1 = new User(1L, "user1", "pwd1", "USER", true);
        User uUpdated = new User(1L, "updateduser", "newpwd", "ADMIN", true);
        
        when(userService.updateUser(any(User.class))).thenReturn(uUpdated);
        lenient().when(userService.getAllUsers()).thenReturn(Arrays.asList(u1)).thenReturn(Arrays.asList(uUpdated));

        window.table("userTable").selectRows(0);
        window.textBox("usernameField").setText("updateduser");
        window.textBox("passwordField").setText("newpwd");
        window.comboBox("roleCombo").selectItem("ADMIN");
        window.button("updateButton").click();

        verify(userService).updateUser(any(User.class));
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
        lenient().when(userService.getAllUsers()).thenReturn(Collections.emptyList());
        window.table("userTable").selectRows(0);

        window.button("deleteButton").click();

        verify(userService).deleteUser(1L);
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
        lenient().when(userService.getAllUsers()).thenReturn(Arrays.asList(uDisabled));

        window.table("userTable").selectRows(0);
        window.button("disableButton").click();

        verify(userService).disableUser(1L);
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
        lenient().when(userService.getAllUsers()).thenReturn(Arrays.asList(uEnabled));

        window.table("userTable").selectRows(0);
        window.button("enableButton").click();

        verify(userService).enableUser(1L);
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

        window.button("logoutButton").click();

        verify(loginViewProvider).get();
        verify(loginView).setVisible(true);
    }
}
