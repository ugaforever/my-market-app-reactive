# Веб-приложение «Витрина интернет-магазина» с использованием Spring Boot на блокирующем стеке технологий

Проект создан в рамках обучения на курсе **Мидл Java-разработчик** от **practicum.yandex.ru**.

## Чек-лист требований к приложению
- [x] Приложение должно быть написано на Spring Boot.
- [x] Код приложения хранится в Git-репозитории на GitHub.
- [x] Сборка приложения осуществляется с помощью систем сборки Maven или Gradle.
- [x] Код приложения написан на Java 21.
- [x] Web UI приложения должен использовать проект Spring Web MVC и подключаться через соответствующий стартер.
- [x] Приложение должно использовать Spring Data JPA и Hibernate ORM под капотом для доступа к данным в базе данных.
- [x] База данных приложения может быть как персистентной (например, PostgreSQL), так и в памяти (например, H2).
- [x] Приложение можно собрать из исходников и запустить локально.
- [x] Приложение должно быть упаковано в Executable JAR и запускаться в Embedded Servlet Container (Tomcat или Jetty).
- [x] Приложение должно быть покрыто тестами (юнит-, интеграционными) с использованием JUnit 5, TestContext Framework, Spring Boot Test и кеширования контекстов.
- [x] Executable JAR должен быть упакован в Docker-контейнер с открытым веб-портом для доступа из браузера (например, 9090).

##  Сборка и запуск в среде разработки

```bash
# Запуск
mvn spring-boot:run

# Перейти на страницу сайта
http://localhost:9090/
http://localhost:9090/items
```

##  Сборка и запуск локально

```bash
# В корневую директорию проекта
cd ./my-market-app

# Сборка JAR
mvn clean package

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
mvn clean package

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



