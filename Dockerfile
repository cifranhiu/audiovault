# ---- Build Stage ----
FROM gradle:8-jdk AS build
WORKDIR /app

# Copy only necessary Gradle files first for caching dependencies
COPY gradle gradle
COPY build.gradle settings.gradle ./
# Download dependencies to leverage caching
RUN gradle dependencies
# Copy the rest of the project
COPY . .
# Run clean build (Ensure Gradle wrapper exists or use system Gradle)
RUN ./gradlew clean build -x test

# ---- Runtime Stage ----
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Install FFmpeg and other dependencies
# RUN apt-get update
# RUN apt-get install -y ffmpeg
RUN apk add --no-cache ffmpeg

# Copy the JAR file from the build stage
COPY --from=build /app/build/libs/audiovault-0.0.1-SNAPSHOT.jar audiovault.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "audiovault.jar"]
