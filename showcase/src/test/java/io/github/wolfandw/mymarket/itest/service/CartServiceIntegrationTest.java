package io.github.wolfandw.mymarket.itest.service;

import io.github.wolfandw.mymarket.itest.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import reactor.test.StepVerifier;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционный тест сервиса корзин.
 */
public class CartServiceIntegrationTest extends AbstractIntegrationTest {
    private static final String ACTION_MINUS = "MINUS";
    private static final String ACTION_PLUS = "PLUS";

    @Test
    void getCartTest() {
        trxStepVerifier.create(cartService.getCart(DEFAULT_CART_ID)).
        consumeNextWith(actualCart -> {
            assertThat(actualCart.total()).isEqualTo(7808L);
        }).verifyComplete();
    }

    @Test
    void getCartItemsTest() {
        trxStepVerifier.create(cartService.getCartItems(DEFAULT_CART_ID).collectList()).
                assertNext(actualCartItems -> {
                    assertThat(actualCartItems).isNotEmpty();
                    assertThat(actualCartItems.size()).isEqualTo(12);
                    assertThat(actualCartItems.getFirst().title()).isEqualTo("Item 08");
                    assertThat(actualCartItems.getFirst().count()).isEqualTo(60);
                }).verifyComplete();
    }

    @ParameterizedTest
    @MethodSource("provideChangeItemCountArgs")
    void changeItemCount(String action, long countBefore, long countAfter) {
        Long cartId = DEFAULT_CART_ID;
        Long entityId = 2L;

        StepVerifier.create(itemService.getItem(cartId, entityId)).
        consumeNextWith(entity -> {
            assertThat(entity.count()).isEqualTo(countBefore);
        }).verifyComplete();

        trxStepVerifier.create(cartService.changeItemCount(cartId, entityId, action).then(itemService.getItem(cartId, entityId))).
                consumeNextWith(entity -> {
                    assertThat(entity.count()).isEqualTo(countAfter);
                }).verifyComplete();
    }

    private static Stream<Arguments> provideChangeItemCountArgs() {
        return Stream.of(
                Arguments.of(ACTION_MINUS, 60, 59),
                Arguments.of(ACTION_PLUS, 60, 61)
        );
    }
}
