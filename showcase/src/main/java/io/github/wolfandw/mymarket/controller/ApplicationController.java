package io.github.wolfandw.mymarket.controller;

import io.github.wolfandw.mymarket.service.BuyService;
import io.github.wolfandw.mymarket.service.PaymentsService;
import io.github.wolfandw.payment.client.domain.ReceiptDto;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private static final String TEMPLATE_LOGIN = "orders";

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
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/buy")
    public Mono<String> buy() {
        return buyService.buy().map(orderDto ->
                        RedirectUrlFactory.createRedirectUrlToNewOrder(orderDto.id())).
                switchIfEmpty(Mono.just(RedirectUrlFactory.createRedirectUrlToUserCart()));
    }

    /**
     * Пополнение баланса и редирект на страницу корзины.
     *
     * @return строка редиректа на корзину
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/topUpBalance")
    public Mono<String> topUpBalance(@ModelAttribute ReceiptDto receiptDto) {
        return paymentsService.topUpUserBalance(receiptDto).map(balanceDto ->
                        RedirectUrlFactory.createRedirectUrlToUserCart()).
                switchIfEmpty(Mono.just(RedirectUrlFactory.createRedirectUrlToUserCart()));
    }
}
