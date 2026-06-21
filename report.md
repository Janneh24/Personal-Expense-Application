# Project Report: Personal Expense Application

## 1. Introduction and Implemented Features

For this project, we built the **Personal Expense Application**, a comprehensive desktop software solution designed to help users manage their daily finances, categorize their expenses, and generate detailed spending reports. We built the application using Java, implementing a robust, layered architecture backed by a MySQL database.

### Core Domain Model
Our application revolves around three primary entities:
1. **User**: Represents the individuals interacting with the application. Users have a username, password, role (`ADMIN` or `USER`), and an active/inactive status.
2. **Category**: A classification for expenses (e.g., "Food", "Travel", "Utilities"). Categories are managed globally and can be assigned to multiple expenses.
3. **Expense**: The central entity representing a financial transaction. An expense consists of a description, an amount, a date, a reference to the user who created it, and a list of associated categories.

### Key Features
- **User Management**: Secure authentication. Administrators can create, update, delete, disable, and enable users.
- **Expense Tracking**: Users can log new expenses, update existing ones, and delete them. We implemented strict validation to ensure amounts are strictly positive and dates are formatted correctly.
- **Category Management**: Users can dynamically create custom categories and assign or remove them from expenses.
- **Report Generation**: The system generates detailed, HTML-formatted financial summaries that break down costs by category. Additionally, users can export reports directly to a professionally formatted PDF document for local saving and printing.

> **[📸 SCREENSHOT PLACEHOLDER: Main Application Window]**
> *Take a screenshot of your running application showing the main dashboard with a list of expenses and categories visible.*

---

## 2. Applied Techniques and Frameworks

We developed the project following modern software engineering practices. Rather than just making the code work, we focused heavily on structural integrity, testability, and high code quality.

- **Apache Maven**: Managed our project dependencies, defined the build lifecycle, and orchestrated our plugins (like Surefire for testing and JaCoCo for coverage).
- **Java Swing**: Used to build a responsive and interactive desktop client for our users.
- **Google Guice**: Served as our Dependency Injection (DI) framework. This was critical for decoupling our application layers, allowing us to seamlessly inject repositories into our services.
- **JUnit 5 (Jupiter), AssertJ, & Mockito**: The core of our testing strategy. We used Mockito heavily to create mock objects for the Repository layer, enabling us to test our Service layer business logic in pure isolation.
- **Testcontainers**: Allowed us to spin up lightweight, ephemeral Docker containers running MySQL during Integration Testing. This ensured we tested our database logic against a real production-like environment rather than an unreliable in-memory substitute.
- **Cucumber**: Implemented for Behavior-Driven Development (BDD). We wrote user stories in Gherkin syntax to validate end-to-end acceptance criteria.
- **AssertJ-Swing**: Used for automated GUI testing, allowing us to programmatically simulate button clicks and text inputs.
- **JaCoCo & Pitest**: JaCoCo monitored our standard code coverage, while Pitest handled Mutation Testing to ensure our test suite was actually robust enough to catch injected faults.
- **GitHub Actions & SonarCloud**: Our CI/CD pipeline automatically built the project, ran tests, and published quality metrics to SonarCloud on every push.

> **[📸 SCREENSHOT PLACEHOLDER: GitHub Actions Pipeline]**
> *Take a screenshot of your GitHub Actions page showing a successful green build passing all steps.*

---

## 3. Design and Implementation Choices

We designed the architecture to strictly separate concerns, making the system highly testable and maintainable over time.

### Layered Architecture
We divided the application into three distinct layers:
1. **Repository Layer (Data Access)**: Interfaces (`UserRepository`, `ExpenseRepository`) that define the contract for data persistence. 
2. **Service Layer (Business Logic)**: Classes (`UserService`, `ExpenseService`) that contain the core business rules and validation. They have no knowledge of the database implementation.
3. **View Layer (GUI)**: The `ExpenseSwingView` handles all user interactions and communicates exclusively with the Service layer.

