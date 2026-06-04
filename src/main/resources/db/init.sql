CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS expenses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    amount DOUBLE NOT NULL,
    date VARCHAR(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS expense_category (
    expense_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    PRIMARY KEY (expense_id, category_id),
    FOREIGN KEY (expense_id) REFERENCES expenses(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);
