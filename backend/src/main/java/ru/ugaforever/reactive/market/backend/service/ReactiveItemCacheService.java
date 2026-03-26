package ru.ugaforever.reactive.market.backend.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import ru.ugaforever.reactive.market.backend.dto.ItemDTO;
import ru.ugaforever.reactive.market.backend.model.Item;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReactiveItemCacheService {

    private final ReactiveItemService itemCacheService;

    @Cacheable(value = "items", key = "'filter'")
    public Mono<Page<ItemDTO>> findItemsWithFilters(String search, Pageable pageable) {
        if (StringUtils.hasText(search)) {
            return itemCacheService.findItemsBySearch(search, pageable);
        } else {
            return itemCacheService.findAllItems(pageable);
        }
    }

    @Cacheable(value = "item", key = "#id")
    public Mono<ItemDTO> findById(Long id) {

        //если нет в кэше, то пойдет в БД!!
        return itemCacheService.findById(id);
    }
}