### Dependency Injection over Hardcoding
By using Google Guice, we avoided tightly coupling our code. Our `ExpenseModule` handles the bindings between the abstract Repository interfaces and their concrete MySQL implementations. This choice was the primary reason we were able to achieve 100% unit test coverage, as it allowed us to effortlessly inject Mockito mocks during testing instead of real databases.

### Pure JDBC over ORM
Rather than relying on a heavy Object-Relational Mapper (ORM) like Hibernate, we implemented the data access layer using pure JDBC (`java.sql.PreparedStatement`). This choice was made so we could maintain absolute control over the SQL queries—specifically for handling the complex Many-to-Many relationship between `Expense` and `Category` via our join table.

> **[📸 SCREENSHOT PLACEHOLDER: Database Schema / ER Diagram]**
> *Insert a simple ER Diagram or a screenshot of your MySQL workbench showing the `users`, `expenses`, `categories`, and `expense_category` tables and how they link together.*

---

## 4. Development and Testing of the Most Interesting Parts

### The HTML Report Generation and PDF Export Engine
One of the most complex features we developed was the report generation system. The `generateReport` method in the `ExpenseService` acts as a financial aggregator, fetching all expenses for a user, calculating the grand total, and creating a mathematical breakdown of spending per category. 

We built the algorithm using a `TreeMap<String, Double>` to dynamically accumulate totals for each category alphabetically, while keeping a separate running total for uncategorized expenses. Finally, it dynamically constructs a stylized HTML document. 

To expand this capability, we implemented the `PdfReportExporter` helper class, which utilizes the **OpenPDF** library to compile this mathematical breakdown and export it into a formal PDF document. The library allows us to build customized layout elements, including an aligned table (`PdfPTable`), custom cell styling (`PdfPCell`), headers, and customized colors.

Testing this engine required:
1. **Unit Testing / Mocking:** Meticulous setup using Mockito to return a curated list of `Expense` objects (some with multiple categories, some uncategorized) and using AssertJ to verify the exact mathematical outputs in the HTML.
2. **Output Stream Verification:** Mocking the output streams during PDF generation tests to verify that files write correctly and no data corruption occurs.

> **[📸 SCREENSHOT PLACEHOLDER: HTML & PDF Reports]**
> *Take a screenshot of the actual generated HTML expense report displaying the total and the category breakdowns, and a screenshot of the opened PDF document.*

### Integration Testing with Testcontainers
Testing our JDBC repositories required a real database to accurately test SQL syntax and foreign key constraints (like `ON DELETE CASCADE`). Instead of mocking the database, we utilized **Testcontainers**. 

In our Integration Tests (`*IT.java`), a Dockerized MySQL database spins up before the test suite begins, and a database initialization script (`init.sql`) executes to create the tables. To ensure test isolation, we ran cleanup scripts to truncate the tables after each test. This gave us absolute confidence that our complex `JOIN` queries worked perfectly in reality.

### Behavior-Driven Development (Cucumber)
To ensure the software met actual user requirements, we developed from the outside in using Cucumber. We wrote Feature files defining scenarios like adding expenses or logging in, and mapped them to Java step definitions. Developing this way forced us to think about the application from the user's perspective before writing a single line of backend logic.

> **[📸 SCREENSHOT PLACEHOLDER: Cucumber Test Output]**
> *Take a screenshot of your terminal or IDE showing the green, passing output of your Cucumber `.feature` scenarios.*

---

## 5. Problems Encountered and Solutions

Developing a project to strict metric standards (100% Code Coverage, 0 Code Smells, 0 Technical Debt, and 100% Mutation Coverage) presented several highly obscure technical challenges that pushed us to our limits.

### Problem 1: Hidden Coverage Gaps (The `this == o` Branch)
**The Problem:** Despite writing comprehensive unit tests for all our models and services, SonarCloud reported our code coverage was stuck at 99.4%. We were incredibly frustrated trying to find the missing fraction of a percent of branch coverage.
**The Solution:** After a rigorous, line-by-line audit of the Jacoco coverage reports, we discovered the culprit. The overridden `equals(Object o)` methods in our domain models contained the standard boilerplate optimization: `if (this == o) return true;`. In all our tests, we compared different objects with identical values, but we never tested comparing an object directly to *itself*. We solved this by explicitly adding `assertThat(entity).isEqualTo(entity);` to our test suites, successfully evaluating the true condition of that branch and pushing our coverage up.

