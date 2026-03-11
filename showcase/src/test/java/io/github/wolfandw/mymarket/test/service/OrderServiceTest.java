package io.github.wolfandw.mymarket.test.service;

import io.github.wolfandw.mymarket.dto.OrderDto;
import io.github.wolfandw.mymarket.model.Order;
import io.github.wolfandw.mymarket.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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
    @MockitoBean(reset = MockReset.BEFORE)
    private UserService mockUserService;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @WithMockUser(roles = "ADMIN")
    void getOrdersTest(boolean emptyCache) {
        Long orderId = 1L;
        when(orderRepository.findAll()).thenReturn(Flux.fromStream(ORDERS.values().stream()));
        Order order = ORDERS.get(orderId);
        when(orderItemRepository.findAllByOrderId(orderId)).thenReturn(Flux.fromStream(ORDER_ITEMS.get(orderId).values().stream()));

        mockItem();
        if (emptyCache) {
            when(itemCache.getItem(any(Long.class))).thenReturn(Mono.empty());
        }
        else {
            mockGetItemFromCache();
        }
        mockCacheItemFromCache();

        StepVerifier.create(orderService.getOrders().collectList()).
                consumeNextWith(orders -> {
                    assertThat(orders.size()).isEqualTo(1);
                    OrderDto actualOrder = orders.getFirst();
                    assertThat(actualOrder.totalSum()).isEqualTo(8129L);
                    assertThat(actualOrder.items().size()).isEqualTo(12);
                }).verifyComplete();

        verify(itemCache, times(emptyCache ? 12 : 0)).cache(any());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @WithMockUser(roles = "USER")
    void getUserOrdersTest(boolean emptyCache) {
        Long orderId = 1L;
        when(orderRepository.findAllByUserId(orderId)).thenReturn(Flux.fromStream(ORDERS.values().stream()));
        Order order = ORDERS.get(orderId);
        when(orderItemRepository.findAllByOrderId(orderId)).thenReturn(Flux.fromStream(ORDER_ITEMS.get(orderId).values().stream()));
        when(mockUserService.getCurrentUserId()).thenReturn(Mono.just(orderId));

        mockItem();
        if (emptyCache) {
            when(itemCache.getItem(any(Long.class))).thenReturn(Mono.empty());
        }
        else {
            mockGetItemFromCache();
        }
        mockCacheItemFromCache();

        StepVerifier.create(orderService.getUserOrders().collectList()).
                consumeNextWith(orders -> {
                    assertThat(orders.size()).isEqualTo(1);
                    OrderDto actualOrder = orders.getFirst();
                    assertThat(actualOrder.totalSum()).isEqualTo(8129L);
                    assertThat(actualOrder.items().size()).isEqualTo(12);
                }).verifyComplete();

        verify(itemCache, times(emptyCache ? 12 : 0)).cache(any());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void getUserOrdersIsUnauthorizedTest(boolean emptyCache) {
        Long orderId = 1L;
        when(orderRepository.findAllByUserId(orderId)).thenReturn(Flux.fromStream(ORDERS.values().stream()));
        Order order = ORDERS.get(orderId);
        when(orderItemRepository.findAllByOrderId(orderId)).thenReturn(Flux.fromStream(ORDER_ITEMS.get(orderId).values().stream()));
        when(mockUserService.getCurrentUserId()).thenReturn(Mono.just(orderId));

        mockItem();
        if (emptyCache) {
            when(itemCache.getItem(any(Long.class))).thenReturn(Mono.empty());
        }
        else {
            mockGetItemFromCache();
        }
        mockCacheItemFromCache();

        StepVerifier.create(orderService.getUserOrders().collectList()).verifyError(AuthorizationDeniedException.class);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @WithMockUser(roles = "ADMIN")
    void getOrderItemsTest(boolean emptyCache) {
        Long orderId = 1L;
        Order order = ORDERS.get(orderId);
        when(orderItemRepository.findAllByOrderId(orderId)).thenReturn(Flux.fromStream(ORDER_ITEMS.get(orderId).values().stream()));
        when(orderRepository.findById(orderId)).thenReturn(Mono.just(order));

        mockItem();
        if (emptyCache) {
            when(itemCache.getItem(any(Long.class))).thenReturn(Mono.empty());
        }
        else {
            mockGetItemFromCache();
        }
        mockCacheItemFromCache();

        StepVerifier.create(orderService.getOrderItems(orderId).collectList()).
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
        when(mockUserService.getCurrentUserId()).thenReturn(Mono.just(orderId));

        mockItem();
        if (emptyCache) {
            when(itemCache.getItem(any(Long.class))).thenReturn(Mono.empty());
        }
        else {
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
    @WithMockUser(roles = "ADMIN")
    void getOrderTest() {
        Long orderId = 1L;
        Order order = ORDERS.get(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Mono.just(order));

        StepVerifier.create(orderService.getOrder(orderId, false)).
                consumeNextWith(orderDto -> {
                    assertThat(orderDto.totalSum()).isEqualTo(8129L);
                }).verifyComplete();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUserOrderTest() {
        Long orderId = 1L;
        Order order = ORDERS.get(orderId);
        when(orderRepository.findByIdAndUserId(orderId, orderId)).thenReturn(Mono.just(order));
        when(mockUserService.getCurrentUserId()).thenReturn(Mono.just(orderId));

        StepVerifier.create(orderService.getUserOrder(orderId, false)).
                consumeNextWith(orderDto -> {
                    assertThat(orderDto.totalSum()).isEqualTo(8129L);
                }).verifyComplete();
    }
}
