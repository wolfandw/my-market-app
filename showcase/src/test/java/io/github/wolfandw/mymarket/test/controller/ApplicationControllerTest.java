package io.github.wolfandw.mymarket.test.controller;

import io.github.wolfandw.mymarket.controller.ApplicationController;
import io.github.wolfandw.mymarket.controller.RedirectUrlFactory;
import io.github.wolfandw.mymarket.dto.OrderDto;
import io.github.wolfandw.mymarket.model.Cart;
import io.github.wolfandw.mymarket.model.Order;
import io.github.wolfandw.mymarket.model.OrderItem;
import io.github.wolfandw.payment.client.domain.BalanceDto;
import io.github.wolfandw.payment.client.domain.ReceiptDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.web.reactive.function.BodyInserters.fromFormData;

/**
 * Модульный тест контроллера приложения.
 */
@WebFluxTest(ApplicationController.class)
public class ApplicationControllerTest extends AbstractControllerTest {
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
        Long orderId = 2L;

        Cart cart = CARTS.get(DEFAULT_USER_ID);

        Order order = new Order();
        order.setId(orderId);
        order.setUserId(DEFAULT_USER_ID);
        List<OrderItem> orderItems = CART_ITEMS.get(DEFAULT_USER_ID).values().stream().map(cartItem ->
                new OrderItem(order.getId(), cartItem.getItemId(), cartItem.getCount())).toList();
        order.setTotalSum(cart.getTotal());

        OrderDto orderDto = new OrderDto(orderId, mapOrderItems(orderItems), cart.getTotal().longValue());
        when(buyService.buy()).thenReturn(Mono.just(orderDto));

        webTestClient.post().uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches(
                        "Location",
                        "\\/orders\\/\\d+\\?newOrder\\=true"
                );
    }

    @Test
    void buyLowBalanceOrServiceErrorTest() throws Exception {
        when(buyService.buy()).thenReturn(Mono.empty());

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

        when(paymentsService.topUpBalance(any(Long.class), any(ReceiptDto.class))).thenReturn(Mono.just(mockBalanceDto));

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
    void topUpBalanceLowBalanceOrServiceErrorTest() throws Exception {
        when(paymentsService.topUpBalance(any(Long.class), any(ReceiptDto.class))).thenReturn(Mono.empty());

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
