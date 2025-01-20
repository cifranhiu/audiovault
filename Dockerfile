# Use an official Java runtime as a parent image
FROM eclipse-temurin:21-jdk

# Set the working directory in the container
WORKDIR /app

# Copy the packaged JAR file into the container
COPY build/libs/audiovault-0.0.1-SNAPSHOT.jar audiovault.jar

# Expose the port the app will run on (default Spring Boot port)
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "audiovault.jar"]