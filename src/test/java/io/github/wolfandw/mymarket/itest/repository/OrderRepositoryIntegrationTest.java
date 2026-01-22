package io.github.wolfandw.mymarket.itest.repository;

import io.github.wolfandw.mymarket.model.Order;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционные тесты репозитория заказов.
 */
public class OrderRepositoryIntegrationTest extends AbstractRepositoryIntegrationTest {
    @Test
    void findAllTest() {
        List<Order> actualOrders = orderRepository.findAll();

        assertThat(actualOrders).size().isEqualTo(1);
        assertThat(actualOrders.get(0).getId()).isEqualTo(1L);
        assertThat(actualOrders.get(0).getItems()).size().isEqualTo(13);
        assertThat(actualOrders.get(0).getItems().get(0).getItem().getTitle()).isEqualTo("Item 07 SearchTag");
        assertThat(actualOrders.get(0).getItems().get(12).getItem().getTitle()).isEqualTo("Item 06");
        assertThat(actualOrders.get(0).getTotalSum()).isEqualTo(new BigDecimal(8645));
    }
}