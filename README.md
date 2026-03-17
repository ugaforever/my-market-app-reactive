# Веб-приложение «Витрина интернет-магазина» с использованием Spring Boot в реактивном стиле

Проект создан в рамках обучения на курсе **Мидл Java-разработчик** от **practicum.yandex.ru**.

## Задание
Перепишите веб-приложение «Витрина интернет-магазина» с использованием Spring Boot на реактивном стеке технологий

Витрина на блокирующем стеке https://github.com/ugaforever/my-market-app

## Стек
- Java 21, Maven
- Spring Boot, WebFlux (Reactor: Mono/Flux), Thymeleaf
- Spring Data R2DBC, H2
- JUnit 5, WebTestClient

## Требования
- JDK 21
- Maven или Maven Wrapper из проекта

##  Сборка и запуск в среде разработки

```bash
# Запуск
./mvnw spring-boot:run

# Перейти на страницу сайта
http://localhost:9090/
http://localhost:9090/items
```

##  Сборка и запуск локально

```bash
# В корневую директорию проекта
cd ./my-market-app

# Сборка JAR
./mvnw clean package

# Запуск
java -jar ./target/my-market-app-0.0.1-SNAPSHOT.jar

# Перейти на страницу сайта
http://localhost:9090/
http://localhost:9090/items
```

##  Сборка и запуск в контейнере Docker

```bash
# В корневую директорию проекта
cd ./my-market-app

# Сборка проекта
./mvnw clean package

# Сборка Docker образа с тегом my-market-app:latest
docker build -t my-market-app:latest .

# Запуск контейнера в фоновом режиме
docker run -d --name market -p 9090:9090 my-market-app:latest
# ИЛИ
# Запуск контейнера и автоудаление после остановки
docker run --rm -p 9090:9090 my-market-app:latest

# Перейти на страницу сайта
http://localhost:9090/
http://localhost:9090/items
```



