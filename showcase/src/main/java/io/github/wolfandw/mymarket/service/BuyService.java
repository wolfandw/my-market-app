package io.github.wolfandw.mymarket.service;

import io.github.wolfandw.mymarket.dto.OrderDto;
import reactor.core.publisher.Mono;

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
    Mono<OrderDto> buy(Long cartId);
}
