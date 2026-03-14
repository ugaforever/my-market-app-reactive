package ru.ugaforever.market.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.ugaforever.market.config.DatabaseInitializer;
import ru.ugaforever.market.dto.ItemDTO;
import ru.ugaforever.market.entity.Item;
import ru.ugaforever.market.util.ItemUtils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(DatabaseInitializer.class)
class ItemServiceITest {

    @Autowired
    private ItemService itemService;

    @Test
    void testFindItemsWithFilters() {
        // Arrange
        String title = "specific_title";
        String description = "specific_description";
        Item item = Item.builder().title(title).description(description).price(1.0).build();
        itemService.save(item);

        Sort springTitle = ItemUtils.convertToSpringSort("NO");
        Pageable sortedPageable = PageRequest.of(0, 5, springTitle);

        // Act1
        Page<ItemDTO> result1 = itemService.findItemsWithFilters(title, sortedPageable);

        // Assert1
        assertAll("Проверка Page<ItemDTO>",
                // 1. Проверка самого Page объекта
                () -> assertThat(result1).isNotNull(),

                // 2. Проверка содержимого страницы
                () -> assertThat(result1.getContent())
                        .isNotNull()
                        .hasSize(1)
                        .extracting(ItemDTO::getTitle, ItemDTO::getDescription)
                        .containsExactly(
                                tuple(title, description)
                        ),

                // 3. Проверка пагинации и навигации
                () -> assertThat(result1.getNumber()).isEqualTo(0),
                () -> assertThat(result1.getSize()).isEqualTo(5),
                () -> assertThat(result1.getTotalElements()).isEqualTo(1),
                () -> assertThat(result1.getTotalPages()).isEqualTo(1),
                () -> assertThat(result1.isFirst()).isTrue(),
                () -> assertThat(result1.isLast()).isTrue(),
                () -> assertThat(result1.hasPrevious()).isFalse(),
                () -> assertThat(result1.hasNext()).isFalse()
        );

        // Act2
        Page<ItemDTO> result2 = itemService.findItemsWithFilters(description, sortedPageable);

        // Assert1
        assertAll("Проверка Page<ItemDTO>",
                () -> assertThat(result2).isNotNull(),
                () -> assertThat(result2.getContent())
                        .isNotNull()
                        .hasSize(1)
                        .extracting(ItemDTO::getTitle, ItemDTO::getDescription)
                        .containsExactly(
                                tuple(title, description)
                        ),
                () -> assertThat(result2.getNumber()).isEqualTo(0),
                () -> assertThat(result2.getSize()).isEqualTo(5),
                () -> assertThat(result2.getTotalElements()).isEqualTo(1),
                () -> assertThat(result2.getTotalPages()).isEqualTo(1),
                () -> assertThat(result2.isFirst()).isTrue(),
                () -> assertThat(result2.isLast()).isTrue(),
                () -> assertThat(result2.hasPrevious()).isFalse(),
                () -> assertThat(result2.hasNext()).isFalse()
        );
    }

    @ParameterizedTest
    @ValueSource(longs = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20})
    void testFindById_ShouldReturnItem(long id) {

        // Act
        Item result = itemService.findById(id);

        // Assert
        assertAll("Проверка найденного товара",
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getId()).isEqualTo(id),
                () -> assertThat(result.getTitle()).isNotNull(),
                () -> assertThat(result.getDescription()).isNotNull(),
                () -> assertThat(result.getPrice()).isNotNull()
        );
    }

    @Test
    void testFindById_ShouldThrowException() {
        // Arrange
        Long nonId = 1000L;

        // Act & Assert
        assertThatThrownBy(() -> itemService.findById(nonId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Товар с ID " + nonId + " не найден");
    }
}