package io.github.wolfandw.mymarket.controller;

import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.dto.OrderDto;
import io.github.wolfandw.mymarket.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    private static final String PARAMETER_NEW_ORDER = "newOrder";

    private final OrderService orderService;

    /**
     * Создает контроллер заказов.
     *
     * @param orderService сервис заказов
     */
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Возвращает страницу заказов.
     *
     * @return шаблон заказов
     */
    @GetMapping
    public Mono<Rendering> getOrders() {
        Flux<OrderDto> ordersFlux = orderService.getOrders();
        return Mono.just(Rendering.view(TEMPLATE_ORDERS)
                .modelAttribute(ATTRIBUTE_ORDERS, ordersFlux)
                .build());
    }

    /**
     * Возвращает страницу заказа.
     *
     * @param id  идентификатор заказа
     * @param newOrder признак нового заказа
     * @return шаблон заказа
     */
    @GetMapping("/{id}")
    public Mono<Rendering> getOrder(@PathVariable Long id,
                                    @RequestParam(value = PARAMETER_NEW_ORDER, required = false, defaultValue = "false") boolean newOrder) {
        Mono<OrderDto>         orderMono = orderService.getOrder(id, newOrder);
        Flux<ItemDto>         orderItemsFlux = orderService.getOrderItems(id);
        return orderMono.map(orderDto -> Rendering.view(TEMPLATE_ORDER)
                        .modelAttribute(ATTRIBUTE_ORDER, orderDto)
                        .modelAttribute(ATTRIBUTE_ITEMS, orderItemsFlux)
                        .modelAttribute(ATTRIBUTE_NEW_ORDER, newOrder)
                        .build()
        ).switchIfEmpty(Mono.just(Rendering.redirectTo(RedirectUrlFactory.createUrlToOrders()).build()));
    }
}
