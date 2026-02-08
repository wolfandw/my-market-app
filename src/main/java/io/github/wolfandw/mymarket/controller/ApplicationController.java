package io.github.wolfandw.mymarket.controller;

import io.github.wolfandw.mymarket.service.BuyService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

/**
 * Контроллер приложения.
 */
@Controller
@RequestMapping("/")
public class ApplicationController {
    private static final Long DEFAULT_CART_ID = 1L;

    private final BuyService buyService;

    /**
     * Создает контроллер приложения.
     *
     * @param buyService  сервис корзин
     */
    public ApplicationController(BuyService buyService) {
        this.buyService = buyService;
    }

    /**
     * Редирект на витрину товаров.
     *
     * @return строка редиректа на витрину
     */
    @GetMapping
    public Mono<String> redirectToItems() {
        return Mono.just(RedirectUrlFactory.createRedirectUrlToItems());
    }

    /**
     * Оформление заказа и редирект на страницу заказа.
     *
     * @return строка редиректа на заказ
     */
    @PostMapping("/buy")
    public Mono<String> buy() {
        return buyService.buy(DEFAULT_CART_ID).map(orderDto ->
                RedirectUrlFactory.createRedirectUrlToNewOrder(orderDto.id())).
                switchIfEmpty(Mono.just(RedirectUrlFactory.createRedirectUrlToOrders()));
    }
}
