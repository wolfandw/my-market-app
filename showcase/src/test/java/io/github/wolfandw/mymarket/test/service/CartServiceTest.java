package io.github.wolfandw.mymarket.test.service;

import io.github.wolfandw.mymarket.model.Cart;
import io.github.wolfandw.mymarket.model.CartItem;
import io.github.wolfandw.mymarket.service.UserService;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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

    @MockitoBean(reset = MockReset.BEFORE)
    private UserService mockUserService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getCartTest() {
        Long cartId = 1L;

        Cart cart = CARTS.get(cartId);
        when(cartRepository.findById(cartId)).thenReturn(Mono.just(cart));

        StepVerifier.create(cartService.getCart(DEFAULT_USER_ID)).
                consumeNextWith(actualCart -> {
                    assertThat(actualCart.total()).isEqualTo(7815L);
                }).verifyComplete();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUserCartTest() {
        Long userId = 1L;

        Cart cart = CARTS.get(userId);
        when(cartRepository.findFirstByUserId(userId)).thenReturn(Mono.just(cart));
        when(mockUserService.getCurrentUserId()).thenReturn(Mono.just(userId));

        StepVerifier.create(cartService.getUserCart()).
                consumeNextWith(actualCart -> {
                    assertThat(actualCart.total()).isEqualTo(7815L);
                }).verifyComplete();
    }

    @Test
    void getUserCartIsUnauthorizedTest() {
        Long userId = 1L;

        Cart cart = CARTS.get(userId);
        when(cartRepository.findFirstByUserId(userId)).thenReturn(Mono.just(cart));
        when(mockUserService.getCurrentUserId()).thenReturn(Mono.just(userId));

        StepVerifier.create(cartService.getUserCart()).verifyError(AuthorizationDeniedException.class);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @WithMockUser(roles = "ADMIN")
    void getCartItemsTest(boolean emptyCache) {
        Long cartId = 1L;

        Cart cart = CARTS.get(cartId);
        List<CartItem> cartItems = CART_ITEMS.get(DEFAULT_USER_ID).values().stream().toList();
        when(cartItemRepository.findAllByCartId(cartId)).thenReturn(Flux.fromIterable(cartItems));
        when(cartRepository.findById(cartId)).thenReturn(Mono.just(cart));

        mockItem();
        if (emptyCache) {
            when(itemCache.getItem(any(Long.class))).thenReturn(Mono.empty());
        }
        else {
            mockGetItemFromCache();
        }
        mockCacheItemFromCache();

        StepVerifier.create(cartService.getCartItems(DEFAULT_USER_ID).collectList()).
                assertNext(actualCartItems -> {
                    assertThat(actualCartItems).isNotEmpty();
                    assertThat(actualCartItems.size()).isEqualTo(12);
                    assertThat(actualCartItems.getFirst().title()).isEqualTo("Item 08");
                    assertThat(actualCartItems.getFirst().count()).isEqualTo(60);
                }).verifyComplete();

        verify(itemCache, times(emptyCache ? 12 : 0)).cache(any());
    }

    private @NonNull Long prepareChangeItemCountTest(long countBefore, boolean emptyCache) {
        Long entityId = 2L;

        when(cartRepository.save(any(Cart.class))).thenReturn(Mono.just(CARTS.get(1L)));
        CartItem savedCartItem = CART_ITEMS.get(1L).get(entityId);
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(Mono.just(savedCartItem));
        when(mockUserService.getCurrentUserId()).thenReturn(Mono.just(1L));
        when(cartRepository.findFirstByUserId(1L)).thenReturn(Mono.just(CARTS.get(1L)));

        mockCart();
        mockItem();
        mockCartItem();
        if (emptyCache) {
            when(itemCache.getItem(any(Long.class))).thenReturn(Mono.empty());
        }
        else {
            mockGetItemFromCache();
        }
        mockCacheItemFromCache();

        StepVerifier.create(itemService.getItem(entityId)).
                consumeNextWith(entity -> {
                    assertThat(entity.count()).isEqualTo(countBefore);
                }).verifyComplete();
        return entityId;
    }

    @ParameterizedTest
    @MethodSource("provideChangeItemCountArgs")
    @WithMockUser(roles = "ADMIN")
    void changeItemCount(String action, long countBefore, long countAfter, boolean emptyCache) {
        Long entityId = prepareChangeItemCountTest(countBefore, emptyCache);

        trxStepVerifier.create(cartService.changeItemCount(1L, entityId, action).then(itemService.getItem(entityId))).
                consumeNextWith(entity -> {
                    assertThat(entity.count()).isEqualTo(countAfter);
                }).verifyComplete();

        verify(cartItemRepository).save(any(CartItem.class));
        verify(itemCache, times(emptyCache ? 2 : 0)).cache(any());
    }

    @ParameterizedTest
    @MethodSource("provideChangeItemCountArgs")
    @WithMockUser(roles = "USER")
    void changeUserItemCount(String action, long countBefore, long countAfter, boolean emptyCache) {
        Long entityId = prepareChangeItemCountTest(countBefore, emptyCache);

        trxStepVerifier.create(cartService.changeUserItemCount(entityId, action).then(itemService.getItem(entityId))).
                consumeNextWith(entity -> {
                    assertThat(entity.count()).isEqualTo(countAfter);
                }).verifyComplete();

        verify(cartItemRepository).save(any(CartItem.class));
        verify(itemCache, times(emptyCache ? 2 : 0)).cache(any());
    }

    @ParameterizedTest
    @MethodSource("provideChangeItemCountArgs")
    void changeUserItemCountIsUnauthorizedTest(String action, long countBefore, long countAfter, boolean emptyCache) {
        Long entityId = prepareChangeItemCountTest(countBefore, emptyCache);

        trxStepVerifier.create(cartService.changeUserItemCount(entityId, action).
                then(itemService.getItem(entityId))).verifyError(AuthorizationDeniedException.class);
    }

    private static Stream<Arguments> provideChangeItemCountArgs() {
        return Stream.of(
                Arguments.of(ACTION_MINUS, 60, 59, true),
                Arguments.of(ACTION_PLUS, 60, 61, true),
                Arguments.of(ACTION_MINUS, 60, 59, false),
                Arguments.of(ACTION_PLUS, 60, 61, false)
        );
    }
}
