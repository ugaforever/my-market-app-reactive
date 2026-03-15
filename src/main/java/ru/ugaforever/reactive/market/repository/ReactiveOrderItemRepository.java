package ru.ugaforever.reactive.market.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import ru.ugaforever.reactive.market.model.OrderItem;

import java.util.List;

@Repository
public interface ReactiveOrderItemRepository extends ReactiveCrudRepository<OrderItem, Long> {

}
