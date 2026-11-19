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
