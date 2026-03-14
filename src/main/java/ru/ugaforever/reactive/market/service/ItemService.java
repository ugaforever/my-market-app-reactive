/*
package ru.ugaforever.market.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.ugaforever.market.dto.ItemDTO;
import ru.ugaforever.market.entity.Item;
import ru.ugaforever.market.mapper.ItemMapper;
import ru.ugaforever.market.repository.ItemRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    public Page<ItemDTO> findItemsWithFilters(String search, Pageable pageable) {
        Page<Item> page;

        if (StringUtils.hasText(search)) {
            page = itemRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                    search, search, pageable);
        } else {
            page = itemRepository.findAll(pageable);
        }

        return page.map(itemMapper::toDto);
    }

    public Item findById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Товар с ID " + id + " не найден"));
    }

    public Item save(Item item) {
        return itemRepository.save(item);
    }
}
*/
