package io.github.wolfandw.mymarket.itest.service;

import io.github.wolfandw.mymarket.dto.DtoConstants;
import io.github.wolfandw.mymarket.dto.CartDto;
import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.itest.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Интеграционный тест сервиса корзин.
 */
public class CartServiceIntegrationTest extends AbstractIntegrationTest {
    @Test
    void getCartTest() {
        Long cartId = DtoConstants.DEFAULT_CART_ID;
        CartDto actualCart = cartService.getCart(cartId);
        assertThat(actualCart).isNotNull();

        Long actualCartTotal = actualCart.total();
        assertThat(actualCartTotal).isEqualTo(7700);

        List<ItemDto> actualCartItems = actualCart.items();
        assertThat(actualCartItems).isNotNull();
        assertThat(actualCartItems.size()).isEqualTo(12);
        assertThat(actualCartItems.getFirst().title()).isEqualTo("Item 08");
        assertThat(actualCartItems.getFirst().count()).isEqualTo(60);
    }

    @Test
    @Transactional
    void changeItemCountPlus() {
        Long cartId = DtoConstants.DEFAULT_CART_ID;
        Long entityId = 2L;

        Optional<ItemDto> entity = itemService.getItem(cartId, entityId);
        assertTrue(entity.isPresent(), "Сущность должна присутствовать");
        int countBefore = entity.get().count();

        cartService.changeItemCount(cartId, entityId, DtoConstants.ACTION_PLUS);

        entity = itemService.getItem(cartId, entityId);
        assertTrue(entity.isPresent(), "Сущность должна присутствовать");
        int countAfter = entity.get().count();

        assertThat(countAfter - countBefore).isEqualTo(1);
    }

    @Test
    @Transactional
    void changeItemCountMinus() {
        Long cartId = DtoConstants.DEFAULT_CART_ID;
        Long entityId = 2L;

        Optional<ItemDto> entity = itemService.getItem(cartId, entityId);
        assertTrue(entity.isPresent(), "Сущность должна присутствовать");
        int countBefore = entity.get().count();

        cartService.changeItemCount(cartId, entityId, DtoConstants.ACTION_MINUS);

        entity = itemService.getItem(cartId, entityId);
        assertTrue(entity.isPresent(), "Сущность должна присутствовать");
        int countAfter = entity.get().count();

        assertThat(countBefore - countAfter).isEqualTo(1);
    }

    @Test
    @Transactional
    void clearCartTest() {
        Long cartId = DtoConstants.DEFAULT_CART_ID;
        CartDto actualCart = cartService.getCart(cartId);
        assertThat(actualCart).isNotNull();

        Long totalBefore = actualCart.total();
        assertThat(totalBefore).isEqualTo(7700);

        List<ItemDto> actualCartItems = actualCart.items();
        assertThat(actualCartItems).isNotNull();

        int sizeBefore = actualCartItems.size();
        assertThat(sizeBefore).isEqualTo(12);

        cartService.clearCart(cartId);

        actualCart = cartService.getCart(cartId);
        assertThat(actualCart).isNotNull();

        Long totalAfter = actualCart.total();
        assertThat(totalAfter).isEqualTo(0);

        actualCartItems = actualCart.items();
        assertThat(actualCartItems).isNotNull();

        int sizeAfter = actualCartItems.size();
        assertThat(sizeAfter).isEqualTo(0);
    }
}
