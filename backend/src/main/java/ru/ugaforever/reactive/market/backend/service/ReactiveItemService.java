package ru.ugaforever.reactive.market.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.ugaforever.reactive.market.backend.dto.ItemDTO;
import ru.ugaforever.reactive.market.backend.exception.EntityNotFoundException;
import ru.ugaforever.reactive.market.backend.mapper.ItemMapper;
import ru.ugaforever.reactive.market.backend.model.Item;
import ru.ugaforever.reactive.market.backend.repository.ReactiveItemRepository;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReactiveItemService {

    private final ReactiveItemRepository itemRepository;
    private final ItemMapper itemMapper;

    public Mono<Page<ItemDTO>> findItemsBySearch(String search, Pageable pageable) {

        String searchTerm = search.toLowerCase();

        // Параллельно получаем данные и общее количество
        return Mono.zip(
                itemRepository.countByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchTerm, searchTerm),
                itemRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchTerm, searchTerm, pageable)
                        .map(itemMapper::toDto)
                        .collectList()
        ).map(tuple -> new PageImpl<>(tuple.getT2(), pageable, tuple.getT1()));
    }

    public Mono<Page<ItemDTO>> findAllItems(Pageable pageable) {

        return Mono.zip(
                itemRepository.count(),
                itemRepository.findAll()  // Изменено здесь!
                        .skip(pageable.getOffset())  // Ручная пагинация
                        .take(pageable.getPageSize())
                        .map(itemMapper::toDto)
                        .collectList()
        ).map(tuple -> new PageImpl<>(tuple.getT2(), pageable, tuple.getT1()));
    }

    public Mono<ItemDTO> findById(Long id) {
        return itemRepository.findById(id)
                .filter(Objects::nonNull)
                .map(itemMapper::toDto)
                .switchIfEmpty(Mono.defer(() -> {
                    return Mono.error(new EntityNotFoundException("Товар с ID " + id + " не найден"));
                }));

    }
}
