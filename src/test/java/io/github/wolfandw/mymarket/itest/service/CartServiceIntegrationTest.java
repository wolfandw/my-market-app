package io.github.wolfandw.mymarket.itest.service;

import io.github.wolfandw.mymarket.itest.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционный тест сервиса корзин.
 */
public class CartServiceIntegrationTest extends AbstractIntegrationTest {
    private static final String ACTION_MINUS = "MINUS";
    private static final String ACTION_PLUS = "PLUS";

    @Test
    void getCartTest() {
        StepVerifier.create(cartService.getCart(DEFAULT_CART_ID)).
        consumeNextWith(actualCart -> {
            assertThat(actualCart.total()).isEqualTo(7808L);
        }).verifyComplete();
    }

    @Test
    void getCartItemsTest() {
        StepVerifier.create(cartService.getCartItems(DEFAULT_CART_ID).collectList()).
                assertNext(actualCartItems -> {
                    assertThat(actualCartItems).isNotEmpty();
                    assertThat(actualCartItems.size()).isEqualTo(12);
                    assertThat(actualCartItems.getFirst().title()).isEqualTo("Item 08");
                    assertThat(actualCartItems.getFirst().count()).isEqualTo(60);
                }).verifyComplete();
    }

    @Test
    void changeItemCountPlus() {
        Long cartId = DEFAULT_CART_ID;
        Long entityId = 2L;

        StepVerifier.create(itemService.getItem(cartId, entityId)).
        consumeNextWith(entity -> {
            assertThat(entity.count()).isEqualTo(60L);
        }).verifyComplete();

        trxStepVerifier.create(cartService.changeItemCount(cartId, entityId, ACTION_PLUS).then(itemService.getItem(cartId, entityId))).
                consumeNextWith(entity -> {
                    assertThat(entity.count()).isEqualTo(61L);
                }).verifyComplete();
    }

    @Test
    void changeItemCountMinus() {
        Long cartId = DEFAULT_CART_ID;
        Long entityId = 2L;

        StepVerifier.create(itemService.getItem(cartId, entityId)).
                consumeNextWith(entity -> {
                    assertThat(entity.count()).isEqualTo(60L);
                }).verifyComplete();

        trxStepVerifier.create(cartService.changeItemCount(cartId, entityId, ACTION_MINUS).then(itemService.getItem(cartId, entityId))).
                consumeNextWith(entity -> {
                    assertThat(entity.count()).isEqualTo(59L);
                }).verifyComplete();
    }
}
