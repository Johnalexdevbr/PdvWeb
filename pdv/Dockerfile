# Estágio de construção (Build)
FROM maven:3.9.6-eclipse-temurin-17 AS build
COPY . .
# Adicionamos a flag para ignorar o erro de codificação (MalformedInputException)
RUN mvn clean package -DskipTests -Dmaven.resources.filtering=false

# Estágio de execução
FROM eclipse-temurin:17-jdk-jammy
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
