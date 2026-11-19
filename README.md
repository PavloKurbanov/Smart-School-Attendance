Markdown
# Smart School Attendance

Backend service for a school attendance and user management system built with **Java**, **Spring Boot**, **Spring Data JPA**, and **MySQL**.

## Overview

The application manages school-related domain data and role-based user accounts (`PARENT`, `CHILD`, `TEACHER`, `SCHOOL_ADMIN`, `SYSTEM_ADMIN`, `PENDING`), including associations between users, schools, classrooms, student profiles, and parent-child relationships.

## Tech Stack

- **Language:** Java
- **Framework:** Spring Boot
- **Persistence:** Spring Data JPA, Hibernate
- **Database:** MySQL
- **Validation & Boilerplate:** Jakarta Validation, Lombok
- **Build Tool:** Gradle

## Project Structure

```text
src/main/java/org/example/smart_school_attendance
├── dto/          # Request and response DTO records
├── entity/       # JPA entities, enums, and lifecycle callbacks
├── mapper/       # Entity <-> DTO mappers
├── repository/   # Spring Data JPA repositories
└── service/      # Business logic and @Transactional services
Prerequisites
JDK (matching the project's Gradle/Java configuration)

MySQL running locally or remotely

Gradle (or Gradle Wrapper ./gradlew)

Database Configuration
Create a MySQL database (for example, smart_school_attendance) and configure your datasource properties in src/main/resources/application.properties or application.yml:

Properties
spring.datasource.url=jdbc:mysql://localhost:3306/smart_school_attendance
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
Running the Application
Run the project with Gradle:

Bash
./gradlew bootRun
Or on Windows:

Bash
gradlew.bat bootRun
Current Status & Next Steps
Core domain model (User, Role, school/classroom/child associations), DTOs, mappers, repositories, and transactional service methods are actively being developed.

Planned/ongoing work: completing registration and parent-child linking flows, exposing REST endpoints, global exception handling, security/role checks, and unit/integration tests.
