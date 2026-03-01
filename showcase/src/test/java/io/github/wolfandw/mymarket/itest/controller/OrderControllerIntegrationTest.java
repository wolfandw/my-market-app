package io.github.wolfandw.mymarket.itest.controller;

import io.github.wolfandw.mymarket.itest.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Интеграционные тесты контроллера заказов.
 */
public class OrderControllerIntegrationTest extends AbstractIntegrationTest {
    private static final String TEMPLATE_ORDERS = "orders";
    private static final String TEMPLATE_ORDER = "order";

    @Test
    void getOrdersTest() throws Exception {
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
    void getOrderTest() throws Exception {
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
