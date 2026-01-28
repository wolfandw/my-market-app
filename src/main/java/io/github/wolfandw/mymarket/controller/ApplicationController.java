package io.github.wolfandw.mymarket.controller;

import io.github.wolfandw.mymarket.MyMarketUtils;
import io.github.wolfandw.mymarket.service.CartService;
import io.github.wolfandw.mymarket.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Контроллер приложения.
 */
@Controller
@RequestMapping("/")
public class ApplicationController {
    private final CartService cartService;
    private final OrderService orderService;

    /**
     * Создает контроллер приложения.
     *
     * @param cartService  сервис корзин
     * @param orderService сервис заказов
     */
    public ApplicationController(CartService cartService, OrderService orderService) {
        this.cartService = cartService;
        this.orderService = orderService;
    }

    /**
     * Редирект на витрину товаров.
     *
     * @return строка редиректа на витрину
     */
    @GetMapping
    public String redirectToItems() {
        return MyMarketUtils.REDIRECT +
                '/' + MyMarketUtils.TEMPLATE_ITEMS;
    }

    /**
     * Оформление заказа и редирект на страницу заказа.
     *
     * @return строка редиректа на заказ
     */
    @PostMapping("/buy")
    public String buy() {
        return orderService.createOrderByCart(MyMarketUtils.DEFAULT_CART_ID).map(orderDto -> {
            cartService.clearCart(MyMarketUtils.DEFAULT_CART_ID);
            return MyMarketUtils.REDIRECT + '/' + MyMarketUtils.TEMPLATE_ORDERS +
                    '/' + orderDto.id() + '?' + MyMarketUtils.PARAMETER_NEW_ORDER + '=' + Boolean.TRUE;
        }).orElse(MyMarketUtils.REDIRECT + '/' + MyMarketUtils.TEMPLATE_ORDERS);
    }
}
