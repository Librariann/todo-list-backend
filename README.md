# Growdo Backend

A RESTful API backend for a Todo List application with gamification features including rewards and challenges. Built with Spring Boot 3.5.x, JWT authentication, PostgreSQL, and Flyway migrations.

## Tech Stack

| Category | Technology |
|----------|------------|
| Language | Java 24 |
| Framework | Spring Boot 3.5.7 |
| Database | PostgreSQL |
| ORM | Spring Data JPA / Hibernate |
| Migrations | Flyway |
| Authentication | JWT (jjwt 0.12.6) |
| Security | Spring Security |
| Build Tool | Gradle 8.14.x |
| Utilities | Lombok |

## Project Structure

```
src/main/java/com/librarian/todo_list/
├── auth/                  # Authentication (login, token refresh, logout)
├── user/                  # User management
├── todos/                 # Todo items CRUD
├── rewards/               # Rewards system
├── challenges/            # Challenges/achievements system
├── points/                # User points system (entity)
├── goals/                 # Goals tracking (entities)
├── security/              # JWT & Spring Security configuration
├── common/                # Shared DTOs and base entities
├── exception/             # Global exception handling
├── SecurityConfig.java    # Main security configuration
└── TodoListApplication.java
```

## Prerequisites

- Java 24+
- PostgreSQL 15+
- Gradle 8.x (or use included wrapper)

## Setup

### 1. Clone the repository

```bash
git clone <repository-url>
cd todo-list-backend
```

### 2. Database Setup

Create a PostgreSQL database and schema:

```sql
CREATE DATABASE postgres;
CREATE SCHEMA todo_list;
```

### 3. Configuration

The application uses `src/main/resources/application.yml` for configuration. Key settings:

| Property | Default | Description |
|----------|---------|-------------|
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/postgres?currentSchema=todo_list` | Database connection URL |
| `spring.datasource.username` | `postgres` | Database username |
| `spring.datasource.password` | - | Database password |
| `jwt.secret` | `${JWT_SECRET}` | JWT signing secret key |
| `jwt.expiration` | `86400000` | Access token expiry (24 hours in ms) |
| `jwt.refresh-expiration` | `604800000` | Refresh token expiry (7 days in ms) |

### 4. Environment Variables

| Variable | Required | Description |
|----------|----------|-------------|
| `JWT_SECRET` | Yes (production) | Secret key for JWT token signing. Must be at least 256 bits for HS256. |

### 5. Running the Application

```bash
./gradlew bootRun
```

Or build and run the JAR:

```bash
./gradlew build
java -jar build/libs/todo-list-backend-0.0.1-SNAPSHOT.jar
```

The server starts on `http://localhost:8080` by default.

## Database Migrations

Flyway handles database migrations automatically on startup. Migration files are located in:

```
src/main/resources/db/migration/
```

Migration naming convention: `V{version}__{description}.sql`

The application uses `ddl-auto: validate`, so all schema changes should be handled by Flyway.

## API Overview

All endpoints return responses wrapped in `ApiResponse<T>`:

```json
{
  "success": true,
  "message": "Request processed successfully",
  "data": { },
  "timestamp": "2026-01-15T12:00:00"
}
```

### Authentication

| Endpoint | Method | Auth | Description |
|----------|--------|------|-------------|
| `/api/auth/login` | POST | No | User login |
| `/api/auth/refresh` | POST | Bearer | Refresh access token |
| `/api/auth/logout` | POST | Bearer | Logout (client-side token removal) |

**Login Request:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Login Response:**
```json
{
  "accessToken": "eyJhbG...",
  "refreshToken": "eyJhbG...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "nickname": "username",
  "email": "user@example.com"
}
```

### Users

