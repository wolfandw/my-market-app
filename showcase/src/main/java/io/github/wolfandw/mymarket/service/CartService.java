package io.github.wolfandw.mymarket.service;

import io.github.wolfandw.mymarket.dto.CartDto;
import io.github.wolfandw.mymarket.dto.ItemDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Сервис для работы с корзинами.
 */
public interface CartService {
    /**
     * Возвращает DTO-представление корзины.
     *
     * @param cartId идентификатор корзины
     * @return DTO-представление корзины.
     */
    Flux<ItemDto> getCartItems(Long cartId);

    /**
     * Возвращает DTO-представление корзины.
     *
     * @param cartId идентификатор корзины
     * @return DTO-представление корзины.
     */
    Mono<CartDto> getCart(Long cartId);

    /**
     * Изменяет количество товара в корзине со страницы товаров.
     *
     * @param cartId идентификатор корзины
     * @param itemId идентификатор товара
     * @param action увеличить (уменьшить) количество товара в корзине
     */
    Mono<Void> changeItemCount(Long cartId, Long itemId, String action);

    /**
     * Возвращает DTO-представление корзины пользователя.
     *
     * @return DTO-представление корзины.
     */
    Flux<ItemDto> getUserCartItems();

    /**
     * Возвращает DTO-представление корзины пользователя.
     *
     * @return DTO-представление корзины.
     */
    Mono<CartDto> getUserCart();

    /**
     * Изменяет количество товара в корзине со страницы товаров пользователя.
     *
     * @param itemId идентификатор товара
     * @param action увеличить (уменьшить) количество товара в корзине
     */
    Mono<Void> changeUserItemCount(Long itemId, String action);
}
