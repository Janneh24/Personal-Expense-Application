# Personal Expense Application

A Java desktop application for tracking and managing personal expenses, built with Maven and backed by MySQL.

## Badges

[![CI Build](https://github.com/Janneh24/Personal-Expense-Application/actions/workflows/ci.yml/badge.svg)](https://github.com/Janneh24/Personal-Expense-Application/actions/workflows/ci.yml)
[![Coverage Status](https://coveralls.io/repos/github/Janneh24/Personal-Expense-Application/badge.svg?branch=main)](https://coveralls.io/github/Janneh24/Personal-Expense-Application?branch=main)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Janneh24_Personal-Expense-Application&metric=alert_status)](https://sonarcloud.io/dashboard?id=Janneh24_Personal-Expense-Application)

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
java -jar target/personal-expense-application-1.0.0-SNAPSHOT.jar
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
| JUnit            | Unit testing                   |
| JaCoCo           | Code coverage                  |
| Coveralls        | Coverage reporting             |
| SonarCloud       | Static code analysis           |
| GitHub Actions   | Continuous integration         |
| Eclipse / M2E    | IDE support                    |
