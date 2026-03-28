package ru.ugaforever.reactive.market.backend.util;

import org.springframework.data.domain.Sort;
import ru.ugaforever.reactive.market.backend.dto.ItemDTO;

import java.util.ArrayList;
import java.util.List;

public final class ItemUtils {

    private ItemUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static List<List<ItemDTO>> groupItemsForDisplay(List<ItemDTO> items, int itemsPerRow){
        List<List<ItemDTO>> rows = new ArrayList<>();
        for (int i = 0; i < items.size(); i += itemsPerRow) {
            int end = Math.min(i + itemsPerRow, items.size());
            List<ItemDTO> row = new ArrayList<>(items.subList(i, end));

            while (row.size() < itemsPerRow) {
                row.add(ItemDTO.builder()
                        .id(-1L)
                        .title("")
                        .description("")
                        .price(0D)
                        .imgPath("")
                        .count(0)
                        .build());
            }

            rows.add(row);
        }

        return rows;
    }

    public static Sort convertToSpringSort(String sortParam) {
        if (sortParam == null) {
            return Sort.unsorted();
        }

        //todo Magic string!!
        return switch (sortParam.toUpperCase()) {
            case "ALPHA" -> Sort.by(Sort.Direction.ASC, "title");
            case "PRICE" -> Sort.by(Sort.Direction.ASC, "price");
            default -> Sort.unsorted();
        };
    }

}

