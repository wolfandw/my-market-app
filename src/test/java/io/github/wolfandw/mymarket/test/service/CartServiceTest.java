//package io.github.wolfandw.mymarket.test.service;
//
//import io.github.wolfandw.mymarket.dto.CartDto;
//import io.github.wolfandw.mymarket.dto.ItemDto;
//import io.github.wolfandw.mymarket.model.Cart;
//import io.github.wolfandw.mymarket.model.CartItem;
//import org.junit.jupiter.api.Test;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
///**
// * Модульные тесты сервиса корзины.
// */
//public class CartServiceTest extends AbstractServiceTest {
//    private static final String ACTION_MINUS = "MINUS";
//    private static final String ACTION_PLUS = "PLUS";
//
//    @Test
//    void getCartTest() {
//        Long cartId = DEFAULT_CART_ID;
//
//        Cart cart = CARTS.get(cartId);
//        when(cartRepository.findById(cartId)).thenReturn(Optional.ofNullable(cart));
//
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
//    void changeItemCountPlus() {
//        Long cartId = DEFAULT_CART_ID;
//        Long entityId = 2L;
//
//        when(itemRepository.findById(entityId)).thenReturn(Optional.ofNullable(ITEMS.get(entityId)));
//
//        Cart cart = CARTS.get(cartId);
//        when(cartRepository.findById(cartId)).thenReturn(Optional.ofNullable(cart));
//        assert cart != null;
//        CartItem cartItem = cart.getItems().getFirst();
//        int countBefore = cartItem.getCount();
//        when(cartItemRepository.findByCartAndItemId(cart, entityId)).thenReturn(Optional.of(cartItem));
//
//        cartService.changeItemCount(cartId, entityId, ACTION_PLUS);
//
//        int countAfter = cartItem.getCount();
//        assertThat(countAfter - countBefore).isEqualTo(1);
//
//        verify(cartItemRepository).save(cartItem);
//        verify(cartRepository).save(cart);
//    }
//    @Test
//    void changeItemCountMinus() {
//        Long cartId = DEFAULT_CART_ID;
//        Long entityId = 2L;
//
//        when(itemRepository.findById(entityId)).thenReturn(Optional.ofNullable(ITEMS.get(entityId)));
//
//        Cart cart = CARTS.get(cartId);
//        when(cartRepository.findById(cartId)).thenReturn(Optional.ofNullable(cart));
//        assert cart != null;
//        CartItem cartItem = cart.getItems().getFirst();
//        int countBefore = cartItem.getCount();
//        when(cartItemRepository.findByCartAndItemId(cart, entityId)).thenReturn(Optional.of(cartItem));
//
//        cartService.changeItemCount(cartId, entityId, ACTION_MINUS);
//
//        int countAfter = cartItem.getCount();
//        assertThat(countBefore- countAfter).isEqualTo(1);
//
//        verify(cartItemRepository).save(cartItem);
//        verify(cartRepository).save(cart);
//    }
//}
