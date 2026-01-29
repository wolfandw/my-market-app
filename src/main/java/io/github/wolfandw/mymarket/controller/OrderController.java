package io.github.wolfandw.mymarket.controller;

import io.github.wolfandw.mymarket.dto.DtoConstants;
import io.github.wolfandw.mymarket.dto.OrderDto;
import io.github.wolfandw.mymarket.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Контроллер для работы с заказами товаров.
 */
@Controller
@RequestMapping("/orders")
public class OrderController {
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
     * @param model модель заказа
     * @return шаблон заказов
     */
    @GetMapping
    public String getOrders(Model model) {
        List<OrderDto> orders = orderService.getOrders();
        model.addAttribute(DtoConstants.ATTRIBUTE_ORDERS, orders);
        return DtoConstants.TEMPLATE_ORDERS;
    }

    /**
     * Возвращает страницу заказа.
     *
     * @param id  идентификатор заказа
     * @param newOrder признак нового заказа
     * @param model модель заказа
     * @return шаблон заказа
     */
    @GetMapping("/{id}")
    public String getOrder(@PathVariable Long id,
                           @RequestParam(value = DtoConstants.PARAMETER_NEW_ORDER, required = false, defaultValue = "false") boolean newOrder,
                           Model model) {
        orderService.getOrder(id, newOrder).ifPresent(order -> {
            model.addAttribute(DtoConstants.ATTRIBUTE_ORDER, order);
            model.addAttribute(DtoConstants.ATTRIBUTE_TOTAL_SUM, order.totalSum());
            model.addAttribute(DtoConstants.ATTRIBUTE_NEW_ORDER, newOrder);
        });
        return DtoConstants.TEMPLATE_ORDER;
    }
}
