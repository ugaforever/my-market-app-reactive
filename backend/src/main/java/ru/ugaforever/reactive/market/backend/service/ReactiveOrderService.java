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
import ru.ugaforever.reactive.market.payment.client.model.PaymentRequest;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReactiveOrderService {

    private final ReactiveOrderRepository orderRepository;
    private final ReactiveItemRepository itemRepository;
    private final ReactiveCartService cartService;
    private final ReactivePaymentService paymentService;

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

        // имитация accountId
        Long accountId = session.getAttribute("accountId");
        if (accountId == null) {
            accountId = generateAccountId();
            session.getAttributes().put("accountId", accountId);
        }
        final String finalAccountId = accountId.toString();

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
                    long totalSum = orderItems.stream()
                            .mapToLong(oi -> (long) (oi.getPrice() * oi.getQuantity()))
                            .sum();

                    PaymentRequest paymentRequest = PaymentRequest.builder()
                            .accountId(finalAccountId)
                            .amount(BigDecimal.valueOf(totalSum))
                            .build();

                    return paymentService.getBalance(finalAccountId)
                            .flatMap(balance -> {
                                if (balance.getBalance().longValue() < totalSum) {
                                    return Mono.error(new ResponseStatusException(
                                            HttpStatus.PAYMENT_REQUIRED,
                                            "Недостаточно средств. Доступно: " + balance.getBalance()
                                    ));
                                }
                                return paymentService.processPayment(paymentRequest);
                            })
                            .flatMap(paymentResponse -> {
                                if (!"SUCCESS".equals(paymentResponse.getStatus())) {
                                    return Mono.error(new ResponseStatusException(
                                            HttpStatus.PAYMENT_REQUIRED,
                                            "Оплата не прошла: " + paymentResponse.getStatus()
                                    ));
                                }

                                Order order = new Order();
                                order.setTotalSum(totalSum);
                                orderItems.forEach(order::addItem);

                                return orderRepository.save(order)
                                        .map(Order::getId);
                            });
                });
    }

    private Long generateAccountId() {
        return System.currentTimeMillis() + (long) (Math.random() * 10000);
    }
}

