# Project Management API

A production-style REST API built with **Java 21** and **Spring Boot** for managing projects and employees with relational persistence.

The application supports:

- creating employees;
- creating projects associated with one or more employees;
- retrieving employees;
- retrieving individual projects;
- listing projects with their associated employees.

This project focuses on clean backend design, explicit API contracts, relational data modeling, validation, automated testing, database versioning, and reproducible local infrastructure.

---

## Tech Stack

- Java 21
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA / Hibernate
- PostgreSQL 17
- Flyway
- Bean Validation
- Maven
- Docker Compose
- JUnit 5
- Mockito
- Testcontainers
- OpenAPI / Swagger

---

## Architecture

The application follows a layered architecture:

```text
HTTP Request
     ↓
Controller
     ↓
Service
     ↓
Repository
     ↓
JPA / Hibernate
     ↓
PostgreSQL
```

Main package structure:

```text
src/main/java/com/brunacosta/projectmanagement
├── controller
├── dto
├── entity
├── exception
├── repository
└── service
```

DTOs are kept separate from persistence entities so that the external API contract is not directly coupled to the database model.

---

## Domain Model

A project can contain multiple employees, and an employee can participate in multiple projects.

The many-to-many relationship is represented by an explicit association table:

```text
project
   │
   │ N
   │
project_employee
   │
   │ N
   │
employee
```

Database tables:

- `project`
- `employee`
- `project_employee`

The `project_employee` table uses the combination of `project_id` and `employee_id` as its primary key.

Database constraints include:

- unique CPF;
- unique email;
- salary greater than or equal to zero;
- referential integrity between projects and employees.

The schema is versioned with **Flyway**, while Hibernate uses `ddl-auto: validate`, keeping schema evolution under explicit migration control.

---

## Running the Application

### Prerequisites

Make sure you have:

- Java 21
- Maven
- Docker
- Docker Compose

### 1. Configure environment variables

The project provides a `.env.example` file.

Create a local `.env` file:

#### PowerShell

```powershell
Copy-Item .env.example .env
```

#### Linux / macOS

```bash
cp .env.example .env
```

The file should contain:

```env
POSTGRES_DB=project_management
POSTGRES_USER=project_user
POSTGRES_PASSWORD=project_password
```

> `.env` is ignored by Git and should not be committed.

### 2. Start PostgreSQL

From the project root:

```bash
docker compose up -d
```

Check the container status:

```bash
docker compose ps
```

### 3. Configure variables for the application

#### PowerShell

```powershell
$env:POSTGRES_DB="project_management"
$env:POSTGRES_USER="project_user"
$env:POSTGRES_PASSWORD="project_password"
```

#### Linux / macOS

```bash
export POSTGRES_DB=project_management
export POSTGRES_USER=project_user
export POSTGRES_PASSWORD=project_password
```

### 4. Run the application

```bash
mvn spring-boot:run
```

The API will be available at:

```text
http://localhost:8080
```

---

## API Endpoints

### Create Employee

```http
POST /api/v1/employees
```

Example request:

```json
{
  "name": "Ana Silva",
  "cpf": "12345678901",
  "email": "ana@example.com",
  "salary": 15000.00
}
```

Expected response:

```http
201 Created
```

### Get Employee

```http
GET /api/v1/employees/{id}
```

### Create Project

```http
POST /api/v1/projects
```

Example request:

```json
{
  "name": "Project Management API",
  "employeeIds": [1, 2]
}
```

Expected response:

```http
201 Created
```

### Get Project

```http
GET /api/v1/projects/{id}
```

### List Projects

```http
GET /api/v1/projects
```

The response contains projects and their associated employees.

---

## Validation and Error Handling

The API uses **Bean Validation** for request validation.

Implemented validations include:

- required employee name;
- required CPF with 11 digits;
- valid email format;
- required and non-negative salary;
- employee IDs referenced by a project must exist.

Exceptions are handled centrally, producing consistent HTTP responses.

Examples:

- `400 Bad Request` — invalid request data;
- `404 Not Found` — requested resource does not exist;
- `409 Conflict` — data conflict such as unique-field violations.

---

## Testing

Run the full test suite with:

```bash
mvn test
```

### Unit Tests

Service-layer behavior is tested in isolation using **JUnit 5** and **Mockito**.

Covered scenarios include:

- employee creation;
- duplicate CPF validation;
- duplicate email validation;
- project creation with employees;
- project creation with a non-existent employee.

### Integration Tests

Integration tests use **Testcontainers** to automatically start an isolated PostgreSQL 17 instance.

The tests exercise the application stack together:

```text
HTTP
 ↓
Controller
 ↓
Service
 ↓
Repository
 ↓
Hibernate
 ↓
Flyway
 ↓
PostgreSQL
```

The integration-test database is independent from the database configured for normal application execution.

With Docker available, the tests can be executed directly:

```bash
mvn test
```

No manually configured PostgreSQL instance is required for the integration tests.

---

## Technical Decisions

### PostgreSQL Instead of an In-Memory Database

PostgreSQL is used both by the application and by integration tests.

This reduces the risk of database-specific behavior being hidden by differences between an in-memory database and the actual relational database used by the application.

### Flyway for Database Versioning

Database creation and evolution are controlled through versioned migrations.

Hibernate validates the entity model against the existing schema instead of creating or modifying the schema automatically.

### Project / Employee Relationship

The current relationship has no attributes of its own, so it is modeled with `@ManyToMany` while keeping an explicit normalized association table in the database.

If the relationship later requires additional information — such as project role, allocation date, workload, or status — the association can evolve into a dedicated entity.

### BigDecimal for Monetary Values

Salary values are represented with `BigDecimal` to avoid precision issues associated with binary floating-point types.

### DTOs Separate from JPA Entities

JPA entities are not exposed directly through the API.

Dedicated request and response DTOs keep the HTTP contract independent from the persistence model and avoid unnecessary serialization coupling.

### Optimized Project Queries

Project queries use `EntityGraph` when employee data is required.

This makes the fetch strategy explicit for the use case and avoids unnecessary additional queries while keeping the relationship from being globally eager.

---

## API Documentation

With the application running, interactive API documentation is available through Swagger UI:

- Swagger UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

Swagger UI can be used to inspect the API contract and execute requests directly from the browser.

---

## What This Project Demonstrates

This project showcases practical backend engineering with:

- Java 21 and Spring Boot;
- REST API design;
- layered application architecture;
- relational modeling with PostgreSQL;
- JPA / Hibernate;
- database migrations with Flyway;
- request validation and centralized exception handling;
- automated unit and integration testing;
- Testcontainers-based infrastructure testing;
- Docker Compose for reproducible local setup;
- OpenAPI / Swagger documentation.

---

## Author

**Bruna Costa**

Senior Java Backend Developer  
Java • Spring Boot • Microservices • REST APIs • PostgreSQL • Kafka • AWS
