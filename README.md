# Веб-приложение «Витрина интернет-магазина» с использованием Spring Boot в реактивном стиле c Redis кэшем товаров и RESTfull серсивом оплаты и аутентификацией и авторизацией 

Проект создан в рамках обучения на курсе **Мидл Java-разработчик** от **practicum.yandex.ru**.

## Задание
В существующий проект «Витрина интернет-магазина» с RESTful-сервисом платежей, OpenAPI, Redis в качестве кеша товаров внедрить аутентификацию и авторизацию.

Отчет о проделанной работе в Pull request.

https://github.com/ugaforever/my-market-app-reactive/tree/module_two_sprint_seven_branch

## Стек
- Java 21, Maven
- Spring Boot
- Spring WebFlux (Reactor: Mono/Flux), Thymeleaf
- Spring Data R2DBC, H2
- Spring Data Redis Reactive
- Spring Security
- JUnit 5, WebTestClient, WebFluxTest, SpringBootTest
- OpenAPI 3.0
- Docker Compose

##  Сборка и запуск в среде разработки

```bash
# В корневую директорию проекта
cd ./my-market-app-reactive

# Запуск контейнеров redis и keycloak
docker compose up -d

# Запуск payment
./mvnw spring-boot:run -pl payment

# Запуск backend
./mvnw spring-boot:run -pl backend

# Cтраница сайта
http://localhost:9090/
http://localhost:9090/items
# Cтраница Swagger-UI
http://localhost:9091/swagger-ui/index.html
# Cтраница конфигурации Keycloak
http://localhost:9092/realms/my-market-app/.well-known/openid-configuration
```

##  Сборка и запуск в контейнере Docker

```bash
# В корневую директорию проекта
cd ./my-market-app-reactive

# Сборка всего мультипроекта
./mvnw clean package

# Запуск контейнеров: redis, payment, backend  
docker compose up -d
```