### Problem 2: The SonarCloud Code Smell Dilemma in HTML Generation
**The Problem:** Our `generateReport` method builds an HTML string. Because standard HTML tags like `"</tr>"` and `"</td>"` were appended multiple times in the loops, SonarCloud aggressively flagged them under **Rule java:S1192: String literals should not be duplicated**, generating Technical Debt.
**Attempted Solution:** We initially added the `@SuppressWarnings("java:S1192")` annotation to force SonarCloud to ignore it. However, SonarCloud is incredibly strict, and this introduced a *new* code smell: **Rule java:S1309: Track uses of SuppressWarnings**. 
**The Solution:** To achieve a pure 0 Technical Debt score without relying on suppression hacks, we refactored the HTML generation. We extracted the duplicated tags into private static final constants (e.g., `private static final String HTML_TR_END = "</tr>";`) and referenced the constants instead. This legitimately eliminated the code smells while maintaining clean architecture.

### Problem 3: The Untested "ADMIN" Validation Branch
**The Problem:** While hunting down the final 0.3% of missing branch coverage, we identified a missing branch in the `validateUser` method. The method checked `if (!role.equals("ADMIN") && !role.equals("USER"))`.
**The Solution:** We realized that across all our Service tests for `createUser` and `updateUser`, we had exclusively mocked and created users with the `"USER"` role. Therefore, the `!role.equals("ADMIN")` check was *always* evaluating to `true`, and the false condition was never executed. We resolved this by explicitly writing a `testCreateUserAdminRoleSuccess` unit test that passed the `"ADMIN"` role through the service layer, successfully hitting the final missing branch condition and securing perfect 100% test coverage.

> **[📸 SCREENSHOT PLACEHOLDER: Perfect SonarCloud Dashboard]**
> *Take a screenshot of your SonarCloud project dashboard showing exactly 100% Coverage, 0.0% Duplication, 0 Code Smells, and 0 Technical Debt. This is the ultimate proof of your success!*

### Conclusion
Building the Personal Expense Application was an incredible exercise in strict engineering discipline. By adhering to architectural best practices, test-driven methodologies, and rigorous static code analysis, we delivered a highly reliable, mathematically proven codebase that successfully achieved flawless quality metrics.

---

## 6. References and Citations

As requested by the course guidelines, below are the citations and online documentation links for the third-party libraries, frameworks, and mechanisms utilized in this project that were not part of the standard course slides:

1. **Google Guice (Dependency Injection Framework):**
   * *Purpose:* Configured as our DI container to decouple views, services, and repositories.
   * *Source/Documentation:* [Google Guice Wiki & Getting Started Guide](https://github.com/google/guice/wiki/GettingStarted)
2. **OpenPDF (PDF Generation Library):**
   * *Purpose:* Used inside `PdfReportExporter` to write and format the financial summary report to a local PDF file.
   * *Source/Documentation:* [OpenPDF Github Repository & API Guide](https://github.com/LibrePDF/OpenPDF)
3. **Testcontainers (Database Integration Testing):**
   * *Purpose:* Used to spin up real, isolated MySQL Docker containers during integration tests.
   * *Source/Documentation:* [Testcontainers Java Quickstart](https://java.testcontainers.org/)
4. **Cucumber-JVM (Behavior-Driven Development):**
   * *Purpose:* Used to map our plain-text Gherkin feature scenarios to executable step definitions.
   * *Source/Documentation:* [Cucumber Java Reference Documentation](https://cucumber.io/docs/installation/java/)
5. **AssertJ Swing (GUI Testing Framework):**
   * *Purpose:* Used to execute automated GUI assertions and simulate user events in the Swing interface.
   * *Source/Documentation:* [AssertJ Swing Documentation](https://assertj.github.io/doc/)
6. **Docker Compose:**
   * *Purpose:* Provisioning local containerized services for development and BDD verification.
   * *Source/Documentation:* [Docker Compose CLI Reference](https://docs.docker.com/compose/)
