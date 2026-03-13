package io.github.wolfandw.mymarket.itest.service;

import io.github.wolfandw.mymarket.IsRoleAdmin;
import io.github.wolfandw.mymarket.IsRoleGuest;
import io.github.wolfandw.mymarket.IsRoleUser;
import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.dto.OrderDto;
import io.github.wolfandw.mymarket.dto.UserInfoDto;
import io.github.wolfandw.mymarket.itest.AbstractIntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.authorization.AuthorizationDeniedException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Интеграционный тест сервиса заказов.
 */
public class OrderServiceIntegrationTest extends AbstractIntegrationTest {
    @Test
    @IsRoleAdmin
    public void getOrdersAdminTest() {
        getOrdersTest(orderService.getOrders(), getAdminInfo());
    }

    @Test
    @IsRoleUser
    public void getOrdersUserTest() {
        trxStepVerifier.create(orderService.getOrders().collectList()).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @IsRoleGuest
    public void getOrdersGuestTest() {
        trxStepVerifier.create(orderService.getOrders().collectList()).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @IsRoleUser
    public void getUserOrdersUserTest() {
        getOrdersTest(orderService.getUserOrders(), getUserInfo());
    }

    @Test
    @IsRoleGuest
    public void getUserOrdersGuestTest() {
        trxStepVerifier.create(orderService.getOrders().collectList()).verifyError(AuthorizationDeniedException.class);
    }

    private void getOrdersTest(Flux<OrderDto> testOrdersMono, UserInfoDto userInfo) {
        trxStepVerifier.create(testOrdersMono.collectList()).
                consumeNextWith(orders -> {
                    assertThat(orders.size()).isEqualTo(userInfo.isAdmin() ? 2 : 1);
                    OrderDto actualOrder = orders.getFirst();
                    assertThat(actualOrder.totalSum()).isEqualTo(8129L);
                    assertThat(actualOrder.items().size()).isEqualTo(12);
                }).verifyComplete();
    }

    @Test
    @IsRoleUser
    public void getOrderItemsUserTest() throws InterruptedException {
        trxStepVerifier.create(orderService.getOrderItems(1L).collectList()).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @IsRoleAdmin
    public void getOrderItemsAdminTest() throws InterruptedException {
        getOrderItemsTest(orderService.getOrderItems(1L));
    }

    @Test
    @IsRoleGuest
    public void getOrderItemsGuestTest() throws InterruptedException {
        trxStepVerifier.create(orderService.getOrderItems(1L).collectList()).verifyError(AuthorizationDeniedException.class);
    }

    private void getOrderItemsTest(Flux<ItemDto> orderItems) throws InterruptedException {
        Long orderId = 1L;
        TimeUnit.SECONDS.sleep(2L);
        trxStepVerifier.create(orderItems.collectList()).
                assertNext(actualOrderItems -> {
                    Assertions.assertThat(actualOrderItems).isNotEmpty();
                    System.out.println(actualOrderItems);
                    Assertions.assertThat(actualOrderItems.size()).isEqualTo(12);
                    Assertions.assertThat(actualOrderItems.get(0).title()).isEqualTo("Item 08");
                    Assertions.assertThat(actualOrderItems.get(0).count()).isEqualTo(65);
                }).verifyComplete();
    }

    @Test
    @IsRoleUser
    public void getOrderUserTest() {
        trxStepVerifier.create(orderService.getOrder(1L, false)).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @IsRoleAdmin
    public void getOrderAdminTest() {
        getOrderTest(orderService.getOrder(1L, false));
    }

    @Test
    @IsRoleGuest
    public void getOrderGuestTest() {
        trxStepVerifier.create(orderService.getOrder(1L, false)).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @IsRoleUser
    public void getUserOrderUserTest() {
        getOrderTest(orderService.getUserOrder(2L, false));
    }

    @Test
    @IsRoleGuest
    public void getUserOrderGuestTest() {
        trxStepVerifier.create(orderService.getUserOrder(2L, false)).verifyError(AuthorizationDeniedException.class);
    }

    private void getOrderTest(Mono<OrderDto> order) {
        trxStepVerifier.create(order).
                consumeNextWith(orderDto -> {
            assertThat(orderDto.totalSum()).isEqualTo(8129L);
        }).verifyComplete();
    }
}
