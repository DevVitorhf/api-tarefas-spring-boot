# ETAPA 1 - Compilar a aplicação
FROM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /app

COPY pom.xml .

COPY src ./src

RUN mvn clean package -DskipTests

# ETAPA 2 - Executar a aplicação
FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build /app/target/v2aprendendospring-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]