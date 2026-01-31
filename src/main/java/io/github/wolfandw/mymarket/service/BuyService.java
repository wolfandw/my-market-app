package io.github.wolfandw.mymarket.service;

import io.github.wolfandw.mymarket.dto.OrderDto;

import java.util.Optional;

/**
 * Сервис покупки товаров.
 */
public interface BuyService {
    /**
     * Купить товары в корзине.
     *
     * @param cartId идентификатор корзины.
     * @return заказ.
     */
    Optional<OrderDto> buy(Long cartId);
}
