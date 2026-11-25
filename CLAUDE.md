# CLAUDE.md

Pet project: REST backend for school attendance and user management.
Java 21, Spring Boot 3.3.5, Spring Data JPA (Hibernate), MySQL, Spring Security + JWT (jjwt 0.11.5), MapStruct, Lombok, Gradle 9.5.1 (Kotlin DSL).

## Commands

```bash
./gradlew test                                   # all tests (H2, no MySQL needed)
./gradlew test --tests "*.UserServiceImplTest"   # one test class
./gradlew build                                  # compile + tests + build/libs/app.jar
./gradlew bootRun                                # run locally (needs local MySQL, see below)

docker compose run --rm tests                    # tests inside Docker (reports -> build/docker-test-reports)
docker compose up --build                        # app on :8080 + MySQL 8.4 on host port 3307
docker compose down                              # stop (add -v to drop the MySQL volume)
```

- Local run: MySQL `localhost:3306/smart_school_attendance`, credentials in `src/main/resources/application.properties`. Override without editing the file via env `SPRING_DATASOURCE_URL` / `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD`.
- Docker: MySQL root password is `${DB_PASSWORD:-root}`; data lives in the `mysql-data` volume.
- Schema is managed by `ddl-auto=update` — no migrations (no Flyway/Liquibase).
- `/actuator/health` is open (security only covers `/api/v1/**`).

## Layout

Base package: `org.example.smart_school_attendance`

- `controller/<domain>/` — REST controllers, all under `/api/v1`. Separate `*UpdateController` for field-level updates (email/phone/password).
- `service/<domain>/` — interfaces; implementations in `service/<domain>/impl/*Impl` (exception: `service/classRoom/ClassRoomServiceImpl` sits next to its interface). Split: `XService` (CRUD) + `XUpdateService` (field changes).
- `dto/<domain>/request|response/` — Java `record`s with Jakarta Validation annotations. Naming: `*RequestDTO` / `*ResponseDTO`.
- `entity/` — JPA entities (Lombok `@Data`, explicit no-arg constructor, `@PrePersist`/`@PreUpdate` set `createDate`/`updateDate`, `equals/hashCode` by id).
- `mapper/` — MapStruct interfaces, `@Mapper(componentModel = "spring")`. Custom fields via `@Mapping(expression = "java(...)")` + `default` methods.
- `repository/` — Spring Data JPA.
- `security/` — `JwtService`, `JwtAuthenticationFilter`, `SecurityUser`, `SecurityService` (current user helpers + school access checks).
- `exception/GlobalExceptionHandler` — maps exceptions to `ApiErrorResponse`.

Domain: `User` (`Role`: PARENT, CHILD, TEACHER, SCHOOL_ADMIN, SYSTEM_ADMIN, PENDING; `UserStatus`: PENDING, ACTIVE, INACTIVE, DISABLED), `School` (geo `latitude`/`longitude`/`radius`), `ClassRoom` (one teacher via `@OneToOne` → unique `teacher_id`, `capacity` ≥ 10), `ChildDetails` (shares PK with child `User` via `@MapsId`, many-to-many `parents`, `ChildLocationStatus`).

## Conventions

- Constructor injection with `@Autowired` on the constructor (no `@RequiredArgsConstructor` in services/controllers).
- Avoid service ↔ service cycles (Spring forbids them and the app won't start). If service A needs B and B needs A, one side uses the repository directly (see `ClassRoomServiceImpl` → `ChildDetailsRepository`).
- Authorization: `@PreAuthorize("hasRole(...)")` on controller methods (`@EnableMethodSecurity`). Public endpoints: `/api/v1/login`, `/api/v1/register`. Ownership checks live in services (`SecurityService.validateSchoolAccess`, `hasAccess(userId)` in `UserUpdateServiceImpl`).
- "Me" endpoints (`/api/v1/users/me/...`) take the user id from `SecurityService.getCurrentUserId()`, not from the path.
- Error handling via exceptions: `IllegalArgumentException` → 400, `IllegalStateException` → 409, anything else → 500. Services validate inputs and throw these; don't return error objects. There is no handler for `AccessDeniedException` / `MethodArgumentNotValidException`, so they currently become 500.
- JWT: subject = email; claims `userId`, `email`, `role`, `active` (user status). A token is valid only when `active == ACTIVE`, so newly registered (PENDING) users cannot use their token until a SYSTEM_ADMIN sets status via `PUT /api/v1/users/{id}/status` and they log in again.
- Passwords are always stored via `PasswordEncoder` (BCrypt bean in `PasswordConfig`).
- Logging: Lombok `@Slf4j`, `log.info` on start/success of service operations, `log.warn` on rejected attempts. Config in `logback.xml`.
- Write-service methods are `@Transactional`.

## Testing

- Unit tests (`service/**`, `security/**`, `mapper/**`): JUnit 5 + `@ExtendWith(MockitoExtension.class)` + `@Mock`, the `*Impl` is built manually in `@BeforeEach`; AssertJ assertions; group cases with `@Nested`. Mappers are tested via `Mappers.getMapper(...)` (no Spring).
- DB tests run on H2 in MySQL mode, profile `test` (`src/test/resources/application-test.properties`):
  - `repository/RepositoryTest` — `@DataJpaTest @ActiveProfiles("test") @AutoConfigureTestDatabase(replace = NONE)`.
  - `integration/ServiceFlowIntegrationTest` — `@SpringBootTest @ActiveProfiles("test") @Transactional` with real services.
  - Every `@SpringBootTest` / `@DataJpaTest` must have `@ActiveProfiles("test")`, otherwise it tries to connect to MySQL.
- Test data builders: `TestData` (`user`, `school`, `registerRequest`, `adminCreateRequest`, `phone`).
- Known bugs are captured as tests with `@Disabled("BUG: ...")` that assert the correct behavior. When fixing a bug, remove `@Disabled` from its test. To see all of them fail at once, temporarily add `src/test/resources/junit-platform.properties` with `junit.jupiter.conditions.deactivate=org.junit.*DisabledCondition` (don't commit it).
- Mockito strict stubs are on: don't stub calls the code under test doesn't make.

## Known issues / TODO

- Open bugs: `grep -rn '@Disabled("BUG' src/test`.
- No way to bootstrap via API: creating schools and users needs SYSTEM_ADMIN, and public registration needs an existing school. The first admin/school must be inserted manually.
- Secrets are committed: DB password in `application.properties`, JWT `SECRET_KEY` hardcoded in `JwtService`. Should move to env vars.
- `JwtAuthenticationFilter` creates its own `new JwtService()` instead of using the bean.
- `mysql-connector-j` is declared twice in `build.gradle.kts` (runtimeOnly + implementation 9.0.0).
- No attendance logic yet (`ChildLocationStatus` exists but nothing updates it); no controllers for class rooms / child details yet.
