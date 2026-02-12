package io.github.wolfandw.mymarket.test.controller;

import io.github.wolfandw.mymarket.controller.CartController;
import io.github.wolfandw.mymarket.controller.RedirectUrlFactory;
import io.github.wolfandw.mymarket.dto.CartDto;
import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.model.Cart;
import io.github.wolfandw.mymarket.model.CartItem;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    void getCartTest() {
        Cart cart = CARTS.get(DEFAULT_CART_ID);
        List<ItemDto> cartItems = mapCartItems(CART_ITEMS.get(DEFAULT_CART_ID).values().stream().toList());
        CartDto cartDto = new CartDto(DEFAULT_CART_ID, cart.getTotal().longValue());
        when(cartService.getCart(DEFAULT_CART_ID)).thenReturn(Mono.just(cartDto));
        when(cartService.getCartItems(DEFAULT_CART_ID)).thenReturn(Flux.fromIterable(cartItems));

        webTestClient.get().uri("/cart/items")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(res -> {
                    String body = res.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("Item 08"));
                    assertTrue(body.contains("7815"));
                    assertTrue(body.contains(TEMPLATE_CART));
                });
    }

    @Test
    void changeChartItemCountTest() {
        Long itemId = 1L;
        when(cartService.changeItemCount(DEFAULT_CART_ID, itemId, ACTION_PLUS)).thenReturn(Mono.empty());

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

        verify(cartService).changeItemCount(DEFAULT_CART_ID, itemId, ACTION_PLUS);
    }

    private List<ItemDto> mapCartItems(List<CartItem> cartItems) {
        return cartItems.stream().map(ci -> mapItem(ITEMS.get(ci.getItemId()),
                ci.getCount())).collect(Collectors.toList());
    }
}
