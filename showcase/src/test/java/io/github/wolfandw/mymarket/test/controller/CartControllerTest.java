package io.github.wolfandw.mymarket.test.controller;

import io.github.wolfandw.mymarket.controller.CartController;
import io.github.wolfandw.mymarket.controller.RedirectUrlFactory;
import io.github.wolfandw.mymarket.dto.CartDto;
import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.model.Cart;
import io.github.wolfandw.mymarket.model.CartItem;
import io.github.wolfandw.payment.client.domain.BalanceDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.security.test.context.support.WithMockUser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

/**
 * Модульный тест контроллера корзин.
 */
@WebFluxTest(CartController.class)
public class CartControllerTest extends AbstractControllerTest{
    private static final String TEMPLATE_CART = "cart";

    private static final String PARAMETER_ID = "id";
    private static final String PARAMETER_ACTION = "action";

    private static final String ACTION_PLUS = "PLUS";

    @Test
    @WithMockUser(roles = "ANONYMOUS")
    void getCartTest() {
        when(userService.getCurrentUserInfo()).thenReturn(getGuestInfo());
        Cart cart = CARTS.get(DEFAULT_USER_ID);
        List<ItemDto> cartItems = mapCartItems(CART_ITEMS.get(DEFAULT_USER_ID).values().stream().toList());
        CartDto cartDto = new CartDto(cart.getId(), cart.getUserId(), cart.getTotal().longValue());
        BalanceDto balanceDto = new BalanceDto();
        balanceDto.setId(DEFAULT_USER_ID);
        balanceDto.setAccept(true);
        balanceDto.setBalance(BigDecimal.valueOf(8000L));
        when(cartService.getUserCart()).thenReturn(Mono.just(cartDto));
        when(cartService.getUserCartItems()).thenReturn(Flux.fromIterable(cartItems));
        when(paymentsService.getUserBalance()).thenReturn(Mono.just(balanceDto));

        webTestClient.get().uri("/cart/items")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(res -> {
                    String body = res.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("Для незарегистрированных пользователей функционал ограничен! Корзина не может быть наполнена!"));
                });
    }

    @Test
    @WithMockUser(roles = "USER")
    void getCartUserTest() {
        when(userService.getCurrentUserInfo()).thenReturn(getUserInfo());
        Cart cart = CARTS.get(DEFAULT_USER_ID);
        List<ItemDto> cartItems = mapCartItems(CART_ITEMS.get(DEFAULT_USER_ID).values().stream().toList());
        CartDto cartDto = new CartDto(cart.getId(), cart.getUserId(), cart.getTotal().longValue());
        BalanceDto balanceDto = new BalanceDto();
        balanceDto.setId(DEFAULT_USER_ID);
        balanceDto.setAccept(true);
        balanceDto.setBalance(BigDecimal.valueOf(8000L));
        when(cartService.getUserCart()).thenReturn(Mono.just(cartDto));
        when(cartService.getUserCartItems()).thenReturn(Flux.fromIterable(cartItems));
        when(paymentsService.getUserBalance()).thenReturn(Mono.just(balanceDto));

        webTestClient.get().uri("/cart/items")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(res -> {
                    String body = res.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("Item 08"));
                    assertTrue(body.contains("7815"));
                    assertTrue(body.contains("8000"));
                    assertTrue(body.contains("Купить"));
                    assertTrue(body.contains(TEMPLATE_CART));
                });
    }

    @Test
    @WithMockUser(roles = "USER")
    void getCartLowBalanceTest() {
        when(userService.getCurrentUserInfo()).thenReturn(getUserInfo());
        Cart cart = CARTS.get(DEFAULT_USER_ID);
        List<ItemDto> cartItems = mapCartItems(CART_ITEMS.get(DEFAULT_USER_ID).values().stream().toList());
        CartDto cartDto = new CartDto(cart.getId(), cart.getUserId(), cart.getTotal().longValue());
        BalanceDto balanceDto = new BalanceDto();
        balanceDto.setId(DEFAULT_USER_ID);
        balanceDto.setAccept(true);
        balanceDto.setBalance(BigDecimal.valueOf(7000L));
        when(cartService.getUserCart()).thenReturn(Mono.just(cartDto));
        when(cartService.getUserCartItems()).thenReturn(Flux.fromIterable(cartItems));
        when(paymentsService.getUserBalance()).thenReturn(Mono.just(balanceDto));

        webTestClient.get().uri("/cart/items")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(res -> {
                    String body = res.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("Item 08"));
                    assertTrue(body.contains("7815"));
                    assertTrue(body.contains("7000"));
                    assertTrue(body.contains("Купить"));
                    assertTrue(body.contains("Недостаточно средств на балансе! Заказ не может быть создан!"));
                    assertTrue(body.contains(TEMPLATE_CART));
                });
    }

    @Test
    @WithMockUser(roles = "USER")
    void getCartPaymentsServiceErrorTest() {
        when(userService.getCurrentUserInfo()).thenReturn(getUserInfo());
        Cart cart = CARTS.get(DEFAULT_USER_ID);
        List<ItemDto> cartItems = mapCartItems(CART_ITEMS.get(DEFAULT_USER_ID).values().stream().toList());
        CartDto cartDto = new CartDto(cart.getId(), cart.getUserId(), cart.getTotal().longValue());
        when(cartService.getUserCart()).thenReturn(Mono.just(cartDto));
        when(cartService.getUserCartItems()).thenReturn(Flux.fromIterable(cartItems));
        when(paymentsService.getUserBalance()).thenReturn(Mono.empty());

        webTestClient.get().uri("/cart/items")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(res -> {
                    String body = res.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("Item 08"));
                    assertTrue(body.contains("7815"));
                    assertTrue(body.contains("Купить"));
                    assertTrue(body.contains("Сервис платежей недоступен! Заказ не может быть создан!"));
                    assertTrue(body.contains(TEMPLATE_CART));
                });
    }

    @Test
    @WithMockUser(roles = "ANONYMOUS")
    void changeChartItemCountGuestTest() {
        when(userService.getCurrentUserInfo()).thenReturn(getGuestInfo());
        Long itemId = 1L;
        when(cartService.changeUserItemCount(itemId, ACTION_PLUS)).thenReturn(Mono.empty());

        webTestClient.mutateWith(csrf())
                .post().uri(uriBuilder -> uriBuilder
                        .path("/cart/items")
                        .queryParam(PARAMETER_ID, Long.toString(1L))
                        .queryParam(PARAMETER_ACTION, ACTION_PLUS)
                        .build())
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals(
                        "Location",
                        RedirectUrlFactory.createUrlToUserCart()
                );

        verify(cartService).changeUserItemCount(itemId, ACTION_PLUS);
    }

    @Test
    @WithMockUser(roles = "USER")
    void changeChartItemCountUserTest() {
        when(userService.getCurrentUserInfo()).thenReturn(getUserInfo());
        Long itemId = 1L;
        when(cartService.changeUserItemCount(itemId, ACTION_PLUS)).thenReturn(Mono.empty());

        webTestClient.mutateWith(csrf())
                .post().uri(uriBuilder -> uriBuilder
                        .path("/cart/items")
                        .queryParam(PARAMETER_ID, Long.toString(1L))
                        .queryParam(PARAMETER_ACTION, ACTION_PLUS)
                        .build())
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals(
                        "Location",
                        RedirectUrlFactory.createUrlToUserCart()
                );

        verify(cartService).changeUserItemCount(itemId, ACTION_PLUS);
    }

    private List<ItemDto> mapCartItems(List<CartItem> cartItems) {
        return cartItems.stream().map(ci -> mapItem(ITEMS.get(ci.getItemId()),
                ci.getCount())).collect(Collectors.toList());
    }
}
