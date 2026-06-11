package com.personalexpense.view;

import com.personalexpense.model.User;
import com.personalexpense.service.UserService;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginViewTest {

    @Mock
    private UserService userService;

    @Mock
    private Provider<ExpenseSwingView> expenseViewProvider;

    @Mock
    private Provider<AdminView> adminViewProvider;

    @Mock
    private ExpenseSwingView expenseSwingView;

    @Mock
    private AdminView adminView;

    private FrameFixture window;
    private LoginView loginView;

    @BeforeEach
    void setUp() {
        loginView = GuiActionRunner.execute(() -> new LoginView(userService, expenseViewProvider, adminViewProvider));
        window = new FrameFixture(loginView);
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
        window.radioButton("userRadio").requireSelected();
        window.radioButton("adminRadio").requireNotSelected();
        window.label("errorLabel").requireText(" ");
    }

    @Test
    void testLoginValidationError() {
        when(userService.authenticate("invalid", "pwd"))
                .thenThrow(new IllegalArgumentException("Invalid username or password"));

        window.textBox("usernameField").setText("invalid");
        window.textBox("passwordField").setText("pwd");
        window.button("loginButton").click();

        window.label("errorLabel").requireText("Invalid username or password");
        verify(expenseViewProvider, never()).get();
        verify(adminViewProvider, never()).get();
    }

    @Test
    void testLoginRoleMismatch() {
        User u = new User(1L, "admin", "adminpwd", "ADMIN", true);
        when(userService.authenticate("admin", "adminpwd")).thenReturn(u);

        window.textBox("usernameField").setText("admin");
        window.textBox("passwordField").setText("adminpwd");
        window.radioButton("userRadio").click(); // User radio is selected, but user is ADMIN in db
        window.button("loginButton").click();

        window.label("errorLabel").requireText("Role mismatch. You selected USER but you are registered as ADMIN");
        verify(expenseViewProvider, never()).get();
        verify(adminViewProvider, never()).get();
    }

    @Test
    void testLoginUserSuccess() {
        User u = new User(1L, "user1", "userpwd", "USER", true);
        when(userService.authenticate("user1", "userpwd")).thenReturn(u);
        when(expenseViewProvider.get()).thenReturn(expenseSwingView);

        window.textBox("usernameField").setText("user1");
        window.textBox("passwordField").setText("userpwd");
        window.radioButton("userRadio").click();
        window.button("loginButton").click();

        verify(expenseViewProvider).get();
        verify(expenseSwingView).setCurrentUser(u);
        verify(expenseSwingView).setVisible(true);
    }

    @Test
    void testLoginAdminSuccess() {
        User u = new User(1L, "admin", "adminpwd", "ADMIN", true);
        when(userService.authenticate("admin", "adminpwd")).thenReturn(u);
        when(adminViewProvider.get()).thenReturn(adminView);

        window.textBox("usernameField").setText("admin");
        window.textBox("passwordField").setText("adminpwd");
        window.radioButton("adminRadio").click();
        window.button("loginButton").click();

        verify(adminViewProvider).get();
        verify(adminView).setVisible(true);
    }
}
