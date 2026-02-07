//package io.github.wolfandw.mymarket.test.service;
//
//import io.github.wolfandw.mymarket.dto.OrderDto;
//import io.github.wolfandw.mymarket.model.Cart;
//import io.github.wolfandw.mymarket.model.CartItem;
//import io.github.wolfandw.mymarket.model.Order;
//import io.github.wolfandw.mymarket.model.OrderItem;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.mockito.Mockito.when;
//
///**
// * Модульные тесты сервиса покупок.
// */
//public class BuyServiceTest extends AbstractServiceTest {
//    @Test
//    void createOrderTest() {
//        Long cartId = DEFAULT_CART_ID;
//        Cart cart = CARTS.get(cartId);
//        assert cart != null;
//        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
//
//        BigDecimal totalBefore = cart.getTotal();
//        Assertions.assertThat(totalBefore).isEqualTo(BigDecimal.valueOf(7700));
//
//        List<CartItem> cartItemsBefore = cart.getItems();
//        Assertions.assertThat(cartItemsBefore).isNotEmpty();
//
//        int sizeBefore = cartItemsBefore.size();
//        Assertions.assertThat(sizeBefore).isEqualTo(12);
//
//        Long orderId = 2L;
//        Order order = new Order(orderId);
//        order.setTotalSum(cart.getTotal());
//        order.setItems(cart.getItems().stream().map(cartItem -> new OrderItem(cartItem.getId(), order,
//                cartItem.getItem(), cartItem.getCount())).toList());
//        when(orderRepository.save(Mockito.any(Order.class))).thenReturn(order);
//
//        Optional<OrderDto> newOrderDto = buyService.buy(cartId);
//        assertThat(newOrderDto).isNotNull();
//        assertThat(newOrderDto.get().totalSum()).isEqualTo(totalBefore.longValue());
//        assertThat(newOrderDto.get().items().size()).isEqualTo(sizeBefore);
//
//        Assertions.assertThat(cart.getTotal()).isEqualTo(BigDecimal.ZERO);
//        Assertions.assertThat(cart.getItems()).isEmpty();
//    }
//}
