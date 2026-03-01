package io.github.wolfandw.mymarket.itest.controller;

import io.github.wolfandw.mymarket.controller.RedirectUrlFactory;
import io.github.wolfandw.mymarket.itest.AbstractIntegrationTest;
import io.github.wolfandw.payment.client.domain.BalanceDto;
import io.github.wolfandw.payment.client.domain.PaymentDto;
import io.github.wolfandw.payment.client.domain.ReceiptDto;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.web.reactive.function.BodyInserters.fromFormData;

/**
 * Интеграционные тесты контроллера приложения.
 */
public class ZApplicationControllerIntegrationTest extends AbstractIntegrationTest {
    @Test
    void redirectToItemsTest() throws Exception {
        webTestClient.get().uri("/")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals(
                        "Location",
                        RedirectUrlFactory.createUrlToItems()
                );
    }

    @Test
    void buyTest() throws Exception {
        BalanceDto balanceDto = new BalanceDto();
        balanceDto.setId(DEFAULT_CART_ID);
        balanceDto.setAccept(true);
        balanceDto.setBalance(BigDecimal.valueOf(0L));
        when(paymentsApi.makePayment(any(Long.class), any(PaymentDto.class))).thenReturn(Mono.just(balanceDto));

        webTestClient.post().uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches(
                        "Location",
                "\\/orders\\/\\d+\\?newOrder\\=true"
                );
    }

    @Test
    void buyLowBalanceTest() throws Exception {
        BalanceDto balanceDto = new BalanceDto();
        balanceDto.setId(DEFAULT_CART_ID);
        balanceDto.setAccept(false);
        balanceDto.setBalance(BigDecimal.valueOf(0L));
        when(paymentsApi.makePayment(any(Long.class), any(PaymentDto.class))).thenReturn(Mono.just(balanceDto));

        webTestClient.post().uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches(
                        "Location",
                        "/cart/items"
                );
    }

    @Test
    void buyServiceErrorTest() throws Exception {
        when(paymentsApi.makePayment(any(Long.class), any(PaymentDto.class))).thenReturn(Mono.error(new IllegalArgumentException()));

        webTestClient.post().uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches(
                        "Location",
                        "/cart/items"
                );
    }

    @Test
    void topUpBalanceTest() throws Exception {
        Long id = 1L;
        BalanceDto mockBalanceDto = new BalanceDto();
        mockBalanceDto.setId(id);
        mockBalanceDto.setAccept(true);
        mockBalanceDto.setBalance(BigDecimal.TEN);

        when(paymentsApi.topUpBalance(any(Long.class), any(ReceiptDto.class))).thenReturn(Mono.just(mockBalanceDto));

        webTestClient.post().uri("/topUpBalance")
                .body(fromFormData("id", "1").
                        with("receipt", "1000.01"))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches(
                        "Location",
                        "/cart/items"
                );
    }

    @Test
    void topUpBalanceServiceErrorTest() throws Exception {
        when(paymentsApi.topUpBalance(any(Long.class), any(ReceiptDto.class))).thenReturn(Mono.error(new IllegalArgumentException()));

        webTestClient.post().uri("/topUpBalance")
                .body(fromFormData("id", "1").
                        with("receipt", "1000.01"))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches(
                        "Location",
                        "/cart/items"
                );
    }
}
