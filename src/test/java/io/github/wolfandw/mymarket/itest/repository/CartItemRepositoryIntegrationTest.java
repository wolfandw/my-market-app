package io.github.wolfandw.mymarket.itest.repository;

import io.github.wolfandw.mymarket.model.Cart;
import io.github.wolfandw.mymarket.model.CartItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционные тесты репозитория строк корзин.
 */
public class CartItemRepositoryIntegrationTest extends AbstractRepositoryIntegrationTest {
    private Cart defaultCart;

    @BeforeEach
    void setup() {
        defaultCart = cartRepository.findById(1L).orElse(null);
    }

    @Test
    void findAllByCartTest() {
        assertThat(defaultCart).isNotNull();

        List<CartItem> actualContent = cartItemRepository.findAllByCart(defaultCart);

        assertThat(actualContent).size().isEqualTo(13);
        assertThat(actualContent.get(0).getItem().getTitle()).isEqualTo("Item 07 SearchTag");
        assertThat(actualContent.get(0).getCount()).isEqualTo(70);
        assertThat(actualContent.get(12).getItem().getTitle()).isEqualTo("Item 06");
        assertThat(actualContent.get(12).getCount()).isEqualTo(80);
    }

    @Test
    void findAllByCartAndItemInTest() {
        assertThat(defaultCart).isNotNull();

        List<CartItem> actualContent = cartItemRepository.findAllByCartAndItemIn(defaultCart,
                List.of(Objects.requireNonNull(itemRepository.findById(1L).orElse(null)),
                        Objects.requireNonNull(itemRepository.findById(13L).orElse(null))));

        assertThat(actualContent).size().isEqualTo(2);
        assertThat(actualContent.get(0).getItem().getTitle()).isEqualTo("Item 07 SearchTag");
        assertThat(actualContent.get(0).getCount()).isEqualTo(70);
        assertThat(actualContent.get(1).getItem().getTitle()).isEqualTo("Item 06");
        assertThat(actualContent.get(1).getCount()).isEqualTo(80);
    }

    @Test
    void findByCartAndItemIdTest() {
        assertThat(defaultCart).isNotNull();

        Optional<CartItem> actualContent = cartItemRepository.findByCartAndItemId(defaultCart, 13L);

        assertThat(actualContent).isPresent();
        assertThat(actualContent.get().getItem().getTitle()).isEqualTo("Item 06");
        assertThat(actualContent.get().getCount()).isEqualTo(80);
    }
}

