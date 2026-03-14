package ru.ugaforever.market.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ugaforever.market.dto.ItemDTO;
import ru.ugaforever.market.entity.Item;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class) // Только Mockito, без Spring контекста вообще. На кэширование контекстов не влияет!
class ItemMapperUTest {

    @InjectMocks
    private ItemMapper itemMapper;

    @Test
    void testToDto() {
        // Arrange
        Long expectedId = 1L;
        String expectedTitle ="item title";
        String expectedDescription ="item description";
        Double expectedPrice = 100D;
        String expectedImgPath ="item img path";

        Item item = Item.builder()
                .id(expectedId)
                .title(expectedTitle)
                .description(expectedDescription)
                .price(expectedPrice)
                .imgPath(expectedImgPath)
                .build();

        ItemDTO result = itemMapper.toDto(item);

        // Assert
        System.out.println(result);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(expectedId);
        assertThat(result.getTitle()).isEqualTo(expectedTitle);
        assertThat(result.getDescription()).isEqualTo(expectedDescription);
        assertThat(result.getPrice()).isEqualTo(expectedPrice);
    }

    @Test
    void testToEntity() {
        // Arrange
        Long expectedId = 1L;
        String expectedTitle ="item title";
        String expectedDescription ="item description";
        Double expectedPrice = 100D;
        String expectedImgPath ="item img path";

        ItemDTO itemDTO = ItemDTO.builder()
                .id(expectedId)
                .title(expectedTitle)
                .description(expectedDescription)
                .price(expectedPrice)
                .imgPath(expectedImgPath)
                .build();

        Item result = itemMapper.toEntity(itemDTO);

        // Assert
        System.out.println(result);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(expectedId);
        assertThat(result.getTitle()).isEqualTo(expectedTitle);
        assertThat(result.getDescription()).isEqualTo(expectedDescription);
        assertThat(result.getPrice()).isEqualTo(expectedPrice);
        assertThat(result.getImgPath()).isEqualTo(expectedImgPath);
    }
}