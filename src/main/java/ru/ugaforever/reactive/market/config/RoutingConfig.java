package ru.ugaforever.reactive.market.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import ru.ugaforever.reactive.market.handler.OrderHandler;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RoutingConfig {
    @Bean
    public RouterFunction<ServerResponse> orderRoutes(OrderHandler handler) {
        return route()
                .GET("/orders/{id}", handler::getOrderById)
                .GET("/orders", handler::getOrders)
                .POST("/buy", handler::createOrder)
                .build();
    }
}
