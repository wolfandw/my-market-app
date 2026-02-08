package io.github.wolfandw.mymarket.service;

import io.github.wolfandw.mymarket.dto.CartDto;
import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.model.Cart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

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
    Flux<ItemDto> getCartItems(Long id);

    /**
     * Возвращает DTO-представление корзины.
     *
     * @param id идентификатор корзины
     * @return DTO-представление корзины.
     */
    Mono<CartDto> getCart(Long id);

    /**
     * Изменяет количество товара в корзине со страницы товаров.
     *
     * @param cartId идентификатор корзины
     * @param itemId идентификатор товара
     * @param action увеличить (уменьшить) количество товара в корзине
     */
    Mono<Void> changeItemCount(Long cartId, Long itemId, String action);
}
