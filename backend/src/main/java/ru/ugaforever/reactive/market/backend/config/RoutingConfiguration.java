package ru.ugaforever.reactive.market.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import ru.ugaforever.reactive.market.backend.filter.AuthenticationFilter;
import ru.ugaforever.reactive.market.backend.handler.OrderHandler;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RoutingConfiguration {

    private final AuthenticationFilter authenticationFilter;

    @Bean
    public RouterFunction<ServerResponse> orderRoutes(OrderHandler handler) {
        return route()
                .GET("/orders/{id}", handler::getOrderById)
                .GET("/orders", handler::getOrders)
                .POST("/buy", handler::createOrder)
                .filter(authenticationFilter)
                .build();
    }
}
