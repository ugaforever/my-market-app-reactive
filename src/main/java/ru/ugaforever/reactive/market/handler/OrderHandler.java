package ru.ugaforever.reactive.market.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.ugaforever.reactive.market.service.ReactiveCartService;
import ru.ugaforever.reactive.market.service.ReactiveOrderService;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OrderHandler {

    private final ReactiveOrderService orderService;
    private final ReactiveCartService cartService;

    public Mono<ServerResponse> getOrders(ServerRequest request) {
        return orderService.findAll()
                .collectList()
                .flatMap(orders -> ServerResponse.ok()
                        .render("orders", Map.of("orders", orders)))
                .switchIfEmpty(ServerResponse.ok()
                        .render("orders", Map.of("orders", Collections.emptyList())));
    }

    public Mono<ServerResponse> getOrderById(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        Boolean newOrder = Boolean.parseBoolean(request.queryParam("newOrder").orElse("false"));

        return orderService.findById(id)
                .flatMap(order ->
                        ServerResponse.ok()
                                .render("order", Map.of(
                                        "order", order,
                                        "newOrder", newOrder
                                ))
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> createOrder(ServerRequest request) {
        return request.session()
                .flatMap(session ->
                        cartService.getCartItems(session)
                                .collectList()
                                .flatMap(items -> {
                                    if (items.isEmpty()) {
                                        return Mono.error(new IllegalStateException("Корзина пуста"));
                                    }

                                    return orderService.createOrder(session, items)
                                            .flatMap(orderId ->
                                                    cartService.clearCart(session)
                                                            .thenReturn(orderId)
                                            );
                                })
                )
                .flatMap(orderId ->
                        ServerResponse.seeOther(URI.create("/orders/" + orderId + "?newOrder=true"))
                                .build()
                );
    }
}
