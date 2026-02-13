package io.github.wolfandw.mymarket.test.controller;

import io.github.wolfandw.mymarket.controller.ApplicationController;
import io.github.wolfandw.mymarket.controller.RedirectUrlFactory;
import io.github.wolfandw.mymarket.dto.OrderDto;
import io.github.wolfandw.mymarket.model.Cart;
import io.github.wolfandw.mymarket.model.Order;
import io.github.wolfandw.mymarket.model.OrderItem;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.Mockito.when;

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

        Cart cart = CARTS.get(DEFAULT_CART_ID);

        Order order = new Order(orderId);
        List<OrderItem> orderItems = CART_ITEMS.get(DEFAULT_CART_ID).values().stream().map(cartItem ->
                new OrderItem(order.getId(), cartItem.getItemId(), cartItem.getCount())).toList();
        order.setTotalSum(cart.getTotal());

        OrderDto orderDto = new OrderDto(orderId, mapOrderItems(orderItems), cart.getTotal().longValue());
        when(buyService.buy(DEFAULT_CART_ID)).thenReturn(Mono.just(orderDto));

        webTestClient.post().uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches(
                        "Location",
                        "\\/orders\\/\\d+\\?newOrder\\=true"
                );
    }
}
