package io.github.wolfandw.mymarket.itest.repository;

import io.github.wolfandw.mymarket.model.Cart;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционные тесты репозитория корзин.
 */
public class CartRepositoryIntegrationTest extends AbstractRepositoryIntegrationTest {
    @Test
    void findAllTest() {
        List<Cart> actualCarts = cartRepository.findAll();

        assertThat(actualCarts).size().isEqualTo(1);
        assertThat(actualCarts.get(0).getId()).isEqualTo(1L);
        assertThat(actualCarts.get(0).getItems()).size().isEqualTo(12);
        assertThat(actualCarts.get(0).getItems().get(0).getItem().getTitle()).isEqualTo("Item 08");
        assertThat(actualCarts.get(0).getItems().get(11).getItem().getTitle()).isEqualTo("Item 06");
        assertThat(actualCarts.get(0).getTotal()).isEqualTo(new BigDecimal(7700));
    }
}