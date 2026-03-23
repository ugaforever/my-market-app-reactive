package ru.ugaforever.reactive.market.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.ugaforever.reactive.market.backend.dto.ItemDTO;
import ru.ugaforever.reactive.market.backend.model.Order;
import ru.ugaforever.reactive.market.backend.model.OrderItem;
import ru.ugaforever.reactive.market.backend.repository.ReactiveItemRepository;
import ru.ugaforever.reactive.market.backend.repository.ReactiveOrderRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReactiveOrderService {

    private final ReactiveOrderRepository orderRepository;
    private final ReactiveItemRepository itemRepository;
    private final ReactiveCartService cartService;

    public Mono<Order> findById(Long id) {
        return orderRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Заказ с ID " + id + " не найден")
                ));
    }

    public Flux<Order> findAll() {
        return orderRepository.findAll();
    }

    public Mono<Long> createOrder(WebSession session, List<ItemDTO> items) {
        if (items == null || items.isEmpty()) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Список товаров пуст"));
        }

        return Flux.fromIterable(items)
                .flatMap(itemDto ->
                        itemRepository.findById(itemDto.getId())
                                .switchIfEmpty(Mono.error(new RuntimeException("Товар не найден: " + itemDto.getId())))
                                .flatMap(item ->
                                        cartService.getCount(session, item.getId())
                                                .map(count -> {
                                                    OrderItem orderItem = OrderItem.builder()
                                                            .item(item)
                                                            .price(item.getPrice())
                                                            .quantity(count)
                                                            .build();
                                                    return orderItem;
                                                })
                                )
                )
                .collectList()
                .flatMap(orderItems -> {
                    Order order = new Order();

                    long totalSum = orderItems.stream()
                            .mapToLong(oi -> (long) (oi.getPrice() * oi.getQuantity()))
                            .sum();

                    order.setTotalSum(totalSum);
                    orderItems.forEach(order::addItem);

                    return orderRepository.save(order)
                            .map(Order::getId);
                });
    }
}

