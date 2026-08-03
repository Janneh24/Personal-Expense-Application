package com.personalexpense.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void testUserProperties() {
        User user = new User(1L, "admin", "adminpwd", "ADMIN", true);
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getUsername()).isEqualTo("admin");
        assertThat(user.getPassword()).isEqualTo("adminpwd");
        assertThat(user.getRole()).isEqualTo("ADMIN");
        assertThat(user.isEnabled()).isTrue();

        user.setId(2L);
        user.setUsername("user1");
        user.setPassword("");
        user.setRole("USER");
        user.setEnabled(false);

        assertThat(user.getId()).isEqualTo(2L);
        assertThat(user.getUsername()).isEqualTo("user1");
        assertThat(user.getPassword()).isEqualTo("");
        assertThat(user.getRole()).isEqualTo("USER");
        assertThat(user.isEnabled()).isFalse();
    }

    @Test
    void testDefaultConstructor() {
        User user = new User();
        assertThat(user.isEnabled()).isTrue();
    }

    @Test
    void testEqualsAndHashCode() {
        User u1 = new User(1L, "admin", "adminpwd", "ADMIN", true);
        User u2 = new User(1L, "admin", "adminpwd", "ADMIN", true);
        User u3 = new User(2L, "admin", "adminpwd", "ADMIN", true);
        User u4 = new User(1L, "admin2", "adminpwd", "ADMIN", true);
        User u5 = new User(1L, "admin", "adminpwd2", "ADMIN", true);
        User u6 = new User(1L, "admin", "adminpwd", "USER", true);
        User u7 = new User(1L, "admin", "adminpwd", "ADMIN", false);

        assertThat(u1).isEqualTo(u2);
        assertThat(u1).isNotEqualTo(u3);
        assertThat(u1).isNotEqualTo(u4);
        assertThat(u1).isNotEqualTo(u5);
        assertThat(u1).isNotEqualTo(u6);
        assertThat(u1).isNotEqualTo(u7);
        assertThat(u1).isNotEqualTo(null);
        assertThat(u1).isNotEqualTo(new Object());

        assertThat(u1.hashCode()).isEqualTo(u2.hashCode());
        assertThat(u1.hashCode()).isNotEqualTo(u3.hashCode());
    }

    @Test
    void testToString() {
        User user = new User(1L, "admin", "adminpwd", "ADMIN", true);
        assertThat(user.toString()).contains("1", "admin", "ADMIN", "true");
    }
}
