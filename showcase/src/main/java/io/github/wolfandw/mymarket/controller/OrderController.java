package io.github.wolfandw.mymarket.controller;

import io.github.wolfandw.mymarket.dto.OrderDto;
import io.github.wolfandw.mymarket.dto.UserInfoDto;
import io.github.wolfandw.mymarket.service.OrderService;
import io.github.wolfandw.mymarket.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Контроллер для работы с заказами товаров.
 */
@Controller
@RequestMapping("/orders")
public class OrderController {
    private static final String TEMPLATE_ORDERS = "orders";
    private static final String TEMPLATE_ORDER = "order";

    private static final String ATTRIBUTE_ORDERS = "orders";
    private static final String ATTRIBUTE_ORDER = "order";
    private static final String ATTRIBUTE_ITEMS = "items";
    private static final String ATTRIBUTE_NEW_ORDER = "newOrder";
    private static final String ATTRIBUTE_USER_INFO = "userInfo";

    private static final String PARAMETER_NEW_ORDER = "newOrder";

    private final OrderService orderService;
    private final UserService userService;

    /**
     * Создает контроллер заказов.
     *
     * @param orderService сервис заказов
     * @param userService сервис пользователей
     */
    public OrderController(OrderService orderService,
                           UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    /**
     * Возвращает страницу заказов.
     *
     * @return шаблон заказов
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public Mono<Rendering> getOrders() {
        Flux<OrderDto> ordersFlux = orderService.getUserOrders();
        Mono<UserInfoDto> userInfoMono = userService.getCurrentUserInfo();
        return Mono.just(Rendering.view(TEMPLATE_ORDERS)
                .modelAttribute(ATTRIBUTE_ORDERS, ordersFlux)
                .modelAttribute(ATTRIBUTE_USER_INFO, userInfoMono)
                .build());
    }

    /**
     * Возвращает страницу заказа.
     *
     * @param id       идентификатор заказа
     * @param newOrder признак нового заказа
     * @return шаблон заказа
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public Mono<Rendering> getOrder(@PathVariable Long id,
                                    @RequestParam(value = PARAMETER_NEW_ORDER, required = false, defaultValue = "false") boolean newOrder) {
        Mono<OrderDto> orderMono = orderService.getUserOrder(id, newOrder);
        Mono<UserInfoDto> userInfoMono = userService.getCurrentUserInfo();
        return orderMono.map(orderDto -> Rendering.view(TEMPLATE_ORDER)
                .modelAttribute(ATTRIBUTE_ORDER, orderDto)
                .modelAttribute(ATTRIBUTE_ITEMS, orderService.getUserOrderItems(orderDto.id()))
                .modelAttribute(ATTRIBUTE_NEW_ORDER, newOrder)
                .modelAttribute(ATTRIBUTE_USER_INFO, userInfoMono)
                .build()
        ).switchIfEmpty(Mono.just(Rendering.redirectTo(RedirectUrlFactory.createUrlToOrders()).build()));
    }
}
