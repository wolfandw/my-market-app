package io.github.wolfandw.mymarket.controller;

import io.github.wolfandw.mymarket.service.BuyService;
import io.github.wolfandw.mymarket.service.PaymentsService;
import io.github.wolfandw.payment.client.domain.ReceiptDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    private final PaymentsService paymentsService;

    /**
     * Создает контроллер приложения.
     *
     * @param buyService      сервис корзин
     * @param paymentsService сервис платежей
     */
    public ApplicationController(BuyService buyService, PaymentsService paymentsService) {
        this.buyService = buyService;
        this.paymentsService = paymentsService;
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
                switchIfEmpty(Mono.just(RedirectUrlFactory.createRedirectUrlToCart(DEFAULT_CART_ID)));
    }

    /**
     * Пополнение баланса и редирект на страницу корзины.
     *
     * @return строка редиректа на корзину
     */
    @PostMapping("/topUpBalance")
    public Mono<String> topUpBalance(@ModelAttribute ReceiptDto receiptDto) {
        return paymentsService.topUpBalance(receiptDto.getId(), receiptDto).map(balanceDto ->
                        RedirectUrlFactory.createRedirectUrlToCart(receiptDto.getId())).
                switchIfEmpty(Mono.just(RedirectUrlFactory.createRedirectUrlToCart(receiptDto.getId())));
    }
}
