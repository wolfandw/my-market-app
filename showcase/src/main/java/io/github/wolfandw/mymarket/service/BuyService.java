package io.github.wolfandw.mymarket.service;

import io.github.wolfandw.mymarket.dto.OrderDto;
import reactor.core.publisher.Mono;

/**
 * Сервис покупки товаров.
 */
public interface BuyService {
    /**
     * Купить товары в корзине пользователя.
     *
     * @return заказ.
     */
    Mono<OrderDto> buy();
}
