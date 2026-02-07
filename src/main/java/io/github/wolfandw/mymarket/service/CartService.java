package io.github.wolfandw.mymarket.service;

import io.github.wolfandw.mymarket.dto.CartDto;
import org.springframework.transaction.annotation.Transactional;
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
    Mono<Map<Long, Integer>> getCartCount(Long cartId);

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
