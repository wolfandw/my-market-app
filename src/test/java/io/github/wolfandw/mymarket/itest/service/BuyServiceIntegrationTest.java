package io.github.wolfandw.mymarket.itest.service;

import io.github.wolfandw.mymarket.itest.AbstractIntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Интеграционные тесты сервиса покупок.
 */
public class BuyServiceIntegrationTest extends AbstractIntegrationTest {
    @Test
    void buyOrderTest() {
        trxStepVerifier.create(buyService.buy(DEFAULT_CART_ID)).
                consumeNextWith(newOrderDto -> {
                    assertThat(newOrderDto.totalSum()).isEqualTo(7808L);
                }).verifyComplete();
    }

    @Test
    void buyOrderItemsTest() {
        trxStepVerifier.create(buyService.buy(DEFAULT_CART_ID).flatMapMany(orderDto ->
                        orderService.getOrderItems(orderDto.id()).collectList())).
                assertNext(newOrderItems -> {
                    Assertions.assertThat(newOrderItems).isNotEmpty();
                    Assertions.assertThat(newOrderItems.size()).isEqualTo(12);
                }).verifyComplete();
    }

    @Test
    void buyCartTest() {
        trxStepVerifier.create(cartService.getCart(DEFAULT_CART_ID)).
                consumeNextWith(actualCartBefore -> {
                    Assertions.assertThat(actualCartBefore.total()).isEqualTo(7808L);
                }).verifyComplete();

        trxStepVerifier.create(buyService.buy(DEFAULT_CART_ID).flatMap(orderDto ->
                        cartService.getCart(DEFAULT_CART_ID))).
                consumeNextWith(actualCartAfter -> {
                    Assertions.assertThat(actualCartAfter.total()).isEqualTo(0L);
                }).verifyComplete();
    }

    @Test
    void buyCartItemsTest() {
        trxStepVerifier.create(cartService.getCartItems(DEFAULT_CART_ID).collectList()).
                assertNext(actualCartItemsAfter -> {
                    Assertions.assertThat(actualCartItemsAfter).isNotEmpty();
                    Assertions.assertThat(actualCartItemsAfter.size()).isEqualTo(12);
                }).verifyComplete();

        trxStepVerifier.create(buyService.buy(DEFAULT_CART_ID).flatMapMany(orderDto ->
                        cartService.getCartItems(DEFAULT_CART_ID).collectList())).
                assertNext(actualCartItemsAfter -> {
                    Assertions.assertThat(actualCartItemsAfter).isEmpty();
                }).verifyComplete();
    }
}
