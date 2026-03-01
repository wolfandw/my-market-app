package io.github.wolfandw.mymarket.itest.repository;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционные тесты репозитория корзин.
 */
public class CartRepositoryIntegrationTest extends AbstractRepositoryIntegrationTest {
    @Test
    void findAllTest() {
        StepVerifier.create(cartRepository.findAll().collectList()).
                assertNext(actualCarts -> {
                    assertThat(actualCarts).size().isEqualTo(1);
                    assertThat(actualCarts.get(0).getId()).isEqualTo(1L);
                    assertThat(actualCarts.get(0).getTotal().longValue()).isEqualTo(new BigDecimal(7808.4).longValue());
                }).verifyComplete();
    }
}