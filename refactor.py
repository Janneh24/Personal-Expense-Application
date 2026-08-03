import os
import re

root_dir = r"C:\Users\Essa Janneh\Desktop\UNIFI\2 SEM\AUTOMATED SOFTWARE TESTING\AST PROJECT 2\src"

replacements = {
    "com.personalexpense.service": "com.personalexpense.controller",
    "ExpenseService": "ExpenseController",
    "expenseService": "expenseController",
    "UserService": "UserController",
    "userService": "userController",
    "MYSQL_PASSWORD: userpwd": "MYSQL_ALLOW_EMPTY_PASSWORD: yes",
    '.withPassword("userpwd")': '.withEnv("MYSQL_ALLOW_EMPTY_PASSWORD", "yes")'
}

def process_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    original = content
    
    for old, new in replacements.items():
        content = content.replace(old, new)
        
    # Find JDBC URL and ensure no password is required if it was userpwd
    # Some Testcontainers might not even need withPassword if we use ALLOW_EMPTY_PASSWORD
    # Also just in case there's any other "userpwd" hardcoded
    content = content.replace('"userpwd"', '""')
    
    if content != original:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"Updated {filepath}")

for root, dirs, files in os.walk(root_dir):
    for file in files:
        if file.endswith(".java") or file.endswith(".xml") or file.endswith(".yaml") or file.endswith(".yml") or file.endswith(".properties"):
            process_file(os.path.join(root, file))

print("Refactoring complete.")
