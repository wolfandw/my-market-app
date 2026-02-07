//package io.github.wolfandw.mymarket.test.service;
//
//import io.github.wolfandw.mymarket.dto.OrderDto;
//import io.github.wolfandw.mymarket.model.Order;
//import org.junit.jupiter.api.Test;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.mockito.Mockito.when;
//
///**
// * Интеграционные тесты сервиса заказов.
// */
//public class OrderServiceTest extends AbstractServiceTest {
//    @Test
//    void getOrdersTest() {
//        Long orderId = 1L;
//        when(orderRepository.findAll()).thenReturn(ORDERS.values().stream().toList());
//        Order order = ORDERS.get(orderId);
//        when(orderItemRepository.findAllByOrder(order)).thenReturn(order.getItems());
//
//        List<OrderDto> orders = orderService.getOrders();
//        assertThat(orders.size()).isEqualTo(1);
//        OrderDto actualOrder = orders.getFirst();
//
//        assertThat(actualOrder.totalSum()).isEqualTo(8120);
//        assertThat(actualOrder.items().size()).isEqualTo(12);
//        assertThat(actualOrder.items().get(0).title()).isEqualTo("Item 08");
//        assertThat(actualOrder.items().get(0).count()).isEqualTo(65);
//    }
//
//    @Test
//    void getOrderTest() {
//        Long orderId = 1L;
//        Order order = ORDERS.get(orderId);
//        when(orderRepository.findById(orderId)).thenReturn(Optional.ofNullable(order));
//        assert order != null;
//        when(orderItemRepository.findAllByOrder(order)).thenReturn(order.getItems());
//
//        Optional<OrderDto> orderDto = orderService.getOrder(orderId, false);
//        assertThat(orderDto.isPresent()).isTrue();
//        OrderDto actualOrder = orderDto.get();
//        assertThat(actualOrder.totalSum()).isEqualTo(8120);
//        assertThat(actualOrder.items().size()).isEqualTo(12);
//        assertThat(actualOrder.items().get(0).title()).isEqualTo("Item 08");
//        assertThat(actualOrder.items().get(0).count()).isEqualTo(65);
//    }
//}
