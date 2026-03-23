package ru.ugaforever.reactive.market.backend.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import ru.ugaforever.reactive.market.backend.model.OrderItem;

@Repository
public interface ReactiveOrderItemRepository extends ReactiveCrudRepository<OrderItem, Long> {

}
