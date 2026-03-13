package io.github.wolfandw.mymarket.test.controller;

import io.github.wolfandw.mymarket.IsRoleUser;
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
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.web.reactive.function.BodyInserters.fromFormData;

/**
 * Модульный тест контроллера приложения.
 */
@WebFluxTest(ApplicationController.class)
public class ApplicationControllerTest extends AbstractControllerTest {
    @Test
    @IsRoleUser
    public void redirectToItemsUserTest() throws Exception {
        when(userService.getCurrentUserInfo()).thenReturn(getGuestInfoMono());
        webTestClient.get().uri("/")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals(
                        "Location",
                        RedirectUrlFactory.createUrlToItems()
                );
    }

    @Test
    public void redirectToItemsTest() {
        checkFound("/");
    }

    @Test
    @IsRoleUser
    void buyUserTest() throws Exception {
        when(userService.getCurrentUserInfo()).thenReturn(getUserInfoMono());
        Cart cart = CARTS.get(getUser().getId());

        Order order = new Order();
        order.setId(getUser().getId());
        order.setUserId(getUser().getId());
        List<OrderItem> orderItems = CART_ITEMS.get(cart.getId()).values().stream().map(cartItem ->
                new OrderItem(order.getId(), cartItem.getItemId(), cartItem.getCount())).toList();
        order.setTotalSum(cart.getTotal());

        OrderDto orderDto = new OrderDto(order.getId(), mapOrderItems(orderItems), cart.getTotal().longValue());
        when(buyService.buy()).thenReturn(Mono.just(orderDto));

        webTestClient.mutateWith(csrf())
                .post().uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches(
                        "Location",
                        "\\/orders\\/\\d+\\?newOrder\\=true"
                );
    }

    @Test
    public void buyTest() {
        checkFound("/buy");
    }

    @Test
    @IsRoleUser
    void buyLowBalanceOrServiceErrorTest() throws Exception {
        when(userService.getCurrentUserInfo()).thenReturn(getUserInfoMono());
        when(buyService.buy()).thenReturn(Mono.empty());

        webTestClient.mutateWith(csrf())
                .post().uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches(
                        "Location",
                        "/cart/items"
                );
    }

    @Test
    @IsRoleUser
    void topUpBalanceUserTest() throws Exception {
        when(userService.getCurrentUserInfo()).thenReturn(getUserInfoMono());
        Long id = 1L;
        BalanceDto mockBalanceDto = new BalanceDto();
        mockBalanceDto.setId(id);
        mockBalanceDto.setAccept(true);
        mockBalanceDto.setBalance(BigDecimal.TEN);

        when(paymentsService.topUpUserBalance(any(ReceiptDto.class))).thenReturn(Mono.just(mockBalanceDto));

        webTestClient.mutateWith(csrf())
                .post().uri("/topUpBalance")
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
    public void topUpBalanceTest() {
        checkFound("/topUpBalance");
    }

    @Test
    @IsRoleUser
    void topUpBalanceLowBalanceOrServiceErrorTest() throws Exception {
        when(userService.getCurrentUserInfo()).thenReturn(getUserInfoMono());
        when(paymentsService.topUpUserBalance(any(ReceiptDto.class))).thenReturn(Mono.empty());

        webTestClient.mutateWith(csrf())
                .post().uri("/topUpBalance")
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
