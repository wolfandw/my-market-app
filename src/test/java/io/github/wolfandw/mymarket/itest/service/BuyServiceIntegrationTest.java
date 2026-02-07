//package io.github.wolfandw.mymarket.itest.service;
//
//import io.github.wolfandw.mymarket.dto.CartDto;
//import io.github.wolfandw.mymarket.dto.ItemDto;
//import io.github.wolfandw.mymarket.dto.OrderDto;
//import io.github.wolfandw.mymarket.itest.AbstractIntegrationTest;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
///**
// * Интеграционные тесты сервиса покупок.
// */
//public class BuyServiceIntegrationTest extends AbstractIntegrationTest {
//    @Test
//    @Transactional
//    void createOrderTest() {
//        Long cartId = DEFAULT_CART_ID;
//        CartDto cartBefore = cartService.getCart(cartId);
//        Assertions.assertThat(cartBefore).isNotNull();
//
//        Long totalBefore = cartBefore.total();
//        Assertions.assertThat(totalBefore).isEqualTo(7700);
//
//        List<ItemDto> cartItemsBefore = cartBefore.items();
//        Assertions.assertThat(cartItemsBefore).isNotEmpty();
//
//        int sizeBefore = cartItemsBefore.size();
//        Assertions.assertThat(sizeBefore).isEqualTo(12);
//
//        Optional<OrderDto> newOrderDto = buyService.buy(cartId);
//        assertThat(newOrderDto).isPresent();
//        assertThat(newOrderDto.get().totalSum()).isEqualTo(totalBefore);
//        assertThat(newOrderDto.get().items().size()).isEqualTo(sizeBefore);
//
//        CartDto cartAfter = cartService.getCart(cartId);
//        Assertions.assertThat(cartAfter).isNotNull();
//        Assertions.assertThat(cartAfter.total()).isEqualTo(0);
//        Assertions.assertThat(cartAfter.items()).isEmpty();
//    }
//}
