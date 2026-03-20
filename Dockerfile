# Build Stage
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run Stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Alinhado com o arquivo pom.xml que gera o .jar no momento do build
COPY --from=build /app/target/*.jar app.jar
# Porta configurada no application.yml da aplicação
EXPOSE 3003
ENTRYPOINT ["java", "-jar", "app.jar"]
