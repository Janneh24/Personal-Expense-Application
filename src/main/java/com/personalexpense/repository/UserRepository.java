package com.personalexpense.repository;

import com.personalexpense.model.User;
import java.util.List;

public interface UserRepository {
    List<User> findAll();
    User findById(long id);
    User findByUsername(String username);
    User save(User user);
    User update(User user);
    void delete(long id);
}
