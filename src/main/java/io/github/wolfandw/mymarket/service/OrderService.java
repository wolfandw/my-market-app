package io.github.wolfandw.mymarket.service;

import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.dto.OrderDto;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с заказами.
 */
public interface OrderService {
    /**
     * Возвращает DTO-представление списка заказов.
     *
     * @return DTO-представление списка заказов.
     */
    List<OrderDto> getOrders();

    /**
     * Возвращает DTO-представление заказа.
     *
     * @param id       идентификатор заказа
     * @param newOrder новый заказ
     * @return DTO-представление заказа.
     */
    Optional<OrderDto> getOrder(Long id, boolean newOrder);

    OrderDto createOrder(Long totalSum, List<ItemDto> items);
}
