package io.github.wolfandw.mymarket.test.controller;

import io.github.wolfandw.mymarket.controller.OrderController;
import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.dto.OrderDto;
import io.github.wolfandw.mymarket.model.Order;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Модульный тест контроллера заказов.
 */
@WebFluxTest(OrderController.class)
public class OrderControllerTest extends AbstractControllerTest {
    private static final String TEMPLATE_ORDERS = "orders";
    private static final String TEMPLATE_ORDER = "order";

    @Test
    void getOrdersTest() {
        List<OrderDto> orders = ORDERS.values().stream().map(this::mapOrder).toList();
        when(orderService.getOrders()).thenReturn(Flux.fromIterable(orders));
        webTestClient.get().uri("/orders")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(res -> {
                    String body = res.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("Item 08"));
                    assertTrue(body.contains("8129"));
                    assertTrue(body.contains(TEMPLATE_ORDERS));
                });
    }

    @Test
    void getOrderTest() {
        Long orderId = 1L;
        Order order = ORDERS.get(orderId);
        List<ItemDto> orderItems = mapOrderItems(ORDER_ITEMS.get(orderId).values().stream().toList());
        when( orderService.getOrder(orderId, false)).thenReturn(Mono.just(mapOrder(order)));
        when( orderService.getOrderItems(orderId)).thenReturn(Flux.fromIterable(orderItems));
        webTestClient.get().uri("/orders/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(res -> {
                    String body = res.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("Item 08"));
                    assertTrue(body.contains("8129"));
                    assertTrue(body.contains(TEMPLATE_ORDER));
                });
    }
}
