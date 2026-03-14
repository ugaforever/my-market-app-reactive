package ru.ugaforever.market.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.ugaforever.market.dto.ItemDTO;
import ru.ugaforever.market.service.CartService;

import java.util.List;

@Slf4j  // (log)
@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    /**
     * Эндпоинт получения страницы со списком товаров в корзине
     *
     */
    @GetMapping("/items")
    public ModelAndView getItemsCart() {

        List<ItemDTO> itemsCart = cartService.getCartItems();

        ModelAndView modelAndView = new ModelAndView("cart");
        modelAndView.addObject("items", itemsCart);
        modelAndView.addObject("total", cartService.getTotalPrice());

        return modelAndView;
    }

    /**
     * уменьшение/увеличение количества товара в корзине со страницы корзины
     * POST /cart/items?id=[id]&action=[action]
     *
     * @param id        обязательный параметр, [id] — идентификатор товара
     * @param action    обязательный параметр, объект действия с товаром, [action] — значение MINUS — уменьшить число товаров с id в корзине на один, PLUS — увеличить число товаров с id в корзине на один, DELETE — удалить товар с id из корзины
     */
    @PostMapping("/items")
    public ModelAndView handleItemActionById(
            @RequestParam Long id,
            @RequestParam String action) {

        // Обработка действий
        if ("PLUS".equals(action)) {
            cartService.addToCart(id);
        } else if ("MINUS".equals(action)) {
            cartService.removeFromCart(id);
        } else if ("DELETE".equals(action)) {
            cartService.removeAllItems(id);
        }

        List<ItemDTO> itemsCart = cartService.getCartItems();

        ModelAndView modelAndView = new ModelAndView("cart");
        modelAndView.addObject("items", itemsCart);
        modelAndView.addObject("total", cartService.getTotalPrice());

        return modelAndView;
    }
}
