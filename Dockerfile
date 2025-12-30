FROM eclipse-temurin:17-jdk

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy the JAR file
COPY target/*.jar /app.jar


# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application with better JVM settings
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "/app.jar"]
