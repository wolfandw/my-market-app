package io.github.wolfandw.mymarket.itest.repository;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционные тесты репозитория строк корзин.
 */
public class CartItemRepositoryIntegrationTest extends AbstractRepositoryIntegrationTest {
    @Test
    void findAllByCartIdTest() {
        cartItemRepository.findAllByCartId(DEFAULT_CART_ID).collectList().doOnNext(cartItemsCount -> {
            assertThat(cartItemsCount).size().isEqualTo(12);
            assertThat(cartItemsCount.getFirst().getItemId()).isEqualTo(2L);
            assertThat(cartItemsCount.getFirst().getCount()).isEqualTo(60);
            assertThat(cartItemsCount.getLast().getItemId()).isEqualTo(3L);
            assertThat(cartItemsCount.getLast().getCount()).isEqualTo(80);
        });
    }

    @Test
    void findByCartIdAndItemIdTest() {
        cartItemRepository.findByCartIdAndItemId(DEFAULT_CART_ID, 2L).doOnNext(cartItemsCount -> {
            assertThat(cartItemsCount.getItemId()).isEqualTo(2L);
            assertThat(cartItemsCount.getCount()).isEqualTo(60);
        });
    }

//    @Test
//    void findAllByCartTest() {
//        assertThat(defaultCart).isNotNull();
//
//        List<CartItem> actualContent = cartItemRepository.findAllByCart(defaultCart);
//
//        assertThat(actualContent).size().isEqualTo(12);
//        assertThat(actualContent.get(0).getItem().getTitle()).isEqualTo("Item 08");
//        assertThat(actualContent.get(0).getCount()).isEqualTo(60);
//        assertThat(actualContent.get(11).getItem().getTitle()).isEqualTo("Item 06");
//        assertThat(actualContent.get(11).getCount()).isEqualTo(80);
//    }
//
//    @Test
//    void findAllByCartAndItemInTest() {
//        assertThat(defaultCart).isNotNull();
//
//        List<CartItem> actualContent = cartItemRepository.findAllByCartAndItemIn(defaultCart,
//                List.of(Objects.requireNonNull(itemRepository.findById(2L).orElse(null)),
//                        Objects.requireNonNull(itemRepository.findById(13L).orElse(null))));
//
//        assertThat(actualContent).size().isEqualTo(2);
//        assertThat(actualContent.get(0).getItem().getTitle()).isEqualTo("Item 08");
//        assertThat(actualContent.get(0).getCount()).isEqualTo(60);
//        assertThat(actualContent.get(1).getItem().getTitle()).isEqualTo("Item 06");
//        assertThat(actualContent.get(1).getCount()).isEqualTo(80);
//    }
//
//    @Test
//    void findByCartAndItemIdTest() {
//        assertThat(defaultCart).isNotNull();
//
//        Optional<CartItem> actualContent = cartItemRepository.findByCartAndItemId(defaultCart, 13L);
//
//        assertThat(actualContent).isPresent();
//        assertThat(actualContent.get().getItem().getTitle()).isEqualTo("Item 06");
//        assertThat(actualContent.get().getCount()).isEqualTo(80);
//    }
}