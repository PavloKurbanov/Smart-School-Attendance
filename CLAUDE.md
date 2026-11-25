# CLAUDE.md

Pet project: REST backend for school attendance and user management.
Java 21, Spring Boot 3.3.5, Spring Data JPA (Hibernate), MySQL, Spring Security + JWT (jjwt 0.11.5), MapStruct, Lombok, Gradle (Kotlin DSL).

## Commands

```bash
./gradlew bootRun          # run (needs local MySQL, see below)
./gradlew test             # all tests
./gradlew test --tests "*.UserServiceImplTest"   # one test class
./gradlew build
```

- `gradle/wrapper/gradle-wrapper.jar` is NOT in git (`*.jar` is in `.gitignore`), so on a fresh clone `./gradlew` fails. Regenerate it with a local Gradle (`gradle wrapper`) or run from IntelliJ.
- MySQL: `localhost:3306/smart_school_attendance`, credentials in `src/main/resources/application.properties`. Schema is managed by `ddl-auto=update` — no migrations (no Flyway/Liquibase).
- `SmartSchoolAttendanceApplicationTests` is a `@SpringBootTest` and needs a running MySQL. Service unit tests do not.

## Layout

Base package: `org.example.smart_school_attendance`

- `controller/<domain>/` — REST controllers, all under `/api/v1`. Separate `*UpdateController` for field-level updates (email/phone/password).
- `service/<domain>/` — interfaces; implementations in `service/<domain>/impl/*Impl`. Same split: `XService` (CRUD) + `XUpdateService` (field changes).
- `dto/<domain>/request|response/` — Java `record`s with Jakarta Validation annotations. Naming: `*RequestDTO` / `*ResponseDTO`.
- `entity/` — JPA entities (Lombok `@Data`, explicit no-arg constructor, `@PrePersist`/`@PreUpdate` set `createDate`/`updateDate`, `equals/hashCode` by id).
- `mapper/` — MapStruct interfaces, `@Mapper(componentModel = "spring")`.
- `repository/` — Spring Data JPA.
- `security/` — `JwtService`, `JwtAuthenticationFilter`, `SecurityUser`, `SecurityService` (current user helpers + school access checks).
- `exception/GlobalExceptionHandler` — maps exceptions to `ApiErrorResponse`.

Domain: `User` (with `Role`: PARENT, CHILD, TEACHER, SCHOOL_ADMIN, SYSTEM_ADMIN, PENDING; `UserStatus`: PENDING, ACTIVE, INACTIVE, DISABLED), `School` (has geo `latitude`/`longitude`/`radius`), `ClassRoom`, `ChildDetails` (shares PK with child `User` via `@MapsId`, many-to-many `parents`, `ChildLocationStatus`).

## Conventions

- Constructor injection with `@Autowired` on the constructor (no `@RequiredArgsConstructor` in services/controllers).
- Authorization: `@PreAuthorize("hasRole(...)")` on controller methods (`@EnableMethodSecurity`). Public endpoints: `/api/v1/login`, `/api/v1/register`. Ownership checks live in services (`SecurityService.validateSchoolAccess`, `hasAccess(userId)` in `UserUpdateServiceImpl`).
- "Me" endpoints (`/api/v1/users/me/...`) take the user id from `SecurityService.getCurrentUserId()`, not from the path.
- Error handling via exceptions: `IllegalArgumentException` → 400, `IllegalStateException` → 409, anything else → 500. Note: there is no handler for `AccessDeniedException`, so the catch-all `Exception` handler turns it (and `MethodArgumentNotValidException` from `@Valid`) into 500 instead of 403/400. Services validate inputs and throw these; don't return error objects.
- JWT: subject = email; claims `userId`, `email`, `role`, `active` (user status). A token is valid only when `active == ACTIVE`, so newly registered (PENDING) users cannot use their token until a SYSTEM_ADMIN sets status via `PUT /api/v1/users/{id}/status`.
- Passwords are always stored via `PasswordEncoder` (BCrypt bean in `PasswordConfig`).
- Logging: Lombok `@Slf4j`, `log.info` on start/success of service operations, `log.warn` on rejected attempts. Config in `logback.xml`.
- Write-service methods are `@Transactional`.
- Unit tests: plain JUnit 5 + Mockito, build the `*Impl` manually with mocks in the test constructor (no Spring context).
- Code comments in the codebase are sometimes in Ukrainian — that's fine.

## Known issues / TODO

- Secrets are committed: DB password in `application.properties`, JWT `SECRET_KEY` hardcoded in `JwtService`. Should move to env vars.
- `JwtAuthenticationFilter` creates its own `new JwtService()` instead of using the bean.
- `mysql-connector-j` is declared twice in `build.gradle.kts` (runtimeOnly + implementation 9.0.0).
- No attendance logic yet (`ChildLocationStatus` exists but nothing updates it); planned: parent-child linking, classroom endpoints, more tests.
