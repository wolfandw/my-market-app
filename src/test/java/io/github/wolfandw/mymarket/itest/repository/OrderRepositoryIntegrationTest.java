package io.github.wolfandw.mymarket.itest.repository;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционные тесты репозитория заказов.
 */
public class OrderRepositoryIntegrationTest extends AbstractRepositoryIntegrationTest {
    @Test
    void findAllTest() {
        StepVerifier.create(orderRepository.findAll().collectList()).
                assertNext(actualOrders -> {
                    assertThat(actualOrders).size().isEqualTo(1);
                    assertThat(actualOrders.get(0).getId()).isEqualTo(1L);
                    assertThat(actualOrders.get(0).getTotalSum()).isEqualTo(new BigDecimal(8129));
                }).verifyComplete();
    }
}