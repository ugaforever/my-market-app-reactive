FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY target/*.jar app.jar

# Открываем порт 9090
EXPOSE 9090

ENTRYPOINT ["java", "-jar", "app.jar"]