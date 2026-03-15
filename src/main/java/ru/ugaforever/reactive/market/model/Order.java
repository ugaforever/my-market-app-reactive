package ru.ugaforever.reactive.market.model;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.ArrayList;
import java.util.List;


/**
 * Заказ
 */

@Table(name = "orders")
@Data
@ToString(exclude = "items")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    private Long id;

    @Column("total_sum")
    long totalSum;

    @Transient  // R2DBC не поддерживает @OneToMany, помечаем как временное поле
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    private OrderMetadata metadata;

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }
}

