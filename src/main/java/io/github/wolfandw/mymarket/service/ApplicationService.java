package io.github.wolfandw.mymarket.service;

import io.github.wolfandw.mymarket.dto.OrderDto;

import java.util.Optional;

/**
 * Сервис приложения.
 */
public interface ApplicationService {
    /**
     * Оформляет заказ по данным корзины.
     *
     * @param cartId идентификатор корзины
     * @return DTO-представление оформленного заказа.
     */
    Optional<OrderDto> buy(Long cartId);
}
