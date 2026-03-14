/*
package ru.ugaforever.market.model;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

*/
/**
 * Корзина хранится в памяти в рамках сессии.
 * После заверсения сессии корзина очищается.
 *//*


@Component
@SessionScope
@RequiredArgsConstructor
public class Cart {
    private final Map<Long, Integer> cartItems = new ConcurrentHashMap<>();

    public void plusItem(Long itemId) {
        cartItems.merge(itemId, 1, Integer::sum);
    }

    public void minusItem(Long itemId) {
        cartItems.computeIfPresent(itemId, (id, count) -> {
            if (count <= 1) {
                return null;  // Удаляем товар из корзины
            }
            return count - 1;
        });
    }

    public int getCountInCart(Long itemId) {
        return cartItems.getOrDefault(itemId, 0);
    }

    public boolean isEmpty() {
        return cartItems.isEmpty();
    }

    public Iterable<Long> keySet() {
        return cartItems.keySet();
    }

    public int getCount(Long itemId) {
        return cartItems.getOrDefault(itemId, 0);
    }

    public void clear() {
        cartItems.clear();
    }

    public void removeItem(Long itemId) {
        cartItems.remove(itemId);
    }
}
*/
