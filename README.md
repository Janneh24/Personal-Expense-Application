# Personal Expense Application

A Java desktop application for tracking and managing personal expenses, built with Maven and backed by MySQL.

## Badges

[![CI Build](https://github.com/Janneh24/Personal-Expense-Application/actions/workflows/ci.yml/badge.svg)](https://github.com/Janneh24/Personal-Expense-Application/actions/workflows/ci.yml)
[![Coverage Status](https://coveralls.io/repos/github/Janneh24/Personal-Expense-Application/badge.svg?branch=main&kill_cache=1)](https://coveralls.io/github/Janneh24/Personal-Expense-Application?branch=main)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Janneh24_Personal-Expense-Application&metric=alert_status)](https://sonarcloud.io/dashboard?id=Janneh24_Personal-Expense-Application)
[![SonarCloud Coverage](https://sonarcloud.io/api/project_badges/measure?project=Janneh24_Personal-Expense-Application&metric=coverage)](https://sonarcloud.io/dashboard?id=Janneh24_Personal-Expense-Application)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Janneh24_Personal-Expense-Application&metric=bugs)](https://sonarcloud.io/dashboard?id=Janneh24_Personal-Expense-Application)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=Janneh24_Personal-Expense-Application&metric=code_smells)](https://sonarcloud.io/dashboard?id=Janneh24_Personal-Expense-Application)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=Janneh24_Personal-Expense-Application&metric=sqale_index)](https://sonarcloud.io/dashboard?id=Janneh24_Personal-Expense-Application)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=Janneh24_Personal-Expense-Application&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=Janneh24_Personal-Expense-Application)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=Janneh24_Personal-Expense-Application&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=Janneh24_Personal-Expense-Application)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=Janneh24_Personal-Expense-Application&metric=security_rating)](https://sonarcloud.io/dashboard?id=Janneh24_Personal-Expense-Application)

## Prerequisites

- **Java 17** (JDK)
- **Maven** 3.8+
- **Docker** & **Docker Compose**

## Getting Started

### 1. Start the Database

```bash
docker-compose up -d
```

This launches a MySQL 8.0 container with the `expensesdb` database pre-configured.

### 2. Build the Project

```bash
mvn clean verify
```

### 3. Run the Application

```bash
mvn exec:java "-Dexec.mainClass=com.personalexpense.app.ExpenseApp" "-Ddb.port=3307"
```

## Project Structure

```
Personal-Expense-Application/
├── .github/
│   └── workflows/
│       └── ci.yml              # GitHub Actions CI pipeline
├── src/
│   ├── main/
│   │   ├── java/               # Application source code
│   │   └── resources/
│   │       └── db/
│   │           └── init.sql    # Database initialization script
│   └── test/
│       ├── java/               # Unit and integration tests
│       └── resources/          # Test resources
├── .classpath                  # Eclipse classpath configuration
├── .gitignore                  # Git ignore rules
├── .project                    # Eclipse project descriptor
├── docker-compose.yml          # Docker Compose for MySQL
├── pom.xml                     # Maven build configuration
└── README.md                   # This file
```

## Technologies Used

| Technology       | Purpose                        |
|------------------|--------------------------------|
| Java 17          | Application language           |
| Maven            | Build and dependency management|
| MySQL 8.0        | Relational database            |
| Docker Compose   | Local database provisioning    |
| JUnit 5          | Unit testing                   |
| Mockito          | Mocking framework              |
| AssertJ Swing    | GUI testing                    |
| Cucumber         | BDD / End-to-end testing       |
| Testcontainers   | Integration testing with MySQL |
| JaCoCo           | Code coverage                  |
| PIT              | Mutation testing               |
| Coveralls        | Coverage reporting             |
| SonarCloud       | Static code analysis           |
| GitHub Actions   | Continuous integration         |
