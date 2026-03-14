package ru.ugaforever.reactive.market.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import ru.ugaforever.market.config.DatabaseInitializer;
import ru.ugaforever.market.dto.ItemDTO;
import ru.ugaforever.market.entity.Item;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

@SpringBootTest
@Import(DatabaseInitializer.class)
class CartServiceITest {

    @Autowired
    private CartService cartService;

    @Autowired
    private ReactiveItemService itemService;

    @Test
    void testContextLoads() {
        assertThat(cartService).isNotNull();
        assertThat(itemService).isNotNull();
    }

    @Test
    void testGetCartItems_ShouldReturnListItemDTO() {
        // Arrange
        Long id1 = 1L;
        cartService.addToCart(id1);
        Long id2 = 2L;
        cartService.addToCart(id2);
        cartService.addToCart(id2);

        // Act
        List<ItemDTO> result = cartService.getCartItems();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result)
                .isNotNull()
                .hasSize(2)
                .extracting(ItemDTO::getId, ItemDTO::getCount)
                .containsExactlyInAnyOrder(
                        tuple(1L, 1),
                        tuple(2L, 2)
                );
    }

    @Test
    void TestClearCart() {
        // Arrange
        Long id1 = 1L;
        cartService.addToCart(id1);
        Long id2 = 2L;
        cartService.addToCart(id2);
        cartService.addToCart(id2);

        // Act
        cartService.clearCart();

        // Assert
        List<ItemDTO> result = cartService.getCartItems();
        assertThat(result)
                .isNotNull()
                .hasSize(0);
    }

    @Test
    void testRemoveAllItems() {
        // Arrange
        Long id1 = 1L;
        cartService.addToCart(id1);
        Long id2 = 2L;
        cartService.addToCart(id2);
        cartService.addToCart(id2);

        // Act
        cartService.removeAllItems(id1);

        // Assert
        List<ItemDTO> result = cartService.getCartItems();
        assertThat(result)
                .extracting(ItemDTO::getId)
                .doesNotContain(1L);
    }

    @Test
    void testGetTotalPrice() {
        // Arrange
        Item item1 = itemService.findById(1L);
        Item item2 = itemService.findById(2L);

        cartService.addToCart(item1.getId());
        cartService.addToCart(item1.getId());
        cartService.addToCart(item2.getId());
        cartService.addToCart(item2.getId());

        // Act
        double sum = cartService.getTotalPrice();

        // Assert
        assertThat(sum).isEqualTo(1998.0);
    }

    @Test
    void testGetCountInCart() {
        // Arrange
        Long id = 1L;

        cartService.addToCart(id);
        cartService.addToCart(id);
        cartService.addToCart(id);

        // Act
        int result = cartService.getCountInCart(id);

        // Assert
        assertThat(result).isEqualTo(3);
    }
}