# Estágio 1: Compila o projeto Java com Maven e JDK 21
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# 1. Copia apenas o pom.xml primeiro para baixar as dependências (aproveita o cache do Docker)
COPY pom.xml .
RUN mvn dependency:go-offline

# 2. Copia o restante do código-fonte e faz o build
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio 2: Executa a aplicação com JRE 21 leve
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]