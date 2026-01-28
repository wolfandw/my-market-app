package io.github.wolfandw.mymarket.service;

import io.github.wolfandw.mymarket.dto.CartDto;

/**
 * Сервис для работы с корзинами.
 */
public interface CartService {
    /**
     * Возвращает DTO-представление корзины.
     *
     * @param id идентификатор корзины
     * @return DTO-представление корзины.
     */
    CartDto getCart(Long id);

    /**
     * Изменяет количество товара в корзине со страницы товаров.
     *
     * @param cartId идентификатор корзины
     * @param itemId идентификатор товара
     * @param action увеличить (уменьшить) количество товара в корзине
     */
    void changeItemCount(Long cartId, Long itemId, String action);

    /**
     * Очищает корзину.
     *
     * @param cartId идентификатор корзины
     */
    void clearCart(Long cartId);
}
