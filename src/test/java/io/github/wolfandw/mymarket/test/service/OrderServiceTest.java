package io.github.wolfandw.mymarket.test.service;

import io.github.wolfandw.mymarket.dto.DtoConstants;
import io.github.wolfandw.mymarket.dto.OrderDto;
import io.github.wolfandw.mymarket.model.Cart;
import io.github.wolfandw.mymarket.model.Order;
import io.github.wolfandw.mymarket.model.OrderItem;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

/**
 * Интеграционные тесты сервиса заказов.
 */
public class OrderServiceTest extends AbstractServiceTest {
    @Test
    void getOrdersTest() {
        Long orderId = 1L;
        when(orderRepository.findAll()).thenReturn(ORDERS.values().stream().toList());
        Order order = ORDERS.get(orderId);
        when(orderItemRepository.findAllByOrder(order)).thenReturn(order.getItems());

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
        Order order = ORDERS.get(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.ofNullable(order));
        assert order != null;
        when(orderItemRepository.findAllByOrder(order)).thenReturn(order.getItems());

        Optional<OrderDto> orderDto = orderService.getOrder(orderId, false);
        assertThat(orderDto.isPresent()).isTrue();
        OrderDto actualOrder = orderDto.get();
        assertThat(actualOrder.totalSum()).isEqualTo(8120);
        assertThat(actualOrder.items().size()).isEqualTo(12);
        assertThat(actualOrder.items().get(0).title()).isEqualTo("Item 08");
        assertThat(actualOrder.items().get(0).count()).isEqualTo(65);
    }

    @Test
    void createOrderTest() {
        Long cartId = DtoConstants.DEFAULT_CART_ID;
        Cart cart = CARTS.get(cartId);
        assert cart != null;
        when(cartRepository.findById(cartId)).thenReturn(Optional.ofNullable(cart));

        Long orderId = 2L;
        Order order = new Order(orderId);
        order.setTotalSum(cart.getTotal());
        order.setItems(cart.getItems().stream().map(cartItem -> new OrderItem(cartItem.getId(), order,
                cartItem.getItem(), cartItem.getCount())).toList());
        when(orderRepository.save(Mockito.any(Order.class))).thenReturn(order);

        Optional<OrderDto> newOrderDto = orderService.createOrderByCart(cartId);
        assertThat(newOrderDto).isNotNull();
        assertThat(newOrderDto.get().totalSum()).isEqualTo(cart.getTotal().longValue());
        assertThat(newOrderDto.get().items().size()).isEqualTo(cart.getItems().size());
    }
}
