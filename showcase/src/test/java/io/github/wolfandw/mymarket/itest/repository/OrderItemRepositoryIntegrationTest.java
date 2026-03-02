package io.github.wolfandw.mymarket.itest.repository;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционные тесты репозитория строк заказов.
 */
public class OrderItemRepositoryIntegrationTest extends AbstractRepositoryIntegrationTest {
    @Test
    void findAllByOrderIdTest() {
        StepVerifier.create(orderItemRepository.findAllByOrderId(1L).collectList()).
                assertNext(actualContent -> {
                    assertThat(actualContent).size().isEqualTo(12);
                    assertThat(actualContent.get(0).getCount()).isEqualTo(65);
                    assertThat(actualContent.get(11).getCount()).isEqualTo(85);
                }).verifyComplete();
    }
}

