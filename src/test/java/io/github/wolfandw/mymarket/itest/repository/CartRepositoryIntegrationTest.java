package io.github.wolfandw.mymarket.itest.repository;

import io.github.wolfandw.mymarket.itest.AbstractIntegrationTest;
import io.github.wolfandw.mymarket.model.Cart;
import io.github.wolfandw.mymarket.model.Item;
import org.assertj.core.api.BigDecimalAssert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционные тесты репозитория корзин.
 */
@DataJpaTest
public class CartRepositoryIntegrationTest extends AbstractIntegrationTest {
    @Test
    void findAllTest() {
        List<Cart> actualCarts = cartRepository.findAll();

        assertThat(actualCarts).size().isEqualTo(1);
        assertThat(actualCarts.get(0).getId()).isEqualTo(1L);
        assertThat(actualCarts.get(0).getTotal()).isEqualTo(new BigDecimal(8190));
    }
}