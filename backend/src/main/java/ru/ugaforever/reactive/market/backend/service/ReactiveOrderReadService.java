package ru.ugaforever.reactive.market.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.ugaforever.reactive.market.backend.model.Order;
import ru.ugaforever.reactive.market.backend.repository.ReactiveOrderRepository;

@Service
@RequiredArgsConstructor
public class ReactiveOrderReadService {

    private final ReactiveOrderRepository orderRepository;

    public Mono<Order> findById(Long id) {
        return orderRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Заказ с ID " + id + " не найден")
                ));
    }

    public Flux<Order> findAll() {
        return orderRepository.findAll();
    }




}

