package io.github.wolfandw.mymarket.itest.controller;

import io.github.wolfandw.mymarket.IsRoleUser;
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
    @IsRoleUser
    public void getOrdersUserTest() {
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
        webTestClient.get().uri("/orders")
                .exchange()
                .expectStatus().isFound()
                .expectHeader().valueMatches(
                        "Location",
                        "/login"
                );
    }

    @Test
    @IsRoleUser
    public void getOrderUserTest() {
        webTestClient.get().uri(uriBuilder -> uriBuilder
                .path("/orders/1")
                .build())
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches(
                        "Location",
                        "/orders"
                );
    }

    @Test
    public void getOrderTest() {
        webTestClient.get().uri("/orders/1")
                .exchange()
                .expectStatus().isFound()
                .expectHeader().valueMatches(
                        "Location",
                        "/login"
                );
    }
}
