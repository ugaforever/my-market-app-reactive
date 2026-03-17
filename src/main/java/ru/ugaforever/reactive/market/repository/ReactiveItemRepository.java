package ru.ugaforever.reactive.market.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.ugaforever.reactive.market.model.Item;

@Repository
public interface ReactiveItemRepository extends ReactiveCrudRepository<Item, Long> {

    Flux<Item> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String title, String description, Pageable pageable);

    Mono<Long> countByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String title, String description);
}

