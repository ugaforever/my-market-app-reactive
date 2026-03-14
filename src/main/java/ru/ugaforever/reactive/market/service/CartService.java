/*
package ru.ugaforever.market.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ugaforever.market.dto.ItemDTO;
import ru.ugaforever.market.entity.Item;
import ru.ugaforever.market.model.Cart;
import ru.ugaforever.market.repository.ItemRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

*/
/**
 * Сервис взаимодействия с моелью "Корзина".
 * Корзина хранится в памяти в рамках сессии и после заверсения сессии корзина очищается.
 *//*


@Service
@RequiredArgsConstructor
public class CartService {
    private final ItemRepository itemRepository;
    private final Cart cartMemory;

    public List<ItemDTO> getCartItems() {
        if (cartMemory.isEmpty()) {
            return Collections.emptyList();
        }

        List<Item> items = itemRepository.findAllById(cartMemory.keySet());

        return items.stream()
                .map(item -> {
                    Long itemId = item.getId();
                    int quantityInCart = cartMemory.getCount(itemId);

                    return ItemDTO.builder()
                            .id(itemId)
                            .title(item.getTitle())
                            .description(item.getDescription())
                            .price(item.getPrice())
                            .imgPath(item.getImgPath())
                            .count(quantityInCart)         // количество в корзине
                            .build();
                })
                .collect(Collectors.toList());
    }

    public void clearCart() {
        cartMemory.clear();
    }

    public void removeAllItems(Long itemId) {
        cartMemory.removeItem(itemId);
    }

    public double getTotalPrice() {
        if (cartMemory.isEmpty()) {
            return 0.0;
        }

        List<Item> items = itemRepository.findAllById(cartMemory.keySet());

        return items.stream()
                .mapToDouble(item -> {
                    double price = item.getPrice();
                    int quantity = cartMemory.getCount(item.getId());
                    return price * quantity;
                })
                .sum();
    }

    public Integer getCountInCart(Long id) {
        return cartMemory.getCountInCart(id);
    }

    public void addToCart(Long id) {
        cartMemory.plusItem(id);
    }

    public void removeFromCart(Long id) {
        cartMemory.minusItem(id);
    }
}
*/
