package com.personalexpense.repository;

import com.personalexpense.model.Category;
import java.util.List;

public interface CategoryRepository {
    List<Category> findAll();
    Category findById(long id);
    Category save(Category category);
    Category update(Category category);
    void delete(long id);
}
