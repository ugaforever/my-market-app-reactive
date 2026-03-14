/*
package ru.ugaforever.market.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ugaforever.market.dto.ItemDTO;
import ru.ugaforever.market.entity.Item;
import ru.ugaforever.market.entity.Order;
import ru.ugaforever.market.entity.OrderItem;
import ru.ugaforever.market.model.Cart;
import ru.ugaforever.market.repository.ItemRepository;
import ru.ugaforever.market.repository.OrderRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final Cart cartMemory;

    public Order findById(Long id) {
        return orderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new EntityNotFoundException("Заказ с ID " + id + " не найден"));
    }

    public List<Order> findAllWithItems() {
        return orderRepository.findAllWithItems();
    }

    public Long createOrder(List<ItemDTO> items) {
        Order order = new Order();

        long totalSum = 0L;

        for (ItemDTO itemDto : items) {

            // из БД
            Item item = itemRepository.findById(itemDto.getId())
                    .orElseThrow(() -> new RuntimeException("Товар не найден: " + itemDto.getId()));

            int countInCart = cartMemory.getCount(item.getId());

            OrderItem orderItem = OrderItem.builder()
                    .item(item)                    // ссылка на товар
                    .price(item.getPrice())        // цена на момент заказа
                    .count(countInCart)            // количество товара в корзине
                    .build();

            order.addItem(orderItem);

            totalSum += item.getPrice() * countInCart;
        }

        order.setTotalSum(totalSum);

        Order savedOrder = orderRepository.save(order);

        return savedOrder.getId();
    }
}
*/
