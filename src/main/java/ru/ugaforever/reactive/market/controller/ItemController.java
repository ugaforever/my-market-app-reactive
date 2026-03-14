package ru.ugaforever.reactive.market.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;
import ru.ugaforever.reactive.market.service.ReactiveCartService;
import ru.ugaforever.reactive.market.service.ReactiveItemService;


@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ReactiveItemService itemService;
    private final ReactiveCartService cartService;


    /*@GetMapping({"/items", "/"})
     public ModelAndView getItems(
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "NO") String sort,
            @RequestParam(required = false, defaultValue = "1") int pageNumber,
            @RequestParam(required = false, defaultValue = "5") int pageSize) {

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

        // Получаем страницу товаров через сервис
        Page<ItemDTO> itemsPage = itemService.findItemsWithFilters(
                search, sortedPageable);

        // Разбиваем список товаров на ряды по 3 в ряд
        List<List<ItemDTO>> itemsRows = ItemUtils.groupItemsForDisplay(itemsPage.getContent(), 3);

        // Добавляем информацию о количестве в корзине
        itemsPage.getContent().forEach(item ->
                item.setCount(cartService.getCountInCart(item.getId()))
        );

        ModelAndView modelAndView = new ModelAndView("items");
        modelAndView.addObject("items", itemsRows);
        modelAndView.addObject("paging", itemsPage);
        modelAndView.addObject("search", search);
        modelAndView.addObject("sort", sort);

        return modelAndView;
    }*/

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
                .thenReturn("item");  // имя шаблона
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

