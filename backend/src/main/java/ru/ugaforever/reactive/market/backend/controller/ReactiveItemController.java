package ru.ugaforever.reactive.market.backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.ugaforever.reactive.market.backend.dto.ItemDTO;
import ru.ugaforever.reactive.market.backend.model.ReactiveCart;
import ru.ugaforever.reactive.market.backend.service.ReactiveCartService;
import ru.ugaforever.reactive.market.backend.service.ReactiveItemService;
import ru.ugaforever.reactive.market.backend.util.ItemUtils;

import java.util.List;


@Slf4j
@Controller
@RequiredArgsConstructor
public class ReactiveItemController {

    private final ReactiveItemService itemService;
    private final ReactiveCartService cartService;

    //Главная страница проекта с витриной товаров
    @GetMapping({"/items", "/"})
    public Mono<Rendering> getItems(
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "NO") String sort,
            @RequestParam(required = false, defaultValue = "1") int pageNumber,
            @RequestParam(required = false, defaultValue = "5") int pageSize,
            WebSession session) {

        if (pageNumber < 1) {
            pageNumber = 1;
        }

        Sort springSort = ItemUtils.convertToSpringSort(sort);

        Pageable sortedPageable = PageRequest.of(
                pageNumber - 1,
                pageSize,
                springSort
        );

        return itemService.findItemsWithFilters(search, sortedPageable)
                .flatMap(itemsPage ->
                        addCartCountsToItems(session, itemsPage.getContent())
                                .map(itemsWithCartCount -> {

                                    Page<ItemDTO> updatedPage = new PageImpl<>(
                                            itemsWithCartCount,
                                            itemsPage.getPageable(),
                                            itemsPage.getTotalElements()
                                    );

                                    List<List<ItemDTO>> itemsRows = ItemUtils.groupItemsForDisplay(
                                            itemsWithCartCount, 3);

                                    return Rendering.view("items")
                                            .modelAttribute("items", itemsRows)
                                            .modelAttribute("paging", updatedPage)
                                            .modelAttribute("search", search)
                                            .modelAttribute("sort", sort)
                                            .build();
                                })
                )
                .switchIfEmpty(Mono.just(
                        Rendering.view("items").build()
                ));
    }

    private Mono<List<ItemDTO>> addCartCountsToItems(WebSession session, List<ItemDTO> items) {
        return Flux.fromIterable(items)
                .flatMap(item ->
                        cartService.getCount(session, item.getId()) //getCount реактивный
                                .map(count -> {
                                    item.setCount(count);
                                    return item;
                                })
                )
                .collectList();
    }

    //Страница с конкретным товаром
    @GetMapping("/items/{id}")
    public Mono<String> getItemById(@PathVariable Long id,
                                    WebSession session,
                                    Model model) {
        return itemService.findById(id)
                .flatMap(item ->
                        cartService.getCount(session, item.getId())
                                .map(count -> {
                                    item.setCount(count);
                                    return item;
                                })
                )
                .doOnNext(item -> model.addAttribute("item", item))
                .thenReturn("item");
    }


    //Уменьшение/увеличение количества товара в корзине со страницы товаров в корзине
    @PostMapping("/items")
    public Mono<Rendering> handleItemAction(
            @RequestParam Long id,
            @RequestParam String action,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "NO") String sort,
            @RequestParam(required = false, defaultValue = "1") int pageNumber,
            @RequestParam(required = false, defaultValue = "5") int pageSize,
            WebSession session) {

        //todo сделать через аспекты наконец-таки !!
        /*log.info("Received request: id='{}', action='{}', search='{}', sort='{}', pageNumber='{}', pageSize='{}'",
                id, action, search, sort, pageNumber, pageSize);*/

        if (pageNumber < 1) {
            pageNumber = 1;
        }

        return executeCartAction(session, action, id)
                .thenReturn(buildRedirect(search, sort, pageNumber, pageSize));
    }

    //Уменьшение/увеличение количества товара в корзине со страницы товара
    @PostMapping("/items/{id}")
    public Mono<Rendering>  handleItemActionById(
            @PathVariable Long id,
            @RequestParam String action,
            WebSession session) {

        System.out.println("=== DEBUG: id=" + id + ", action=" + action);
        log.info("Received request: id='{}', action='{}'", id, action);

        return itemService.findById(id)
                .flatMap(item ->
                        executeCartAction(session, action, id)
                                .map(cart -> {
                                    item.setCount(cart.getCount(id));
                                    return item;
                                })
                )
                .map(item -> Rendering.view("item")
                        .modelAttribute("item", item)
                        .build()
                );
    }

    //Добавить товар
    @PostMapping("/items/{id}/plus")
    public Mono<Rendering> plusItem(@PathVariable Long id, WebSession session) {
        return cartService.plusItem(session, id)
                .thenReturn(Rendering.view("redirect:/items/" + id).build());
    }

    //Убавить товар
    @PostMapping("/items/{id}/minus")
    public Mono<Rendering> minusItem(@PathVariable Long id, WebSession session) {
        return cartService.minusItem(session, id)
                .thenReturn(Rendering.view("redirect:/items/" + id).build());
    }

    //Добавить в корзину (для кнопки CART)
    @PostMapping("/items/{id}/add")
    public Mono<Rendering> addToCart(@PathVariable Long id, WebSession session) {
        return cartService.plusItem(session, id)
                .thenReturn(Rendering.view("redirect:/items/" + id).build());
    }

    private Mono<ReactiveCart> executeCartAction(WebSession session, String action, Long itemId) {
        if ("PLUS".equals(action)) {
            return cartService.plusItem(session, itemId);
        } else if ("MINUS".equals(action)) {
            return cartService.minusItem(session, itemId);
        } else if ("DELETE".equals(action)) {
            return cartService.removeItem(session, itemId);
        }
        return Mono.empty();
    }

    private Rendering buildRedirect(String search, String sort, int pageNumber, int pageSize) {
        String redirectUrl = String.format("/items?search=%s&sort=%s&pageNumber=%d&pageSize=%d",
                search != null ? search : "",
                sort,
                pageNumber,
                pageSize);

        return Rendering.redirectTo(redirectUrl).build();
    }
}

