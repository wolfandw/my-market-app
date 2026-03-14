package io.github.wolfandw.mymarket.test.service;

import io.github.wolfandw.mymarket.IsRoleAdmin;
import io.github.wolfandw.mymarket.IsRoleGuest;
import io.github.wolfandw.mymarket.IsRoleUser;
import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.dto.OrderDto;
import io.github.wolfandw.mymarket.model.Order;
import io.github.wolfandw.mymarket.model.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Интеграционные тесты сервиса заказов.
 */
public class OrderServiceTest extends AbstractServiceTest {
    @Test
    @IsRoleUser
    public void getOrdersUserTest() {
        StepVerifier.create(orderService.getOrders().collectList()).verifyError(AuthorizationDeniedException.class);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @IsRoleAdmin
    public void getOrdersAdminTest(boolean emptyCache) {
        getOrdersTest(emptyCache, getAdmin(), getAdminMono(), orderService.getOrders());
    }

    @Test
    @IsRoleGuest
    public void getOrdersGuestTest() {
        StepVerifier.create(orderService.getOrders().collectList()).verifyError(AuthorizationDeniedException.class);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @IsRoleUser
    public void getUserOrdersUserTest(boolean emptyCache) {
        getOrdersTest(emptyCache, getUser(), getUserMono(), orderService.getUserOrders());
    }

    @Test
    @IsRoleGuest
    public void getUserOrdersGuestTest() {
        StepVerifier.create(orderService.getUserOrders().collectList()).verifyError(AuthorizationDeniedException.class);
    }

    private void getOrdersTest(boolean emptyCache, User testUser, Mono<User> testUserMono, Flux<OrderDto> orders) {
        when(orderRepository.findAll()).thenReturn(Flux.fromIterable(ORDERS.values()));
        when(orderRepository.findAllByUserId(testUser.getId())).thenReturn(Flux.just(ORDERS.get(testUser.getId())));
        when(userRepository.findByUsername(any(String.class))).thenReturn(testUserMono);
        mockOrderItem();

        mockItem();
        if (emptyCache) {
            when(itemCache.getItem(any(Long.class))).thenReturn(Mono.empty());
        } else {
            mockGetItemFromCache();
        }
        mockCacheItemFromCache();

        StepVerifier.create(orders.collectList()).
                consumeNextWith(actualOrders -> {
                    assertThat(actualOrders.isEmpty()).isFalse();
                    OrderDto actualOrder = actualOrders.getFirst();
                    assertThat(actualOrder.totalSum()).isEqualTo(8129L);
                    assertThat(actualOrder.items().size()).isEqualTo(12);
                }).verifyComplete();

        if (!emptyCache) {
            verify(itemCache, never()).cache(any());
        }
    }

    @Test
    @IsRoleUser
    public void getOrderItemsUserTest() {
        StepVerifier.create(orderService.getOrderItems(getUser().getId())).verifyError(AuthorizationDeniedException.class);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @IsRoleAdmin
    public void getOrderItemsAdminTest(boolean emptyCache) {
        getOrderItemsTest(emptyCache, getAdmin(), getAdminMono(), orderService.getOrderItems(getAdmin().getId()));
    }

    @Test
    @IsRoleGuest
    public void getOrderItemsGuestTest() {
        StepVerifier.create(orderService.getOrderItems(ID_GUEST)).verifyError(AuthorizationDeniedException.class);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @IsRoleUser
    public void getUserOrderItemsUserTest(boolean emptyCache) {
        getOrderItemsTest(emptyCache, getUser(), getUserMono(), orderService.getUserOrderItems(getUser().getId()));
    }

    @Test
    @IsRoleGuest
    public void getUserOrderItemsGuestTest() {
        StepVerifier.create(orderService.getUserOrderItems(ID_GUEST)).verifyError(AuthorizationDeniedException.class);
    }

    private void getOrderItemsTest(boolean emptyCache, User testUser, Mono<User> testUserMono, Flux<ItemDto> orderItems) {
        Order order = ORDERS.get(testUser.getId());
        when(orderItemRepository.findAllByOrderId(order.getId())).thenReturn(Flux.fromIterable(ORDER_ITEMS.get(order.getId()).values()));
        when(orderRepository.findById(order.getId())).thenReturn(Mono.just(order));
        when(orderRepository.findByIdAndUserId(order.getId(), testUser.getId())).thenReturn(Mono.just(order));
        when(userRepository.findByUsername(any(String.class))).thenReturn(testUserMono);

        mockItem();
        if (emptyCache) {
            when(itemCache.getItem(any(Long.class))).thenReturn(Mono.empty());
        } else {
            mockGetItemFromCache();
        }
        mockCacheItemFromCache();

        StepVerifier.create(orderItems.collectList()).
                assertNext(actualOrderItems -> {
                    Assertions.assertThat(actualOrderItems).isNotEmpty();
                    Assertions.assertThat(actualOrderItems.size()).isEqualTo(12);
                    Assertions.assertThat(actualOrderItems.get(0).title()).isEqualTo("Item 08");
                    Assertions.assertThat(actualOrderItems.get(0).count()).isEqualTo(65);
                }).verifyComplete();

        verify(itemCache, times(emptyCache ? 12 : 0)).cache(any());
    }

    @ValueSource(booleans = {true, false})
    @WithMockUser(roles = "USER")
    void getUserOrderItemsTest(boolean emptyCache) {
        Long orderId = 1L;
        Order order = ORDERS.get(orderId);
        when(orderItemRepository.findAllByOrderId(orderId)).thenReturn(Flux.fromStream(ORDER_ITEMS.get(orderId).values().stream()));
        when(orderRepository.findByIdAndUserId(orderId, orderId)).thenReturn(Mono.just(order));
        when(userRepository.findByUsername(any(String.class))).thenReturn(getUserMono());

        mockItem();
        if (emptyCache) {
            when(itemCache.getItem(any(Long.class))).thenReturn(Mono.empty());
        } else {
            mockGetItemFromCache();
        }
        mockCacheItemFromCache();

        StepVerifier.create(orderService.getUserOrderItems(orderId).collectList()).
                assertNext(actualOrderItems -> {
                    Assertions.assertThat(actualOrderItems).isNotEmpty();
                    Assertions.assertThat(actualOrderItems.size()).isEqualTo(12);
                    Assertions.assertThat(actualOrderItems.get(0).title()).isEqualTo("Item 08");
                    Assertions.assertThat(actualOrderItems.get(0).count()).isEqualTo(65);
                }).verifyComplete();

        verify(itemCache, times(emptyCache ? 12 : 0)).cache(any());
    }

    @Test
    @IsRoleUser
    void getOrderUserTest() {
        StepVerifier.create(orderService.getOrder(getUser().getId(), false)).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @IsRoleAdmin
    void getOrderAdminTest() {
        getOrderTest(getAdmin(), getAdminMono(), orderService.getOrder(getAdmin().getId(), false));
    }

    @Test
    @IsRoleGuest
    void getOrderGuestTest() {
        StepVerifier.create(orderService.getOrder(ID_GUEST, false)).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @IsRoleUser
    void getUserOrderUserTest() {
        getOrderTest(getUser(), getUserMono(), orderService.getUserOrder(getUser().getId(), false));
    }

    @Test
    @IsRoleGuest
    void getUserOrderGuestTest() {
        StepVerifier.create(orderService.getUserOrder(ID_GUEST, false)).verifyError(AuthorizationDeniedException.class);
    }

    private void getOrderTest(User testUser, Mono<User> testUserMono, Mono<OrderDto> orderMono) {
        Order order = ORDERS.get(testUser.getId());
        when(orderRepository.findByIdAndUserId(order.getId(), testUser.getId())).thenReturn(Mono.just(order));
        when(userRepository.findByUsername(any(String.class))).thenReturn(testUserMono);
        when(orderRepository.findById(order.getId())).thenReturn(Mono.just(order));

        StepVerifier.create(orderMono).
                consumeNextWith(orderDto -> {
                    assertThat(orderDto.totalSum()).isEqualTo(8129L);
                }).verifyComplete();
    }
}
