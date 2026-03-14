package ru.ugaforever.reactive.market.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import ru.ugaforever.reactive.market.model.Order;

/**
 * R2DBC не поддерживает JOIN FETCH,
 * поэтому 2 отдельных репозитория OrderRepository, OrderItemRepository
 *
 * в JpaRepository были @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items ORDER BY o.id DESC")
 */

@Repository
public interface ReactiveOrderRepository extends ReactiveCrudRepository<Order, Long> {

}


