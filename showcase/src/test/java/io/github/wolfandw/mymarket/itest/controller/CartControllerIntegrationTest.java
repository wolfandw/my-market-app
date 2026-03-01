package io.github.wolfandw.mymarket.itest.controller;

import io.github.wolfandw.mymarket.controller.RedirectUrlFactory;
import io.github.wolfandw.mymarket.itest.AbstractIntegrationTest;
import io.github.wolfandw.payment.client.domain.BalanceDto;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Интеграционные тесты корзины товаров.
 */
public class CartControllerIntegrationTest extends AbstractIntegrationTest {
    private static final String TEMPLATE_CART = "cart";

    private static final String PARAMETER_ID = "id";
    private static final String PARAMETER_ACTION = "action";

    private static final String ACTION_PLUS = "PLUS";

    @Test
    void getCartTest() throws Exception {
        BalanceDto balanceDto = new BalanceDto();
        balanceDto.setId(DEFAULT_CART_ID);
        balanceDto.setAccept(true);
        balanceDto.setBalance(BigDecimal.valueOf(8000L));
        when(paymentsApi.getBalance(any(Long.class))).thenReturn(Mono.just(balanceDto));

        webTestClient.get().uri("/cart/items")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(res -> {
                    String body = res.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("Итого:"));
                    assertTrue(body.contains("Баланс: 8000 руб."));
                    assertTrue(body.contains("Купить"));
                    assertTrue(body.contains(TEMPLATE_CART));
                });
    }

    @Test
    void getCartLowBalanceTest() throws Exception {
        BalanceDto balanceDto = new BalanceDto();
        balanceDto.setId(DEFAULT_CART_ID);
        balanceDto.setAccept(true);
        balanceDto.setBalance(BigDecimal.valueOf(7000L));
        when(paymentsApi.getBalance(any(Long.class))).thenReturn(Mono.just(balanceDto));

        webTestClient.get().uri("/cart/items")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(res -> {
                    String body = res.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("Итого:"));
                    assertTrue(body.contains("\"disabled\">Купить</button>"));
                    assertTrue(body.contains("Недостаточно средств на балансе! Заказ не может быть создан!"));
                    assertTrue(body.contains(TEMPLATE_CART));
                });
    }

    @Test
    void getCartServiceErrorTest() throws Exception {
        when(paymentsApi.getBalance(any(Long.class))).thenReturn(Mono.error(new IllegalArgumentException()));

        webTestClient.get().uri("/cart/items")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(res -> {
                    String body = res.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("Итого:"));
                    assertTrue(body.contains("\"disabled\">Купить</button>"));
                    assertTrue(body.contains("Сервис платежей недоступен! Заказ не может быть создан!"));
                    assertTrue(body.contains(TEMPLATE_CART));
                });
    }

    @Test
    void changeChartItemCountTest() throws Exception {
        webTestClient.post().uri(uriBuilder -> uriBuilder
                        .path("/cart/items")
                        .queryParam(PARAMETER_ID, Long.toString(1L))
                        .queryParam(PARAMETER_ACTION, ACTION_PLUS)
                        .build())
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals(
                        "Location",
                        RedirectUrlFactory.createUrlToCart(DEFAULT_CART_ID)
                );
    }
}