| Endpoint | Method | Auth | Description |
|----------|--------|------|-------------|
| `/api/users/register` | POST | No | Register new user |
| `/api/users/me` | GET | Bearer | Get current user info |
| `/api/users/{id}` | GET | Bearer | Get user by ID |
| `/api/users/username/{username}` | GET | Bearer | Get user by username |
| `/api/users/active` | GET | Bearer | Get all active users |
| `/api/users/check-username/{username}` | GET | No | Check username availability |
| `/api/users/check-email/{email}` | GET | No | Check email availability |
| `/api/users/health` | GET | No | Health check |

### Todos

| Endpoint | Method | Auth | Description |
|----------|--------|------|-------------|
| `/api/todos/` | GET | Bearer | List all todos |
| `/api/todos/register` | POST | Bearer | Create new todo |
| `/api/todos/{id}` | PATCH | Bearer | Update todo |
| `/api/todos/{id}` | DELETE | Bearer | Delete todo |

**Todo Statuses:** `READY`, `PROCESS`, `DONE`

### Rewards

| Endpoint | Method | Auth | Description |
|----------|--------|------|-------------|
| `/api/rewards/` | GET | Bearer | List all rewards |
| `/api/rewards/{id}` | GET | Bearer | Get reward by ID |
| `/api/rewards/register` | POST | Bearer | Create reward |
| `/api/rewards/{id}` | PATCH | Bearer | Update reward |
| `/api/rewards/{id}` | DELETE | Bearer | Delete reward |

### User Rewards

| Endpoint | Method | Auth | Description |
|----------|--------|------|-------------|
| `/api/user/rewards/` | GET | Bearer | List user's rewards |
| `/api/user/rewards/{rewardId}/redeem` | POST | Bearer | Redeem a reward |
| `/api/user/rewards/{id}` | PATCH | Bearer | Mark reward as used |

### Challenges

| Endpoint | Method | Auth | Description |
|----------|--------|------|-------------|
| `/api/challenges/` | GET | Bearer | List all challenges |
| `/api/challenges/register` | POST | Bearer | Create challenge |
| `/api/challenges/{id}` | PATCH | Bearer | Update challenge |
| `/api/challenges/{id}` | DELETE | Bearer | Delete challenge |

### User Challenge Progress

| Endpoint | Method | Auth | Description |
|----------|--------|------|-------------|
| `/api/user/challenges/progress` | POST | Bearer | Update challenge progress |

## Authentication

The API uses JWT (JSON Web Tokens) for authentication.

### Making Authenticated Requests

Include the access token in the `Authorization` header:

```
Authorization: Bearer <access_token>
```

### Public Endpoints (No Auth Required)

- `POST /api/auth/**`
- `POST /api/users/register`
- `GET /api/users/check-username/**`
- `GET /api/users/check-email/**`
- `GET /api/users/health`

All other endpoints require authentication.

## Error Handling

The API uses consistent error responses:

```json
{
  "success": false,
  "message": "Error description",
  "data": null,
  "timestamp": "2026-01-15T12:00:00"
}
```

### HTTP Status Codes

| Status | Meaning |
|--------|---------|
| 200 | Success |
| 201 | Created |
| 400 | Bad Request / Validation Error |
| 401 | Unauthorized |
| 404 | Not Found |
| 405 | Method Not Allowed |
| 409 | Conflict (e.g., duplicate user) |
| 500 | Internal Server Error |

### Validation Errors

Validation failures return field-specific errors:

```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "email": "Invalid email format",
    "password": "Password must be at least 8 characters"
  },
  "timestamp": "2026-01-15T12:00:00"
}
```

## CORS Configuration

CORS is configured to allow:

- **Origins:** `http://localhost:3000`
- **Methods:** GET, POST, PUT, DELETE, PATCH, OPTIONS
- **Headers:** All headers allowed
- **Credentials:** Enabled

## Development

### Running Tests

```bash
./gradlew test
```

### Building

```bash
./gradlew build
```

### Code Style

- Lombok for boilerplate reduction
- Spring Data JPA repositories
- DTOs for request/response separation
- Global exception handling via `@RestControllerAdvice`
