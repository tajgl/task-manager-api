# Task Manager API

A RESTful API for managing tasks and projects, built with Spring Boot 4. Supports JWT-based authentication, task filtering, sorting, pagination, and project-level risk assessment.

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 4.0.1 |
| Language | Java 17 |
| Security | Spring Security + JWT (jjwt 0.12.6) |
| Persistence | Spring Data JPA + PostgreSQL |
| Validation | Jakarta Bean Validation |
| Build Tool | Maven |
| Utilities | Lombok |

## Features

- **JWT Authentication** — register and log in to receive a bearer token
- **Project Management** — create, read, update, and delete projects
- **Task Management** — full CRUD with status, priority, due date, and project association
- **Filtering** — filter tasks by status, priority, title search, or project
- **Sorting** — sort tasks by any field including custom priority ordering (LOW → MEDIUM → HIGH)
- **Pagination** — paginated responses with metadata (page, size, total elements, total pages)
- **Risk Assessment** — per-project risk assessment with history tracking

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL (running locally or via Docker)

### 1. Clone the repository

```bash
git clone https://github.com/tajgl/task-manager-api.git
cd task-manager-api
```

### 2. Configure the database

Copy the example config and fill in your values:

```bash
cp src/main/resources/application.yml.example src/main/resources/application.yml
```

Edit `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/taskmanager
    username: your_db_username
    password: your_db_password

jwt:
  secret: your_jwt_secret_key
  expiration: 86400000
```

### 3. Run the application

```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`.

## API Reference

### Authentication

All endpoints except `/api/auth/**` require a `Bearer` token in the `Authorization` header.

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auth/register` | Register a new user |
| `POST` | `/api/auth/login` | Log in and receive a JWT |

**Register request body:**
```json
{
  "username": "john",
  "password": "secret123"
}
```

**Login response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

### Projects

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/projects` | Create a project |
| `GET` | `/api/projects` | Get all projects |
| `GET` | `/api/projects/{id}` | Get project by ID |
| `PUT` | `/api/projects/{id}` | Update a project |
| `DELETE` | `/api/projects/{id}` | Delete a project |
| `GET` | `/api/projects/{id}/tasks` | Get all tasks for a project |
| `GET` | `/api/projects/{id}/risk-assessment` | Get project risk assessment |
| `GET` | `/api/projects/{id}/risk-assessment/history` | Get risk assessment history |

---

### Tasks

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/tasks` | Create a task |
| `GET` | `/api/tasks` | Get all tasks (supports filters, sorting, pagination) |
| `GET` | `/api/tasks/{id}` | Get task by ID |
| `PUT` | `/api/tasks/{id}` | Update a task |
| `DELETE` | `/api/tasks/{id}` | Delete a task |

#### Task Query Parameters

| Parameter | Type | Description | Example |
|---|---|---|---|
| `status` | enum | Filter by status | `TODO`, `IN_PROGRESS`, `COMPLETED` |
| `priority` | enum | Filter by priority | `LOW`, `MEDIUM`, `HIGH` |
| `search` | string | Filter by title (case-insensitive) | `search=bug` |
| `projectId` | long | Filter by project | `projectId=1` |
| `sortBy` | string | Sort field | `sortBy=dueDate` |
| `order` | string | Sort direction | `asc` or `desc` |
| `page` | int | Page number (0-indexed) | `page=0` |
| `size` | int | Page size | `size=10` |

**Example — paginated tasks filtered by status:**
```
GET /api/tasks?status=IN_PROGRESS&page=0&size=10
```

**Create task request body:**
```json
{
  "title": "Fix login bug",
  "description": "Users are unable to log in on mobile",
  "dueDate": "2026-06-30",
  "priority": "HIGH",
  "status": "TODO",
  "projectId": 1
}
```

## Data Model

### Task

| Field | Type | Notes |
|---|---|---|
| `id` | Long | Auto-generated |
| `title` | String | Required, max 200 chars |
| `description` | String | Optional, max 1000 chars |
| `owner` | String | Set from authenticated user |
| `status` | Enum | `TODO`, `IN_PROGRESS`, `COMPLETED` — defaults to `TODO` |
| `priority` | Enum | `LOW`, `MEDIUM`, `HIGH` — defaults to `MEDIUM` |
| `dueDate` | LocalDate | Optional |
| `createdAt` | LocalDateTime | Auto-set on creation |
| `projectId` | Long | Optional FK to project |

### Project

| Field | Type | Notes |
|---|---|---|
| `id` | Long | Auto-generated |
| `name` | String | Required |
| `description` | String | Optional |
| `owner` | String | Set from authenticated user |
| `tasks` | List\<Task\> | One-to-many relationship |

## Project Structure

```
src/
└── main/
    └── java/com/taj/taskmanager/
        ├── controller/       # REST controllers
        ├── service/          # Business logic
        ├── repository/       # JPA repositories
        ├── model/            # JPA entities
        ├── dto/              # Request/response DTOs
        ├── mapper/           # Entity ↔ DTO mappers
        ├── security/         # JWT filter, config
        └── exception/        # Custom exceptions & handlers
```

## Running Tests

```bash
mvn test
```
