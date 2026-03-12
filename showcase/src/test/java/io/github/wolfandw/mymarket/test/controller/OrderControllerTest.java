package io.github.wolfandw.mymarket.test.controller;

import io.github.wolfandw.mymarket.IsRoleUser;
import io.github.wolfandw.mymarket.controller.OrderController;
import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.dto.OrderDto;
import io.github.wolfandw.mymarket.model.Order;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Модульный тест контроллера заказов.
 */
@WebFluxTest(OrderController.class)
public class OrderControllerTest extends AbstractControllerTest {
    private static final String TEMPLATE_ORDERS = "orders";
    private static final String TEMPLATE_ORDER = "order";

    @Test
    @IsRoleUser
    public void getOrdersUserTest() {
        List<OrderDto> orders = ORDERS.values().stream().map(this::mapOrder).toList();
        when(orderService.getUserOrders()).thenReturn(Flux.fromIterable(orders));
        when(userService.getCurrentUserInfo()).thenReturn(getUserInfoMono());
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
    public void getOrdersTest() {
        checkFound("/orders");
    }

    @Test
    @IsRoleUser
    public void getOrderUserTest() {
        Long orderId = 1L;
        Order order = ORDERS.get(orderId);
        List<ItemDto> orderItems = mapOrderItems(ORDER_ITEMS.get(orderId).values().stream().toList());
        when( orderService.getUserOrder(orderId, false)).thenReturn(Mono.just(mapOrder(order)));
        when( orderService.getUserOrderItems(orderId)).thenReturn(Flux.fromIterable(orderItems));
        when(userService.getCurrentUserInfo()).thenReturn(getGuestInfoMono());
        webTestClient.get().uri("/orders/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(res -> {
                    String body = res.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("Для незарегистрированных пользователей функционал ограничен! Заказ не может быть просмотрен!"));
                });
    }

    @Test
    public void getOrderGuestTest() {
        checkFound("/orders/1");
    }
}
