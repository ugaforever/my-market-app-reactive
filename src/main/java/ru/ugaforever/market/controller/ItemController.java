package ru.ugaforever.market.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.ugaforever.market.dto.ItemDTO;
import ru.ugaforever.market.entity.Item;
import ru.ugaforever.market.service.CartService;
import ru.ugaforever.market.service.ItemService;
import ru.ugaforever.market.util.ItemUtils;

import java.util.List;

@Slf4j  // (log)
@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final CartService cartService;

    /**
     * Главная страница проекта с витриной товаров, доступная по адресам / и /items*
     * GET /?search=[search]&sort=[sort]&pageNumber=[pageNumber]&pageSize=[pageSize]
     * GET /items?search=[search]&sort=[sort]&pageNumber=[pageNumber]&pageSize=[pageSize]
     *
     * @param search        опциональный параметр, [search] — строка для поиска по названию/описанию товара (по умолчанию нет поиска — выводить все товары)
     * @param sort          опциональный параметр, [sort] — способ сортировки товаров: NO — без сортировки (значение по умолчанию), ALPHA — по названию, PRICE — по цене
     * @param pageNumber    опциональный параметр, [pageNumber] — номер текущей страницы с товарами (по умолчанию первая страница — 1)
     * @param pageSize      опциональный параметр, [pageSize] — число товаров на странице с товарами (по умолчанию первая страница — 5)
     */
    @GetMapping({"/items", "/"})
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
    }

    /**
     * Страница с конкретным товаром, доступная по адресу /items/{id}
     * @param id - товар
     */
    @GetMapping("/items/{id}")
    public ModelAndView getItemById(@PathVariable Long id) {
        Item item = itemService.findById(id);

        // Добавляем информацию о количестве в корзине
        item.setCount(cartService.getCountInCart(item.getId()));

        ModelAndView modelAndView = new ModelAndView("item");
        modelAndView.addObject("item", item);

        return modelAndView;
    }

    /**
     * Уменьшение/увеличение количества товара в корзине со страницы товаров в корзине
     * POST /items?id=[id]&search=[search]&sort=[sort]&pageNumber=[pageNumber]&pageSize=[pageSize]&action=[action]
     *
     * @param id            обязательный параметр, [id] — идентификатор товара
     * @param action        обязательный параметр, объект действия с товаром, [action] — значение MINUS — уменьшить число товаров с id в корзине на один, PLUS — увеличить число товаров с id в корзине на один
     * @param search        опциональный параметр, [search] — строка для поиска по названию/описанию товара (по умолчанию нет поиска — выводить все товары)
     * @param sort          опциональный параметр, [sort] — способ сортировки товаров: NO — без сортировки (значение по умолчанию), ALPHA — по названию, PRICE — по цене
     * @param pageNumber    опциональный параметр, [pageNumber] — номер текущей страницы с товарами (по умолчанию первая страница — 1)
     * @param pageSize      опциональный параметр, [pageSize] — число товаров на странице с товарами (по умолчанию первая страница — 5)
     */
    @PostMapping("/items")
    public String handleItemAction(
            @RequestParam Long id,
            @RequestParam String action,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "NO") String sort,
            @RequestParam(required = false, defaultValue = "1") int pageNumber,
            @RequestParam(required = false, defaultValue = "5") int pageSize) {

        //todo сделать через аспекты наконец-таки !!
        /*log.info("Received request: id='{}', action='{}', search='{}', sort='{}', pageNumber='{}', pageSize='{}'",
                id, action, search, sort, pageNumber, pageSize);*/

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
    }

    /**
     * Уменьшение/увеличение количества товара в корзине со страницы товара*
     * POST /items/{id}?action=[action]
     *
     * @param id        идентификатор товара
     * @param action    обязательный параметр, объект действия с товаром, [action] — значение MINUS — уменьшить число товаров с id в корзине на один, PLUS — увеличить число товаров с id в корзине на один
     */
    @PostMapping("/items/{id}")
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
    }
}
