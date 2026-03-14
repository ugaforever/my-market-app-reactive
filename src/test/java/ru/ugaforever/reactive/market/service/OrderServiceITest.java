package ru.ugaforever.reactive.market.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import ru.ugaforever.market.config.DatabaseInitializer;
import ru.ugaforever.market.dto.ItemDTO;
import ru.ugaforever.market.entity.Order;
import ru.ugaforever.market.mapper.ItemMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(DatabaseInitializer.class)
@Transactional  // отказ перед каждым тестом для обнудения заказов
class OrderServiceITest {

    @Autowired
    private ReactiveOrderService orderService;
    @Autowired
    private ReactiveItemService itemService;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private CartService cartService;

    @Test
    void testFindById_ShouldReturnOrder() {
        // Arrange
        cartService.addToCart(1L);
        cartService.addToCart(1L);
        cartService.addToCart(2L);
        cartService.addToCart(2L);

        List<ItemDTO> items = List.of(
                itemMapper.toDto(itemService.findById(1L)),
                itemMapper.toDto(itemService.findById(2L)));

        // Act
        long newId = orderService.createOrder(items);
        Order result = orderService.findById(newId);

        // Assert
        assertAll("Проверка заказа",
                () -> assertThat(result.getId()).isEqualTo(newId),
                () -> assertThat(result.getTotalSum()).isEqualTo(1998L),

                // Проверка всех позиций одной строкой
                () -> assertThat(result.getItems())
                        .hasSize(2)
                        .extracting(
                                orderItem -> orderItem.getItem().getId(),
                                orderItem -> orderItem.getPrice()
                        )
                        .containsExactlyInAnyOrder(
                                tuple(1L, 333.0),
                                tuple(2L, 666.0)
                        )
        );
    }

    @Test
    void testFindById_ShouldThrowException() {
        // Arrange
        Long nonId = 999L;

        // Act & Assert
        assertThatThrownBy(() -> orderService.findById(nonId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Заказ с ID " + nonId + " не найден");
    }

    @Test
    void testFindAllWithItems() {
        // Arrange
        cartService.addToCart(1L);
        cartService.addToCart(1L);
        cartService.addToCart(2L);
        cartService.addToCart(2L);

        List<ItemDTO> items = List.of(
                itemMapper.toDto(itemService.findById(1L)),
                itemMapper.toDto(itemService.findById(2L)));

        long newId = orderService.createOrder(items);

        // Act
        List<Order> result = orderService.findAll();

        // Assert
        assertAll("Получение всех заказов",
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result.get(0))
                        .satisfies(order -> {
                            assertThat(order.getId()).isEqualTo(newId);
                            assertThat(order.getTotalSum()).isEqualTo(1998L);
                            assertThat(order.getItems()).hasSize(2);
                        })
        );
    }

    @Test
    void testCreateOrder() {
        // Arrange
        cartService.addToCart(1L);
        cartService.addToCart(1L);
        cartService.addToCart(2L);
        cartService.addToCart(2L);

        List<ItemDTO> items = List.of(
                itemMapper.toDto(itemService.findById(1L)),
                itemMapper.toDto(itemService.findById(2L)));

        // Act
        long newId = orderService.createOrder(items);

        // Assert
        Order result = orderService.findById(newId);
        assertAll("Получение всех заказов",
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getId()).isEqualTo(newId),
                () -> assertThat(result.getTotalSum()).isEqualTo(1998L),
                () -> assertThat(result.getItems()).hasSize(2)
        );
    }
}