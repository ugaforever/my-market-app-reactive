# Веб-приложение «Витрина интернет-магазина» с использованием Spring Boot в реактивном стиле c Redis кэшем товаров и RESTfull серсивом оплаты

Проект создан в рамках обучения на курсе **Мидл Java-разработчик** от **practicum.yandex.ru**.

## Задание
На основе существующего проекта «Витрина интернет-магазина» разработать RESTful-сервис платежей с помощью OpenAPI и использовать Redis в качестве кеша товаров.

https://github.com/ugaforever/my-market-app-reactive/tree/module_two_sprint_six_branch

## Стек
- Java 21, Maven
- Spring Boot, WebFlux (Reactor: Mono/Flux), Thymeleaf
- Spring Data R2DBC, H2
- Spring Data Redis Reactive
- JUnit 5, WebTestClient, WebFluxTest, SpringBootTest
- OpenAPI 3.0
- Docker Compose

##  Сборка и запуск в среде разработки

```bash
# В корневую директорию проекта
cd ./my-market-app-reactive

# Запуск redis
docker run -d --name redis-server -it --rm -p 6379:6379 redis:7.4.2-bookworm

# Запуск payment
./mvnw spring-boot:run -pl payment

# Запуск backend
./mvnw spring-boot:run -pl backend

# Cтраница сайта
http://localhost:9090/
http://localhost:9090/items
# Cтраница Swagger-UI
http://localhost:9091/swagger-ui/index.html
```

##  Сборка и запуск в контейнере Docker

```bash
# В корневую директорию проекта
cd ./my-market-app-reactive

# Сборка всего мультипроекта
./mvnw clean package

# Запуск контейнеров: redis, payment, backend  
docker compose up -d

# Cтраница сайта
http://localhost:9090/
http://localhost:9090/items
# Cтраница Swagger-UI
http://localhost:9091/swagger-ui/index.html
```



