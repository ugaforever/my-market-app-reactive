package ru.ugaforever.reactive.market.backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;
import ru.ugaforever.reactive.market.backend.dto.ItemDTO;
import ru.ugaforever.reactive.market.backend.model.ReactiveCart;
import ru.ugaforever.reactive.market.backend.service.ReactiveCartService;

import java.util.List;


@Slf4j  // (log)
@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
@PreAuthorize("isAuthenticated()")
public class ReactiveCartController {

    private final ReactiveCartService cartService;

    //получения страницы со списком товаров в корзине
    @GetMapping("/items")
    public Mono<Rendering> getItemsCart(
            WebSession session,
            Model model) {

        return cartService.getCartItems(session)
                .collectList()
                .flatMap(items ->
                        cartService.getTotalPrice(session)
                                .map(total -> Rendering.view("cart")
                                        .modelAttribute("items", items)
                                        .modelAttribute("total", total)
                                        .build()
                                )
                );
    }

    //уменьшение/увеличение количества товара в корзине со страницы корзины
    @PostMapping("/items")
    public Mono<Rendering> handleItemActionById(
            @RequestParam Long id,
            @RequestParam String action,
            WebSession session) {

        return executeCartAction(session, action, id)
                .flatMap(cart ->
                        Mono.zip(
                                cartService.getCartItems(session).collectList(),
                                cartService.getTotalPrice(session)
                        )
                )
                .map(tuple -> {
                    List<ItemDTO> items = tuple.getT1();
                    Double total = tuple.getT2();

                    return Rendering.view("cart")
                            .modelAttribute("items", items)
                            .modelAttribute("total", total)
                            .build();
                });
    }

    private Mono<ReactiveCart> executeCartAction(
            WebSession session,
            String action,
            Long itemId) {

        return switch (action) {
            case "PLUS" -> cartService.plusItem(session, itemId);
            case "MINUS" -> cartService.minusItem(session, itemId);
            case "DELETE" -> cartService.removeItem(session, itemId);
            default -> Mono.error(new IllegalArgumentException("Неизвестное действие: " + action));
        };
    }
}

