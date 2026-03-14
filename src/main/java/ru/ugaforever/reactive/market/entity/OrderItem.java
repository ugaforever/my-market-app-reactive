package ru.ugaforever.reactive.market.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "order_items")
@Data
@ToString(exclude = "order")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    private Long id;

    @Column("order_id")
    private Long orderId;

    @Column("item_id")
    private Long itemId;

    @Transient
    private Order order;  // Не сохраняется в БД, заполняется отдельным запросом

    @Transient
    private Item item;    // Не сохраняется в БД, заполняется отдельным запросом

    @Column("price")
    private Double price;  // Цена данного товара на момент заказа

    @Column("count")
    private Integer count; // Количество данного товара в заказе

    public void setOrder(Order order) {
        this.order = order;
        this.orderId = order != null ? order.getId() : null;
    }

/*    public void setItem(Item item) {
        this.item = item;
        this.itemId = item != null ? item.getId() : null;
        if (item != null && this.title == null) {
            this.title = item.getTitle(); // Копируем название на момент создания
        }
    }*/

    public Double getTotalPrice() {
        return price * count;
    }
}

