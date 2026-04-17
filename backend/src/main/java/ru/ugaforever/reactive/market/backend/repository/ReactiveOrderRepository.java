package ru.ugaforever.reactive.market.backend.repository;

import org.reactivestreams.Publisher;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import ru.ugaforever.reactive.market.backend.model.Order;

/**
 * R2DBC не поддерживает JOIN FETCH,
 * поэтому 2 отдельных репозитория OrderRepository, OrderItemRepository
 *
 * в JpaRepository были @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items ORDER BY o.id DESC")
 */

@Repository
public interface ReactiveOrderRepository extends ReactiveCrudRepository<Order, Long> {

    Publisher<? extends Order> findByAccountId(String accountId);
}


