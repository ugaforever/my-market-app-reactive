package ru.ugaforever.reactive.market.backend.mapper;

import org.springframework.stereotype.Component;
import ru.ugaforever.reactive.market.backend.dto.ItemDTO;
import ru.ugaforever.reactive.market.backend.model.Item;

@Component
public class ItemMapper {

    public ItemDTO toDto(Item item) {
        return ItemDTO.builder()
                .id(item.getId())
                .title(item.getTitle())
                .price(item.getPrice())
                .description(item.getDescription())
                .imgPath(item.getImgPath())
                .build();
    }

    public Item toEntity(ItemDTO dto) {
        return Item.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .price(dto.getPrice())
                .description(dto.getDescription())
                .imgPath(dto.getImgPath())
                .build();
    }
}
