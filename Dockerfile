# Use the latest Java 23 EA build (if available)
FROM openjdk:23-ea-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the application JAR to the container
COPY target/*.jar app.jar

# Expose the application port
EXPOSE 3333

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
