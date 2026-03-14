package io.github.wolfandw.mymarket.service;

import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.dto.OrderDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Сервис для работы с заказами.
 */
public interface OrderService {
    /**
     * Возвращает DTO-представление списка заказов.
     *
     * @return DTO-представление списка заказов.
     */
    Flux<OrderDto> getOrders();

    /**
     * Возвращает DTO-представление заказа.
     *
     * @param orderId       идентификатор заказа
     * @param newOrder новый заказ
     * @return DTO-представление заказа.
     */
    Mono<OrderDto> getOrder(Long orderId, boolean newOrder);

    /**
     * Возвращает DTO-представление списка товаров заказа.
     *
     * @param orderId       идентификатор заказа
     * @return DTO-представление списка заказов.
     */
    Flux<ItemDto> getOrderItems(Long orderId);

    /**
     * Возвращает DTO-представление списка заказов пользователя.
     *
     * @return DTO-представление списка заказов пользователя.
     */
    Flux<OrderDto> getUserOrders();

    /**
     * Возвращает DTO-представление заказа пользователя.
     *
     * @param orderId       идентификатор заказа
     * @param newOrder новый заказ
     * @return DTO-представление заказа.
     */
    Mono<OrderDto> getUserOrder(Long orderId, boolean newOrder);

    /**
     * Возвращает DTO-представление списка товаров заказа пользователя.
     *
     * @param orderId       идентификатор заказа
     * @return DTO-представление списка заказов.
     */
    Flux<ItemDto> getUserOrderItems(Long orderId);
}
