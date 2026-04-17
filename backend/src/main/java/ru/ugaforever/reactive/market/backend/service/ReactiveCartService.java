package ru.ugaforever.reactive.market.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.ugaforever.reactive.market.backend.dto.ItemDTO;
import ru.ugaforever.reactive.market.backend.model.ReactiveCart;
import ru.ugaforever.reactive.market.backend.repository.ReactiveItemRepository;

@Service
@RequiredArgsConstructor
public class ReactiveCartService {

    private final ReactiveItemRepository itemRepository;
    private final ReactiveAccountIdService accountIdService;

    private static final String CART_ATTRIBUTE_PREFIX = "cart:";

    // Генерация уникального имени корзины для пользователей (cart:UUID)
    private Mono<String> getCartName() {
        return accountIdService.getCurrentUserId()
                .map(accountId -> CART_ATTRIBUTE_PREFIX + accountId);
    }

    public Mono<ReactiveCart> getCart(WebSession session) {
        return getCartName()
                .flatMap(cartName ->
                        Mono.justOrEmpty(session.<ReactiveCart>getAttribute(cartName))
                                .switchIfEmpty(
                                        Mono.just(ReactiveCart.empty())
                                                .doOnNext(cart -> session.getAttributes().put(cartName, cart))
                                ));
    }

    public Mono<ReactiveCart> plusItem(WebSession session, Long itemId) {
        return getCartName()
                .flatMap(cartName ->
                        getCart(session)
                                .map(cart -> cart.plusItem(itemId))
                                .doOnNext(newCart -> session.getAttributes().put(cartName, newCart))
                );
    }

    public Mono<ReactiveCart> minusItem(WebSession session, Long itemId) {
        return getCartName()
                .flatMap(cartName ->
                        getCart(session)
                                .map(cart -> cart.minusItem(itemId))
                                .doOnNext(newCart -> session.getAttributes().put(cartName, newCart))
                );
    }

    public Mono<ReactiveCart> removeItem(WebSession session, Long itemId) {
        return getCartName()
                .flatMap(cartName ->
                        getCart(session)
                                .map(cart -> cart.removeItem(itemId))
                                .doOnNext(newCart -> session.getAttributes().put(cartName, newCart))
                );
    }

    public Mono<Integer> getCount(WebSession session, Long itemId) {
        return getCart(session)
                .map(cart -> cart.getCount(itemId));
    }

    public Mono<ReactiveCart> clearCart(WebSession session) {
        return getCartName()
                .flatMap(cartName ->
                        getCart(session)
                                .map(ReactiveCart::clear)
                                .doOnNext(newCart -> session.getAttributes().put(cartName, newCart))
                );
    }

    public Flux<ItemDTO> getCartItems(WebSession session) {
        return this.getCart(session)
                .flatMapMany(cart -> {
                    if (cart.isEmpty()) {
                        return Flux.empty();
                    }

                    return itemRepository.findAllById(cart.getItems().keySet())
                            .map(item -> {
                                int count = cart.getCount(item.getId());

                                return ItemDTO.builder()
                                        .id(item.getId())
                                        .title(item.getTitle())
                                        .description(item.getDescription())
                                        .price(item.getPrice())
                                        .imgPath(item.getImgPath())
                                        .count(count)
                                        .build();
                            });
                });
    }

    public Mono<Double> getTotalPrice(WebSession session) {
        return getCartItems(session)
                .map(item -> item.getPrice() * item.getCount())
                .reduce(0.0, Double::sum)
                .defaultIfEmpty(0.0);
    }

}
