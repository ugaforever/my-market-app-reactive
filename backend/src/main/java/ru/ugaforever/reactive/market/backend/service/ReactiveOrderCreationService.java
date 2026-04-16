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
import ru.ugaforever.reactive.market.payment.client.model.PaymentResponse;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReactiveOrderCreationService {

    private final ReactiveOrderRepository orderRepository;
    private final ReactiveItemRepository itemRepository;
    private final ReactiveCartService cartService;
    private final ReactivePaymentService paymentService;
    private final ReactiveBalanceValidator balanceValidator;
    private final ReactivePaymentValidator paymentValidator;
    private final ReactiveBalanceService balanceService;
    private final ReactiveAccountIdService accountIdService;

    public Mono<Long> create(WebSession session, List<ItemDTO> items) {
        if (items == null || items.isEmpty()) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Список товаров пуст"));
        }

        return accountIdService.getCurrentUserId()
                .flatMap(accountId ->
                        getOrderItems(session, items)
                                .flatMap(orderItems -> {
                                    long totalSum = calculateTotalSum(orderItems);

                                    return processPayment(accountId, totalSum)
                                            .flatMap(paymentResponse -> saveOrder(accountId, totalSum, orderItems));
                                })
                );
    }

    private Mono<List<OrderItem>> getOrderItems(WebSession session, List<ItemDTO> items) {
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
                .collectList();
    }

    private long calculateTotalSum(List<OrderItem> orderItems) {
        return orderItems.stream()
                .mapToLong(oi -> (long) (oi.getPrice() * oi.getQuantity()))
                .sum();
    }

    private PaymentRequest createPaymentRequest(String accountId, long totalSum) {
        return PaymentRequest.builder()
                .accountId(accountId)
                .amount(BigDecimal.valueOf(totalSum))
                .build();
    }

    private Mono<PaymentResponse> processPayment(String accountId, long totalSum) {
        PaymentRequest paymentRequest = createPaymentRequest(accountId, totalSum);

        return balanceService.getBalance(accountId)
                .flatMap(balance -> balanceValidator.validate(balance, totalSum))
                .flatMap(balance -> paymentService.processPayment(paymentRequest))
                .flatMap(paymentValidator::validate);
    }

    private Mono<Long> saveOrder(String accountId, long totalSum, List<OrderItem> orderItems) {

        Order order = new Order();

        //order.setAccountId

        order.setTotalSum(totalSum);
        orderItems.forEach(order::addItem);

        return orderRepository.save(order)
                .map(Order::getId);
    }
}
