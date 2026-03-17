package ru.ugaforever.reactive.market.callback;

import org.reactivestreams.Publisher;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.data.r2dbc.mapping.event.BeforeSaveCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import reactor.core.publisher.Mono;
import ru.ugaforever.reactive.market.model.Order;

import java.util.Objects;

public class ValidateOrderIdBeforeSaveCallback implements BeforeSaveCallback<Order> {
    @Override
    public Publisher<Order> onBeforeSave(Order entity, OutboundRow row, SqlIdentifier table) {

        if (entity.getTotalSum() < 0) {
            return Mono.error(new IllegalArgumentException("Сумма заказа не может быть отрицательной"));
        }

        if (entity.getItems() == null || entity.getItems().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Заказ должен содержать хотя бы один товар"));
        }

        return Mono.just(entity);
    }
}
