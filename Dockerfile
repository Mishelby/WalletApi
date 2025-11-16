FROM gradle:8.14.3-jdk17 AS build
WORKDIR /app

COPY build.gradle settings.gradle ./
RUN gradle build --no-daemon -x test || true

COPY src ./src

RUN gradle bootJar --no-daemon -x test

FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
