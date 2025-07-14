# Utilise une image Java officielle
FROM eclipse-temurin:21-jdk

# Crée un dossier pour l'app
WORKDIR /app

# Copie le build Gradle et les sources
COPY build.gradle settings.gradle ./
COPY gradle gradle
COPY src src

# Copie le wrapper Gradle
COPY gradlew ./
COPY gradle/wrapper gradle/wrapper

# Build l'application (compile le jar)
RUN ./gradlew build -x test

# Expose le port de l'app Spring Boot
EXPOSE 8080

# Lance l'application
CMD ["./gradlew", "bootRun"] 