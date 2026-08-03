package com.personalexpense.controller;

import com.personalexpense.model.User;
import com.personalexpense.repository.UserRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.Locale;

public class UserController {

    private static final String USERNAME_EMPTY_ERROR = "Username cannot be null or empty";
    private static final String PASSWORD_EMPTY_ERROR = "Password cannot be null or empty";
    private static final String USER_NOT_FOUND_ERROR = "User not found: ";
    private static final String USERNAME_EXISTS_ERROR = "Username already exists: ";

    private final UserRepository userRepository;

    @Inject
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User authenticate(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException(USERNAME_EMPTY_ERROR);
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException(PASSWORD_EMPTY_ERROR);
        }

        User user = userRepository.findByUsername(username);
        if (user == null || !user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        if (!user.isEnabled()) {
            throw new IllegalArgumentException("User account is disabled");
        }
        return user;
    }

    public User createUser(User user) {
        validateUser(user);
        
        User existing = userRepository.findByUsername(user.getUsername());
        if (existing != null) {
            throw new IllegalArgumentException(USERNAME_EXISTS_ERROR + user.getUsername());
        }
        
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        if (user.getId() <= 0) {
            throw new IllegalArgumentException("User id must be greater than 0");
        }
        validateUser(user);

        User existing = userRepository.findByUsername(user.getUsername());
        if (existing != null && existing.getId() != user.getId()) {
            throw new IllegalArgumentException(USERNAME_EXISTS_ERROR + user.getUsername());
        }

        return userRepository.update(user);
    }

    public void deleteUser(long id) {
        userRepository.delete(id);
    }

    public void disableUser(long id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new IllegalArgumentException(USER_NOT_FOUND_ERROR + id);
        }
        user.setEnabled(false);
        userRepository.update(user);
    }

    public void enableUser(long id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new IllegalArgumentException(USER_NOT_FOUND_ERROR + id);
        }
        user.setEnabled(true);
        userRepository.update(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    private void validateUser(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException(USERNAME_EMPTY_ERROR);
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException(PASSWORD_EMPTY_ERROR);
        }
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            throw new IllegalArgumentException("Role cannot be null or empty");
        }
        String role = user.getRole().toUpperCase(Locale.ROOT);
        if (!role.equals("ADMIN") && !role.equals("USER")) {
            throw new IllegalArgumentException("Role must be ADMIN or USER");
        }
    }
}
