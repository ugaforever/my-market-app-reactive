package ru.ugaforever.reactive.market.controller;

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
import ru.ugaforever.reactive.market.dto.ItemDTO;
import ru.ugaforever.reactive.market.service.ReactiveCartService;
import ru.ugaforever.reactive.market.service.ReactiveItemService;
import ru.ugaforever.reactive.market.util.ItemUtils;

import java.util.List;


@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ReactiveItemService itemService;
    private final ReactiveCartService cartService;

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

        // Конвертируем строку сортировки в Sort объект Spring Data
        Sort springSort = ItemUtils.convertToSpringSort(sort);

        // Создаем новый Pageable с нашей сортировкой
        Pageable sortedPageable = PageRequest.of(
                pageNumber - 1,
                pageSize,
                springSort
        );

        // Получаем страницу товаров через реактивный сервис
        return itemService.findItemsWithFilters(search, sortedPageable)
                .flatMap(itemsPage ->
                        addCartCountsToItems(session, itemsPage.getContent())
                                .map(itemsWithCartCount -> {
                                    // Обновляем страницу с товарами, содержащими количество в корзине
                                    Page<ItemDTO> updatedPage = new PageImpl<>(
                                            itemsWithCartCount,
                                            itemsPage.getPageable(),
                                            itemsPage.getTotalElements()
                                    );

                                    List<List<ItemDTO>> itemsRows = ItemUtils.groupItemsForDisplay(
                                            itemsWithCartCount, 3);

                                    // Создаем Rendering объект
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


/*    @PostMapping("/items")
    public String handleItemAction(
            @RequestParam Long id,
            @RequestParam String action,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "NO") String sort,
            @RequestParam(required = false, defaultValue = "1") int pageNumber,
            @RequestParam(required = false, defaultValue = "5") int pageSize) {

        //todo сделать через аспекты наконец-таки !!
        log.info("Received request: id='{}', action='{}', search='{}', sort='{}', pageNumber='{}', pageSize='{}'",
                id, action, search, sort, pageNumber, pageSize);


        if (pageNumber < 1) {
            pageNumber = 1;
        }

        // Обработка действий
        if ("PLUS".equals(action)) {
            cartService.addToCart(id);
        } else if ("MINUS".equals(action)) {
            cartService.removeFromCart(id);
        }

        // Перенаправляем обратно на страницу с сохранением параметров
        return "redirect:/items?search=" + (search != null ? search : "")
                + "&sort=" + sort
                + "&pageNumber=" + pageNumber
                + "&pageSize=" + pageSize;
    }*/


/*    @PostMapping("/items/{id}")
    public ModelAndView handleItemActionById(
            @PathVariable Long id,
            @RequestParam String action) {

        Item item = itemService.findById(id);

        // Обработка действий
        if ("PLUS".equals(action)) {
            cartService.addToCart(id);
        } else if ("MINUS".equals(action)) {
            cartService.removeFromCart(id);
        }

        ModelAndView modelAndView = new ModelAndView("item");
        modelAndView.addObject("item", item);

        return modelAndView;
    }*/
}

