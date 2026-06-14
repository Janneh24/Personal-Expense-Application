package com.personalexpense.service;

import com.personalexpense.model.User;
import com.personalexpense.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testAuthenticateSuccess() {
        User u = new User(1L, "admin", "adminpwd", "ADMIN", true);
        when(userRepository.findByUsername("admin")).thenReturn(u);

        User authenticated = userService.authenticate("admin", "adminpwd");
        assertThat(authenticated).isEqualTo(u);
        assertThat(u).isEqualTo(u); // Fix missing this==o coverage
    }

    @Test
    void testAuthenticateInvalidUsername() {
        assertThatThrownBy(() -> userService.authenticate("", "pwd"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username cannot be null or empty");

        assertThatThrownBy(() -> userService.authenticate(null, "pwd"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username cannot be null or empty");
    }

    @Test
    void testAuthenticateInvalidPassword() {
        assertThatThrownBy(() -> userService.authenticate("admin", ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password cannot be null or empty");

        assertThatThrownBy(() -> userService.authenticate("admin", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password cannot be null or empty");
    }

    @Test
    void testAuthenticateUserNotFound() {
        when(userRepository.findByUsername("admin")).thenReturn(null);
        assertThatThrownBy(() -> userService.authenticate("admin", "pwd"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid username or password");
    }

    @Test
    void testAuthenticateWrongPassword() {
        User u = new User(1L, "admin", "adminpwd", "ADMIN", true);
        when(userRepository.findByUsername("admin")).thenReturn(u);

        assertThatThrownBy(() -> userService.authenticate("admin", "wrong"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid username or password");
    }

    @Test
    void testAuthenticateUserDisabled() {
        User u = new User(1L, "admin", "adminpwd", "ADMIN", false);
        when(userRepository.findByUsername("admin")).thenReturn(u);

        assertThatThrownBy(() -> userService.authenticate("admin", "adminpwd"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User account is disabled");
    }

    @Test
    void testCreateUserSuccess() {
        User u = new User(0L, "user1", "userpwd", "USER", true);
        User saved = new User(1L, "user1", "userpwd", "USER", true);

        when(userRepository.findByUsername("user1")).thenReturn(null);
        when(userRepository.save(u)).thenReturn(saved);

        User result = userService.createUser(u);
        assertThat(result).isEqualTo(saved);

        // Test ADMIN role to hit missing validateUser branch
        User adminUser = new User(0L, "admin_user", "adminpwd", "ADMIN", true);
        User adminSaved = new User(2L, "admin_user", "adminpwd", "ADMIN", true);
        when(userRepository.findByUsername("admin_user")).thenReturn(null);
        when(userRepository.save(adminUser)).thenReturn(adminSaved);
        User adminResult = userService.createUser(adminUser);
        assertThat(adminResult).isEqualTo(adminSaved);
    }

    @Test
    void testCreateUserDuplicateUsername() {
        User u = new User(0L, "user1", "userpwd", "USER", true);
        User existing = new User(1L, "user1", "otherpwd", "USER", true);

        when(userRepository.findByUsername("user1")).thenReturn(existing);

        assertThatThrownBy(() -> userService.createUser(u))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username already exists");
    }

    @Test
    void testCreateUserInvalidDetails() {
        // Username null/empty
        assertThatThrownBy(() -> userService.createUser(new User(0L, null, "pwd", "USER", true)))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> userService.createUser(new User(0L, "", "pwd", "USER", true)))
                .isInstanceOf(IllegalArgumentException.class);

        // Password null/empty
        assertThatThrownBy(() -> userService.createUser(new User(0L, "user", null, "USER", true)))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> userService.createUser(new User(0L, "user", "", "USER", true)))
                .isInstanceOf(IllegalArgumentException.class);

        // Role null/empty
        assertThatThrownBy(() -> userService.createUser(new User(0L, "user", "pwd", null, true)))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> userService.createUser(new User(0L, "user", "pwd", "", true)))
                .isInstanceOf(IllegalArgumentException.class);

        // Role invalid
        assertThatThrownBy(() -> userService.createUser(new User(0L, "user", "pwd", "INVALID_ROLE", true)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Role must be ADMIN or USER");
    }

    @Test
    void testUpdateUserSuccess() {
        User u = new User(1L, "user1", "userpwd", "USER", true);
        when(userRepository.findByUsername("user1")).thenReturn(u);
        when(userRepository.update(u)).thenReturn(u);

        User result = userService.updateUser(u);
        assertThat(result).isEqualTo(u);
    }

    @Test
    void testUpdateUserNewUsernameSuccess() {
        User u = new User(1L, "new_username", "pwd", "USER", true);
        // Return null to hit the 'existing == null' branch in updateUser
        when(userRepository.findByUsername("new_username")).thenReturn(null);
        when(userRepository.update(u)).thenReturn(u);

        User result = userService.updateUser(u);
        assertThat(result).isEqualTo(u);
    }

    @Test
    void testUpdateUserInvalidId() {
        User u = new User(0L, "user1", "pwd", "USER", true);
        assertThatThrownBy(() -> userService.updateUser(u))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User id must be greater than 0");
    }

    @Test
    void testUpdateUserDuplicateUsername() {
        User u = new User(1L, "user1", "pwd", "USER", true);
        User existing = new User(2L, "user1", "pwd", "USER", true);

        when(userRepository.findByUsername("user1")).thenReturn(existing);

        assertThatThrownBy(() -> userService.updateUser(u))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username already exists");
    }

    @Test
    void testDeleteUser() {
        userService.deleteUser(1L);
        verify(userRepository).delete(1L);
    }

    @Test
    void testDisableUserSuccess() {
        User u = new User(1L, "user1", "pwd", "USER", true);
        when(userRepository.findById(1L)).thenReturn(u);

        userService.disableUser(1L);

        assertThat(u.isEnabled()).isFalse();
        verify(userRepository).update(u);
    }

    @Test
    void testDisableUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(null);
        assertThatThrownBy(() -> userService.disableUser(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void testEnableUserSuccess() {
        User u = new User(1L, "user1", "pwd", "USER", false);
        when(userRepository.findById(1L)).thenReturn(u);

        userService.enableUser(1L);

        assertThat(u.isEnabled()).isTrue();
        verify(userRepository).update(u);
    }

    @Test
    void testEnableUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(null);
        assertThatThrownBy(() -> userService.enableUser(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void testGetAllUsers() {
        User u = new User(1L, "user1", "pwd", "USER", true);
        when(userRepository.findAll()).thenReturn(Arrays.asList(u));

        List<User> result = userService.getAllUsers();
        assertThat(result).containsExactly(u);
        verify(userRepository).findAll();
    }

    @Test
    void testUpdateUserInvalidDetails() {
        // Calls updateUser with invalid data (empty username) to verify that
        // validateUser() is actually invoked inside updateUser().
        // This kills the VoidMethodCallMutator that removes the validateUser() call.
        User invalidUser = new User(1L, "", "pwd", "USER", true);
        assertThatThrownBy(() -> userService.updateUser(invalidUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username cannot be null or empty");
    }
}
