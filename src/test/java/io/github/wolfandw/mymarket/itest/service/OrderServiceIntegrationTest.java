package io.github.wolfandw.mymarket.itest.service;

import io.github.wolfandw.mymarket.dto.OrderDto;
import io.github.wolfandw.mymarket.itest.AbstractIntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Интеграционный тест сервиса заказов.
 */
public class OrderServiceIntegrationTest extends AbstractIntegrationTest {
    @Test
    void getOrdersTest() {
        StepVerifier.create(orderService.getOrders().collectList()).
                consumeNextWith(orders -> {
                    assertThat(orders.size()).isEqualTo(1);
                    OrderDto actualOrder = orders.getFirst();
                    assertThat(actualOrder.totalSum()).isEqualTo(8129L);
                    assertThat(actualOrder.items().size()).isEqualTo(12);
                }).verifyComplete();
    }
    @Test
    void getOrderItemsTest() {
        Long orderId = 1L;
        StepVerifier.create(orderService.getOrderItems(orderId).collectList()).
                assertNext(actualOrderItems -> {
                    Assertions.assertThat(actualOrderItems).isNotEmpty();
                    Assertions.assertThat(actualOrderItems.size()).isEqualTo(12);
                    Assertions.assertThat(actualOrderItems.get(0).title()).isEqualTo("Item 08");
                    Assertions.assertThat(actualOrderItems.get(0).count()).isEqualTo(65);
                }).verifyComplete();
    }

    @Test
    void getOrderTest() {
        Long orderId = 1L;
        StepVerifier.create(orderService.getOrder(orderId, false)).
                consumeNextWith(orderDto -> {
            assertThat(orderDto.totalSum()).isEqualTo(8129L);
        }).verifyComplete();
    }
}
