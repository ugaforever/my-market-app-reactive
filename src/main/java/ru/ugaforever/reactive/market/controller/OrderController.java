/*
package ru.ugaforever.market.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.ugaforever.market.dto.ItemDTO;
import ru.ugaforever.market.entity.Order;
import ru.ugaforever.market.service.CartService;
import ru.ugaforever.market.service.OrderService;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;

    */
/**
     * Эндпоинт получения страницы со списком заказов
     *
     *//*

    @GetMapping("/orders")
    public ModelAndView getOrders() {

        List<Order> orders = orderService.findAllWithItems();

        ModelAndView modelAndView = new ModelAndView("orders");
        modelAndView.addObject("orders", orders);

        return modelAndView;
    }

    */
/**
     * Эндпоинт получения страницы заказа
     *
     * @param id            идентификатор заказа
     * @param newOrder      опциональный параметр, true если совершён переход с кнопки покупки товаров (по умолчанию false)
     *//*

    @GetMapping("/orders/{id}")
    public ModelAndView getOrderById(
            @PathVariable Long id,
            @RequestParam (required = false, defaultValue = "false")  Boolean newOrder) {

        Order order = orderService.findById(id);

        ModelAndView modelAndView = new ModelAndView("order");
        modelAndView.addObject("order", order);
        modelAndView.addObject("newOrder", newOrder);

        return modelAndView;
    }

    */
/**
     * Эндпоинт совершения заказа
     *
     *//*

    @PostMapping("/buy")
    public String createOrderFromCart(){
        List<ItemDTO> itemsCart = cartService.getCartItems();

        if (itemsCart.isEmpty()) {
            throw new IllegalStateException("Корзина пуста");
        }

        //создаем новый заказ
        long id = orderService.createOrder(itemsCart);

        //очищаем корзину
        cartService.clearCart();

        return "redirect:/orders/" + id + "?newOrder=true";
    }
}
*/
