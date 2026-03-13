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
                    assertThat(actualOrders).size().isEqualTo(2);
                    assertThat(actualOrders.get(0).getId()).isEqualTo(1L);
                    assertThat(actualOrders.get(0).getTotalSum()).isEqualTo(new BigDecimal(8129));
                }).verifyComplete();
    }

    @Test
    void findAllByUserIdTest() {
        StepVerifier.create(orderRepository.findAllByUserId(1L).collectList()).
                assertNext(actualOrders -> {
                    assertThat(actualOrders).size().isEqualTo(1);
                    assertThat(actualOrders.get(0).getId()).isEqualTo(1L);
                    assertThat(actualOrders.get(0).getTotalSum()).isEqualTo(new BigDecimal(8129));
                }).verifyComplete();
    }

    @Test
    void findByIdAndUserIdTest() {
        StepVerifier.create(orderRepository.findByIdAndUserId(1L, 1L)).
                assertNext(actualOrder -> {
                    assertThat(actualOrder.getId()).isEqualTo(1L);
                    assertThat(actualOrder.getTotalSum()).isEqualTo(new BigDecimal(8129));
                }).verifyComplete();
    }
}