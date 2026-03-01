FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copy pom.xml first for dependency caching
COPY pom.xml ./

# Download dependencies
RUN apk add --no-cache maven && mvn dependency:go-offline -q

# Copy source code
COPY src/ src/

# Build the jar
RUN mvn clean package -DskipTests -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]