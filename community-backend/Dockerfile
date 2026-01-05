# Build stage
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copy parent pom
COPY pom.xml .

# Copy module poms
COPY core/pom.xml core/pom.xml
COPY fo-api/pom.xml fo-api/pom.xml
COPY bo-api/pom.xml bo-api/pom.xml

# Copy source code
COPY core/src core/src
COPY fo-api/src fo-api/src
COPY bo-api/src bo-api/src

# Build
RUN mvn clean package -pl fo-api -am -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/fo-api/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
