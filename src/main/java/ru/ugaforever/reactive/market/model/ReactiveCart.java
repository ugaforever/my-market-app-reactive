package ru.ugaforever.reactive.market.model;

import lombok.Builder;
import lombok.Value;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Корзина хранится в памяти в рамках сессии WebSession.
 * После заверсения сессии корзина очищается.
 * Класс корзины ИМмутабельный
 */

@Value
@Builder(toBuilder = true)
public class ReactiveCart {
    Map<Long, Integer> items;

    public static ReactiveCart empty() {
        return new ReactiveCart(Collections.emptyMap());
    }

    public ReactiveCart plusItem(Long itemId) {
        Map<Long, Integer> newItems = new HashMap<>(items);
        newItems.merge(itemId, 1, Integer::sum);
        return new ReactiveCart(Collections.unmodifiableMap(newItems));
    }

    public ReactiveCart minusItem(Long itemId) {
        if (!items.containsKey(itemId)) {
            return this;
        }

        Map<Long, Integer> newItems = new HashMap<>(items);
        Integer currentCount = newItems.get(itemId);

        if (currentCount <= 1) {
            newItems.remove(itemId);
        } else {
            newItems.put(itemId, currentCount - 1);
        }

        return new ReactiveCart(Collections.unmodifiableMap(newItems));
    }

    public ReactiveCart removeItem(Long itemId) {
        if (!items.containsKey(itemId)) {
            return this;
        }

        Map<Long, Integer> newItems = new HashMap<>(items);
        newItems.remove(itemId);
        return new ReactiveCart(Collections.unmodifiableMap(newItems));
    }

    public ReactiveCart clear() {
        return empty();
    }

    public int getCount(Long itemId) {
        return items.getOrDefault(itemId, 0);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public int getTotalItems() {
        return items.values().stream().mapToInt(Integer::intValue).sum();
    }
}
