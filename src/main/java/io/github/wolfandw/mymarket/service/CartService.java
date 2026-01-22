package io.github.wolfandw.mymarket.service;

import io.github.wolfandw.mymarket.dto.CartDto;
import io.github.wolfandw.mymarket.model.Cart;

/**
 * Сервис для работы с корзинами.
 */
public interface CartService {
    /**
     * Возвращает DTO-представление корзины.
     * @return DTO-представление корзины.
     */
    CartDto getCart();

    /**
     * Возвращает корзину по-умолчанию.
     *
     * @return корзина по-умолчанию
     */
    Cart getDefaultCart();
}
