package io.github.wolfandw.mymarket.itest.service;

import io.github.wolfandw.mymarket.MyMarketUtils;
import io.github.wolfandw.mymarket.dto.CartDto;
import io.github.wolfandw.mymarket.dto.OrderDto;
import io.github.wolfandw.mymarket.itest.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Интеграционный тест сервиса заказов.
 */
public class OrderServiceIntegrationTest extends AbstractIntegrationTest {
    @Test
    void getOrdersTest() {
        List<OrderDto> orders = orderService.getOrders();
        assertThat(orders.size()).isEqualTo(1);
        OrderDto actualOrder = orders.getFirst();
        assertThat(actualOrder.totalSum()).isEqualTo(8120);
        assertThat(actualOrder.items().size()).isEqualTo(12);
        assertThat(actualOrder.items().get(0).title()).isEqualTo("Item 08");
        assertThat(actualOrder.items().get(0).count()).isEqualTo(65);
    }

    @Test
    void getOrderTest() {
        Long orderId = 1L;
        Optional<OrderDto> orderDto = orderService.getOrder(orderId, false);
        assertThat(orderDto.isPresent()).isTrue();
        OrderDto actualOrder = orderDto.get();
        assertThat(actualOrder.totalSum()).isEqualTo(8120);
        assertThat(actualOrder.items().size()).isEqualTo(12);
        assertThat(actualOrder.items().get(0).title()).isEqualTo("Item 08");
        assertThat(actualOrder.items().get(0).count()).isEqualTo(65);
    }

    @Test
    @Transactional
    void createOrderTest() {
        CartDto cartDto = cartService.getCart(MyMarketUtils.DEFAULT_CART_ID);
        Optional<OrderDto> newOrderDto = orderService.createOrderByCart(MyMarketUtils.DEFAULT_CART_ID);
        assertThat(newOrderDto).isPresent();
        assertThat(newOrderDto.get().totalSum()).isEqualTo(cartDto.total());
        assertThat(newOrderDto.get().items().size()).isEqualTo(cartDto.items().size());
    }
}
