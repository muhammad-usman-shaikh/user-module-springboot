# User Module – Spring Boot

This is a basic user authentication and management module built with **Spring Boot** and **MySQL**.  
It includes essential features like:

- User signup and login
- Profile retrieval and update
- Email-based OTP verification (no SMS)
- Refresh token–based session management
- MySQL database integration
- Auto-managed `created_at` and `updated_at` timestamps
- Flyway-based database migrations

## Tech Stack

- Java 21 (OpenJDK 21.0.8)
- Spring Boot
- Spring Security
- Spring Data JPA
- Flyway
- MySQL
- Maven

## Project Features

- `User` table with fields like name, email, password, `is_email_verified`, and `is_2fa_enabled`
- `Session` table to store user sessions with refresh tokens
- `Otp` table to manage one-time email verification codes
- JPA entities mapped with proper relationships
- Auto timestamps handled via `@PrePersist` and `@PreUpdate`
- Database schema versioning using Flyway (SQL migrations)
- Secure password storage (BCrypt recommended)
- Clean and extendable codebase

## Requirements

- Java 21+
- Maven 3.8+
- MySQL 8+

## Run the Project

```bash
./mvnw spring-boot:run
```

Or if you're using VS Code, just click **Run** on the main class or run it from the terminal.
