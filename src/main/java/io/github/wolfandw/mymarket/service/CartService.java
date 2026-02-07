package io.github.wolfandw.mymarket.service;

import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Сервис для работы с корзинами.
 */
public interface CartService {
    /**
     * Возвращает количество товара в корзине в разрезе идентификаторов товара.
     *
     * @param cartId идентификатор корзины
     * @return количество товара в корзине в разрезе идентификаторов товара
     */
    Mono<Map<Long, Integer>> getCartItemsCount(Long cartId);

    /**
     * Возвращает количество товара в корзине по идентификатору товара.
     *
     * @param cartId идентификатор корзины
     * @param itemId идентификатор товара
     * @return количество товара в корзине в разрезе идентификаторов товара
     */
    Mono<Integer> getCartItemCount(Long cartId, Long itemId);

//    /**
//     * Возвращает DTO-представление корзины.
//     *
//     * @param id идентификатор корзины
//     * @return DTO-представление корзины.
//     */
//    CartDto getCart(Long id);
//
//    /**
//     * Изменяет количество товара в корзине со страницы товаров.
//     *
//     * @param cartId идентификатор корзины
//     * @param itemId идентификатор товара
//     * @param action увеличить (уменьшить) количество товара в корзине
//     */
//    void changeItemCount(Long cartId, Long itemId, String action);
}
