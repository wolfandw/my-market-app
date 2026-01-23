package io.github.wolfandw.mymarket.controller;

import io.github.wolfandw.mymarket.MyMarketConstants;
import io.github.wolfandw.mymarket.dto.OrderDto;
import io.github.wolfandw.mymarket.service.ApplicationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

/**
 * Контроллер приложения.
 */
@Controller
@RequestMapping("/")
public class ApplicationController {
    private static final String REDIRECT_TO_ITEMS = "redirect:/items";
    private static final String REDIRECT_ORDERS = "redirect:/orders";
    private static final String PARAMETER_NEW_ORDER = "newOrder=";

    private final ApplicationService applicationService;

    /**
     * Создает контроллер приложения.
     *
     * @param applicationService сервис приложения
     */
    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    /**
     * Редирект на витрину товаров.
     *
     * @return строка редиректа на витрину
     */
    @GetMapping
    public String redirectToItems() {
        return REDIRECT_TO_ITEMS;
    }

    /**
     * Оформление заказа и редирект на страницу заказа.
     *
     * @return строка редиректа на заказ
     */
    @PostMapping("/buy")
    public String buy() {
        Optional<OrderDto> newOrder = applicationService.buy(MyMarketConstants.DEFAULT_CART_ID);
        StringBuilder redirect = new StringBuilder(REDIRECT_ORDERS);
        newOrder.ifPresent(orderDto -> {
            redirect.append('/').
                     append(orderDto.id()).
                     append('?').
                     append(PARAMETER_NEW_ORDER).append("true");
        });
        return redirect.toString();
    }
}
