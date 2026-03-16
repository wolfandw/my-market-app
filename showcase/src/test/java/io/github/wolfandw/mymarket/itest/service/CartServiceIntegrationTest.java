package io.github.wolfandw.mymarket.itest.service;

import io.github.wolfandw.mymarket.IsRoleAdmin;
import io.github.wolfandw.mymarket.IsRoleUser;
import io.github.wolfandw.mymarket.itest.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.authorization.AuthorizationDeniedException;
import reactor.test.StepVerifier;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционный тест сервиса корзин.
 */
public class CartServiceIntegrationTest extends AbstractIntegrationTest {
    private static final String ACTION_MINUS = "MINUS";
    private static final String ACTION_PLUS = "PLUS";

    @Test
    @IsRoleUser
    public void getCartUserTest() {
        trxStepVerifier.create(cartService.getCart(getUser().getId())).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @IsRoleAdmin
    public void getCartAdminTest() {
        trxStepVerifier.create(cartService.getCart(getAdmin().getId())).
                consumeNextWith(actualCart -> {
                    assertThat(actualCart.total()).isEqualTo(7808L);
                }).verifyComplete();
    }

    @Test
    public void getCartTest() {
        trxStepVerifier.create(cartService.getCart(ID_GUEST)).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @IsRoleUser
    public void getUserCartAdminTest() {
        trxStepVerifier.create(cartService.getUserCart()).
                consumeNextWith(actualCart -> {
                    assertThat(actualCart.total()).isEqualTo(7808L);
                }).verifyComplete();
    }

    @Test
    public void getUserCartTest() {
        trxStepVerifier.create(cartService.getUserCart()).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @IsRoleUser
    void getCartItemsUserTest() {
        trxStepVerifier.create(cartService.getUserCartItems().collectList()).
                assertNext(actualCartItems -> {
                    assertThat(actualCartItems).isNotEmpty();
                    assertThat(actualCartItems.size()).isEqualTo(12);
                    assertThat(actualCartItems.getFirst().title()).isEqualTo("Item 08");
                    assertThat(actualCartItems.getFirst().count()).isEqualTo(60);
                }).verifyComplete();
    }

    @Test
    public void getCartItemsTest() {
        trxStepVerifier.create(cartService.getUserCartItems()).verifyError(AuthorizationDeniedException.class);
    }

    @ParameterizedTest
    @MethodSource("provideChangeItemCountArgs")
    @IsRoleUser
    void changeItemUserCount(String action, long countBefore, long countAfter) {
        Long entityId = 2L;

        StepVerifier.create(itemService.getItem(entityId)).
        consumeNextWith(entity -> {
            assertThat(entity.count()).isEqualTo(countBefore);
        }).verifyComplete();

        trxStepVerifier.create(cartService.changeUserItemCount(entityId, action).then(itemService.getItem(entityId))).
                consumeNextWith(entity -> {
                    assertThat(entity.count()).isEqualTo(countAfter);
                }).verifyComplete();
    }

    @Test
    public void changeItemCount() {
        trxStepVerifier.create(cartService.getUserCartItems()).verifyError(AuthorizationDeniedException.class);
    }

    private static Stream<Arguments> provideChangeItemCountArgs() {
        return Stream.of(
                Arguments.of(ACTION_MINUS, 60, 59),
                Arguments.of(ACTION_PLUS, 60, 61)
        );
    }
}
