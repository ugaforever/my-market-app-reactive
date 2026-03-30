package ru.ugaforever.reactive.market.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.ugaforever.reactive.market.backend.dto.ItemDTO;
import ru.ugaforever.reactive.market.backend.exception.OrderCreationException;
import ru.ugaforever.reactive.market.backend.model.Order;
import ru.ugaforever.reactive.market.backend.model.OrderItem;
import ru.ugaforever.reactive.market.backend.repository.ReactiveItemRepository;
import ru.ugaforever.reactive.market.backend.repository.ReactiveOrderRepository;
import ru.ugaforever.reactive.market.backend.service.validator.ReactiveBalanceValidator;
import ru.ugaforever.reactive.market.backend.service.validator.ReactivePaymentValidator;
import ru.ugaforever.reactive.market.payment.client.model.PaymentRequest;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReactiveOrderCreationService {

    private final ReactiveOrderRepository orderRepository;
    private final ReactiveItemRepository itemRepository;
    private final ReactiveCartService cartService;
    private final ReactivePaymentService paymentService;
    private final AccountIdGenerationService accountIdGenerator;
    private final ReactiveBalanceValidator balanceValidator;
    private final ReactivePaymentValidator paymentValidator;
    private final ReactiveBalanceService balanceService;

    public Mono<Long> create(WebSession session, List<ItemDTO> items) {
        if (items == null || items.isEmpty()) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Список товаров пуст"));
        }

        // имитация accountId
        final String finalAccountId = accountIdGenerator.generate(session);

        return Flux.fromIterable(items)
                .flatMap(itemDto ->
                        itemRepository.findById(itemDto.getId())
                                .switchIfEmpty(Mono.error(new OrderCreationException("Товар не найден: " + itemDto.getId())))
                                .flatMap(item ->
                                        cartService.getCount(session, item.getId())
                                                .map(count -> OrderItem.builder()
                                                        .item(item)
                                                        .price(item.getPrice())
                                                        .quantity(count)
                                                        .build())
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

                    return balanceService.getBalance(finalAccountId)
                            .flatMap(balance -> balanceValidator.validate(balance, totalSum))
                            .flatMap(balance -> paymentService.processPayment(paymentRequest))
                            .flatMap(responce -> paymentValidator.validate(responce))
                            .flatMap(responce -> saveOrder(totalSum, orderItems));
                });
    }

    private Mono<Long> saveOrder(long totalSum, List<OrderItem> orderItems){
        Order order = new Order();
        order.setTotalSum(totalSum);
        orderItems.forEach(order::addItem);

        return orderRepository.save(order)
                .map(Order::getId);
    }
}
