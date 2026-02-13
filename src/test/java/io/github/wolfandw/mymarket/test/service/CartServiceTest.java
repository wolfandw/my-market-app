package io.github.wolfandw.mymarket.test.service;

import io.github.wolfandw.mymarket.model.Cart;
import io.github.wolfandw.mymarket.model.CartItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Модульные тесты сервиса корзины.
 */
public class CartServiceTest extends AbstractServiceTest {
    private static final String ACTION_MINUS = "MINUS";
    private static final String ACTION_PLUS = "PLUS";

    @Test
    void getCartTest() {
        Long cartId = DEFAULT_CART_ID;

        Cart cart = CARTS.get(cartId);
        when(cartRepository.findById(cartId)).thenReturn(Mono.just(cart));

        StepVerifier.create(cartService.getCart(DEFAULT_CART_ID)).
                consumeNextWith(actualCart -> {
                    assertThat(actualCart.total()).isEqualTo(7815L);
                }).verifyComplete();
    }

    @Test
    void getCartItemsTest() {
        Long cartId = DEFAULT_CART_ID;

        Cart cart = CARTS.get(cartId);
        List<CartItem> cartItems = CART_ITEMS.get(DEFAULT_CART_ID).values().stream().toList();
        when(cartItemRepository.findAllByCartId(cartId)).thenReturn(Flux.fromIterable(cartItems));
        mockItem();

        StepVerifier.create(cartService.getCartItems(DEFAULT_CART_ID).collectList()).
                assertNext(actualCartItems -> {
                    assertThat(actualCartItems).isNotEmpty();
                    assertThat(actualCartItems.size()).isEqualTo(12);
                    assertThat(actualCartItems.getFirst().title()).isEqualTo("Item 08");
                    assertThat(actualCartItems.getFirst().count()).isEqualTo(60);
                }).verifyComplete();
    }

    @ParameterizedTest
    @MethodSource("provideChangeItemCountArgs")
    void changeItemCount(String action, long countBefore, long countAfter) {
        Long cartId = DEFAULT_CART_ID;
        Long entityId = 2L;

        when(cartRepository.save(any(Cart.class))).thenReturn(Mono.just(CARTS.get(DEFAULT_CART_ID)));
        CartItem savedCartItem = CART_ITEMS.get(DEFAULT_CART_ID).get(entityId);
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(Mono.just(savedCartItem));
        mockCart();
        mockItem();
        mockCartItem();

        StepVerifier.create(itemService.getItem(cartId, entityId)).
                consumeNextWith(entity -> {
                    assertThat(entity.count()).isEqualTo(countBefore);
                }).verifyComplete();

        trxStepVerifier.create(cartService.changeItemCount(cartId, entityId, action).then(itemService.getItem(cartId, entityId))).
                consumeNextWith(entity -> {
                    assertThat(entity.count()).isEqualTo(countAfter);
                }).verifyComplete();

        verify(cartItemRepository).save(any(CartItem.class));
    }

    private static Stream<Arguments> provideChangeItemCountArgs() {
        return Stream.of(
                Arguments.of(ACTION_MINUS, 60, 59),
                Arguments.of(ACTION_PLUS, 60, 61)
        );
    }
}
