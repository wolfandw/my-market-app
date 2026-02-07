//package io.github.wolfandw.mymarket.itest.service;
//
//import io.github.wolfandw.mymarket.dto.CartDto;
//import io.github.wolfandw.mymarket.dto.ItemDto;
//import io.github.wolfandw.mymarket.itest.AbstractIntegrationTest;
//import org.junit.jupiter.api.Test;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
///**
// * Интеграционный тест сервиса корзин.
// */
//public class CartServiceIntegrationTest extends AbstractIntegrationTest {
//    private static final String ACTION_MINUS = "MINUS";
//    private static final String ACTION_PLUS = "PLUS";
//
//    @Test
//    void getCartTest() {
//        Long cartId = DEFAULT_CART_ID;
//        CartDto actualCart = cartService.getCart(cartId);
//        assertThat(actualCart).isNotNull();
//
//        Long actualCartTotal = actualCart.total();
//        assertThat(actualCartTotal).isEqualTo(7700);
//
//        List<ItemDto> actualCartItems = actualCart.items();
//        assertThat(actualCartItems).isNotNull();
//        assertThat(actualCartItems.size()).isEqualTo(12);
//        assertThat(actualCartItems.getFirst().title()).isEqualTo("Item 08");
//        assertThat(actualCartItems.getFirst().count()).isEqualTo(60);
//    }
//
//    @Test
//    @Transactional
//    void changeItemCountPlus() {
//        Long cartId = DEFAULT_CART_ID;
//        Long entityId = 2L;
//
//        Optional<ItemDto> entity = itemService.getItem(cartId, entityId);
//        assertTrue(entity.isPresent(), "Сущность должна присутствовать");
//        int countBefore = entity.get().count();
//
//        cartService.changeItemCount(cartId, entityId, ACTION_PLUS);
//
//        entity = itemService.getItem(cartId, entityId);
//        assertTrue(entity.isPresent(), "Сущность должна присутствовать");
//        int countAfter = entity.get().count();
//
//        assertThat(countAfter - countBefore).isEqualTo(1);
//    }
//
//    @Test
//    @Transactional
//    void changeItemCountMinus() {
//        Long cartId = DEFAULT_CART_ID;
//        Long entityId = 2L;
//
//        Optional<ItemDto> entity = itemService.getItem(cartId, entityId);
//        assertTrue(entity.isPresent(), "Сущность должна присутствовать");
//        int countBefore = entity.get().count();
//
//        cartService.changeItemCount(cartId, entityId, ACTION_MINUS);
//
//        entity = itemService.getItem(cartId, entityId);
//        assertTrue(entity.isPresent(), "Сущность должна присутствовать");
//        int countAfter = entity.get().count();
//
//        assertThat(countBefore - countAfter).isEqualTo(1);
//    }
//}
