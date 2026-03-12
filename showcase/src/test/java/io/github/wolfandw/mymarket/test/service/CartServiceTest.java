package io.github.wolfandw.mymarket.test.service;

import io.github.wolfandw.mymarket.IsRoleAdmin;
import io.github.wolfandw.mymarket.IsRoleGuest;
import io.github.wolfandw.mymarket.IsRoleUser;
import io.github.wolfandw.mymarket.dto.CartDto;
import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.model.Cart;
import io.github.wolfandw.mymarket.model.CartItem;
import io.github.wolfandw.mymarket.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.authorization.AuthorizationDeniedException;
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
    @IsRoleUser
    void getCartUserTest() {
        StepVerifier.create(cartService.getCart(getUser().getId())).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @IsRoleAdmin
    public void getCartAdminTest() {
        getCartTest(getAdmin(), getAdminMono(), cartService.getCart(getAdmin().getId()));
    }

    @Test
    @IsRoleGuest
    public void getCartGuestTest() {
        StepVerifier.create(cartService.getCart(ID_GUEST)).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @IsRoleUser
    void getUserCartUserTest() {
        getCartTest(getUser(), getUserMono(), cartService.getUserCart());
    }

//    @Test
//    @WithMockUserAdmin
//    public void getUserCartAdminTest() {
//        getCartTest(getAdmin(), getAdminMono(), cartService.getUserCart());
//    }

    @Test
    @IsRoleGuest
    public void getUserCartGuestTest() {
        StepVerifier.create(cartService.getUserCart()).verifyError(AuthorizationDeniedException.class);
    }

    private void getCartTest(User testUser, Mono<User> testUserMono, Mono<CartDto> testCart) {
        Cart cart = CARTS.get(testUser.getId());
        when(cartRepository.findFirstByUserId(testUser.getId())).thenReturn(Mono.just(cart));
        when(cartRepository.findById(testUser.getId())).thenReturn(Mono.just(cart));
        when(userRepository.findByUsername(any(String.class))).thenReturn(testUserMono);

        StepVerifier.create(testCart).
                consumeNextWith(actualCart -> {
                    assertThat(actualCart.total()).isEqualTo(7815L);
                }).verifyComplete();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @IsRoleUser
    public void getCartItemsUserTest(boolean emptyCache) {
        StepVerifier.create(cartService.getCartItems(ID_GUEST).collectList()).verifyError(AuthorizationDeniedException.class);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @IsRoleAdmin
    public void getCartItemsAdminTest(boolean emptyCache) {
        getCartItemsTest(emptyCache, getAdmin(), getAdminMono(), cartService.getCartItems(getAdmin().getId()));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @IsRoleGuest
    public void getCartItemsGuestTest(boolean emptyCache) {
        StepVerifier.create(cartService.getCartItems(ID_GUEST).collectList()).verifyError(AuthorizationDeniedException.class);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @IsRoleUser
    public void getUserCartItemsUserTest(boolean emptyCache) {
        getCartItemsTest(emptyCache, getUser(), getUserMono(), cartService.getUserCartItems());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @IsRoleGuest
    public void getUserCartItemsGuestTest(boolean emptyCache) {
        StepVerifier.create(cartService.getUserCartItems().collectList()).verifyError(AuthorizationDeniedException.class);
    }

    private void getCartItemsTest(boolean emptyCache, User testUser, Mono<User> testUserMono, Flux<ItemDto> testCartItems) {
        Cart cart = CARTS.get(testUser.getId());
        List<CartItem> cartItems = CART_ITEMS.get(cart.getId()).values().stream().toList();
        when(cartItemRepository.findAllByCartId(cart.getId())).thenReturn(Flux.fromIterable(cartItems));
        when(cartRepository.findById(cart.getId())).thenReturn(Mono.just(cart));
        when(userRepository.findByUsername(any(String.class))).thenReturn(testUserMono);
        when(cartRepository.findFirstByUserId(testUser.getId())).thenReturn(Mono.just(cart));

        mockItem();
        if (emptyCache) {
            when(itemCache.getItem(any(Long.class))).thenReturn(Mono.empty());
        }
        else {
            mockGetItemFromCache();
        }
        mockCacheItemFromCache();

        StepVerifier.create(testCartItems.collectList()).
                assertNext(actualCartItems -> {
                    assertThat(actualCartItems).isNotEmpty();
                    assertThat(actualCartItems.size()).isEqualTo(12);
                    assertThat(actualCartItems.getFirst().title()).isEqualTo("Item 08");
                    assertThat(actualCartItems.getFirst().count()).isEqualTo(60);
                }).verifyComplete();

        verify(itemCache, times(emptyCache ? 12 : 0)).cache(any());
    }

    @ParameterizedTest
    @MethodSource("provideChangeItemCountArgs")
    @IsRoleUser
    void changeItemCountUserTest(String action, long countBefore, long countAfter, boolean emptyCache) {
        Long entityId = 2L;
        trxStepVerifier.create(cartService.changeItemCount(getUser().getId(),entityId, action).
                then(itemService.getItem(entityId))).verifyError(AuthorizationDeniedException.class);
    }

    @ParameterizedTest
    @MethodSource("provideChangeItemCountArgs")
    @IsRoleAdmin
    void changeItemCountAdminTest(String action, long countBefore, long countAfter, boolean emptyCache) {
        Long entityId = 2L;
        changeItemCountTest(action, entityId, countBefore, countAfter, emptyCache, getAdmin(),  getAdminMono(), cartService.changeItemCount(getAdmin().getId(), entityId, action));
    }

    @ParameterizedTest
    @MethodSource("provideChangeItemCountArgs")
    @IsRoleGuest
    void changeItemCountGuestTest(String action, long countBefore, long countAfter, boolean emptyCache) {
        Long entityId = 2L;
        trxStepVerifier.create(cartService.changeItemCount(ID_GUEST, entityId, action).
                then(itemService.getItem(entityId))).verifyError(AuthorizationDeniedException.class);
    }

    @ParameterizedTest
    @MethodSource("provideChangeItemCountArgs")
    @IsRoleUser
    void changeUserItemCountUserTest(String action, long countBefore, long countAfter, boolean emptyCache) {
        Long entityId = 2L;
        changeItemCountTest(action, entityId, countBefore, countAfter, emptyCache, getUser(),  getUserMono(), cartService.changeUserItemCount(entityId, action));
    }

    @ParameterizedTest
    @MethodSource("provideChangeItemCountArgs")
    @IsRoleGuest
    void changeUserItemCountGuestTest(String action, long countBefore, long countAfter, boolean emptyCache) {
        Long entityId = 2L;
        trxStepVerifier.create(cartService.changeUserItemCount(entityId, action).
                then(itemService.getItem(entityId))).verifyError(AuthorizationDeniedException.class);
    }

    private void changeItemCountTest(String action, Long entityId, long countBefore, long countAfter, boolean emptyCache, User testUser,  Mono<User> testUserMono, Mono<Void> testAction) {
        Cart cart = CARTS.get(testUser.getId());
        when(cartRepository.save(any(Cart.class))).thenReturn(Mono.just(cart));
        CartItem savedCartItem = CART_ITEMS.get(cart.getId()).get(entityId);
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(Mono.just(savedCartItem));
        when(cartRepository.findFirstByUserId(testUser.getId())).thenReturn(Mono.just(cart));
        when(userRepository.findByUsername(any(String.class))).thenReturn(testUserMono);
        when(cartRepository.findFirstByUserId(testUser.getId())).thenReturn(Mono.just(cart));

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

        trxStepVerifier.create(testAction.then(itemService.getItem(entityId))).
                consumeNextWith(entity -> {
                    assertThat(entity.count()).isEqualTo(countAfter);
                }).verifyComplete();
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
