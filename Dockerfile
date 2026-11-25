# ---- build: dependencies + sources. Also used to run tests: docker compose run --rm tests
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle gradle
RUN chmod +x gradlew && ./gradlew --no-daemon dependencies > /dev/null
COPY src src
CMD ["./gradlew", "--no-daemon", "test"]

# ---- package: executable jar (tests are run separately)
FROM build AS package
RUN ./gradlew --no-daemon bootJar

# ---- runtime: JRE only
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app
COPY --from=package /app/build/libs/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
