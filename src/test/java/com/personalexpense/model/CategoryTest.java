package com.personalexpense.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CategoryTest {
    @Test
    void testCategoryProperties() {
        Category category = new Category(1L, "Food");
        assertThat(category.getId()).isEqualTo(1L);
        assertThat(category.getName()).isEqualTo("Food");
        
        category.setName("Travel");
        assertThat(category.getName()).isEqualTo("Travel");
    }

    @Test
    void testEqualsAndHashCode() {
        Category c1 = new Category(1L, "Food");
        Category c2 = new Category(1L, "Food");
        Category c3 = new Category(2L, "Travel");
        Category c4 = new Category(1L, "Travel"); // id matches, name mismatch

        assertThat(c1).isEqualTo(c2);
        assertThat(c1).isNotEqualTo(c3);
        assertThat(c1).isNotEqualTo(c4);
        assertThat(c1).isNotEqualTo(null);
        assertThat(c1).isNotEqualTo(new Object());
        assertThat(c1.hashCode()).isEqualTo(c2.hashCode());
        assertThat(c1.hashCode()).isNotEqualTo(c3.hashCode());
        assertThat(c1.hashCode()).isNotEqualTo(c4.hashCode());
    }

    @Test
    void testToString() {
        Category category = new Category(1L, "Food");
        assertThat(category.toString()).contains("1", "Food");
    }
}
