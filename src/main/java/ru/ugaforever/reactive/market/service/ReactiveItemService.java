package ru.ugaforever.reactive.market.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import ru.ugaforever.reactive.market.dto.ItemDTO;
import ru.ugaforever.reactive.market.model.Item;
import ru.ugaforever.reactive.market.exception.EntityNotFoundException;
import ru.ugaforever.reactive.market.mapper.ItemMapper;
import ru.ugaforever.reactive.market.repository.ReactiveItemRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReactiveItemService {

    private final ReactiveItemRepository itemRepository;
    private final ItemMapper itemMapper;


    public Mono<Page<ItemDTO>> findItemsWithFilters(String search, Pageable pageable) {
        if (StringUtils.hasText(search)) {
            return findItemsBySearch(search, pageable);
        } else {
            return findAllItems(pageable);
        }
    }

    private Mono<Page<ItemDTO>> findItemsBySearch(String search, Pageable pageable) {
        String searchTerm = search.toLowerCase();

        // Параллельно получаем данные и общее количество
        return Mono.zip(
                itemRepository.countByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchTerm, searchTerm),
                itemRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchTerm, searchTerm, pageable)
                        .map(itemMapper::toDto)
                        .collectList()
        ).map(tuple -> new PageImpl<>(tuple.getT2(), pageable, tuple.getT1()));
    }

    private Mono<Page<ItemDTO>> findAllItems(Pageable pageable) {
        return Mono.zip(
                itemRepository.count(),
                itemRepository.findAll()  // Изменено здесь!
                        .skip(pageable.getOffset())  // Ручная пагинация
                        .take(pageable.getPageSize())
                        .map(itemMapper::toDto)
                        .collectList()
        ).map(tuple -> new PageImpl<>(tuple.getT2(), pageable, tuple.getT1()));
    }

    public Mono<Item> findById(Long id) {
        return itemRepository.findById(id)
                .switchIfEmpty(Mono.defer(() -> {
                    return Mono.error(new EntityNotFoundException("Товар с ID " + id + " не найден"));
                }));
    }

    public Mono<Item> save(Item item) {
        return itemRepository.save(item);
    }
}

