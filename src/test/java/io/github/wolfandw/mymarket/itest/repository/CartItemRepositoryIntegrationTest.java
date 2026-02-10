package io.github.wolfandw.mymarket.itest.repository;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционные тесты репозитория строк корзин.
 */
public class CartItemRepositoryIntegrationTest extends AbstractRepositoryIntegrationTest {
    @Test
    void findAllByCartIdTest() {
        StepVerifier.create(cartItemRepository.findAllByCartId(DEFAULT_CART_ID).collectList()).
                assertNext(cartItemsCount -> {
                    assertThat(cartItemsCount).size().isEqualTo(12);
                    assertThat(cartItemsCount.getFirst().getItemId()).isEqualTo(2L);
                    assertThat(cartItemsCount.getFirst().getCount()).isEqualTo(60);
                    assertThat(cartItemsCount.getLast().getItemId()).isEqualTo(13L);
                    assertThat(cartItemsCount.getLast().getCount()).isEqualTo(80);
                }).verifyComplete();
    }

    @Test
    void findByCartIdAndItemIdTest() {
        StepVerifier.create(cartItemRepository.findByCartIdAndItemId(DEFAULT_CART_ID, 2L)).
                consumeNextWith(cartItemsCount -> {
            assertThat(cartItemsCount.getItemId()).isEqualTo(2L);
            assertThat(cartItemsCount.getCount()).isEqualTo(60);
        }).verifyComplete();
    }

    //@Test
    void deleteAllByCartIdTest() {
        StepVerifier.create(cartItemRepository.deleteAllByCartId(DEFAULT_CART_ID)).
                expectNextCount(0).verifyComplete();
    }
}