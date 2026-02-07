//package io.github.wolfandw.mymarket.itest.repository;
//
//import io.github.wolfandw.mymarket.model.Order;
//import io.github.wolfandw.mymarket.model.OrderItem;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.List;
//import java.util.Objects;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
///**
// * Интеграционные тесты репозитория строк заказов.
// */
//public class OrderItemRepositoryIntegrationTest extends AbstractRepositoryIntegrationTest {
//    private Order defaultOrder;
//
//    @BeforeEach
//    void setup() {
//        defaultOrder = orderRepository.findById(1L).orElse(null);
//    }
//
//    @Test
//    void findAllByOrderTest() {
//        assertThat(defaultOrder).isNotNull();
//
//        List<OrderItem> actualContent = orderItemRepository.findAllByOrder(defaultOrder);
//
//        assertThat(actualContent).size().isEqualTo(12);
//        assertThat(actualContent.get(0).getItem().getTitle()).isEqualTo("Item 08");
//        assertThat(actualContent.get(0).getCount()).isEqualTo(65);
//        assertThat(actualContent.get(11).getItem().getTitle()).isEqualTo("Item 06");
//        assertThat(actualContent.get(11).getCount()).isEqualTo(85);
//    }
//
//    @Test
//    void findAllByOrderAndItemInTest() {
//        assertThat(defaultOrder).isNotNull();
//
//        List<OrderItem> actualContent = orderItemRepository.findAllByOrderAndItemIn(defaultOrder,
//                List.of(Objects.requireNonNull(itemRepository.findById(2L).orElse(null)),
//                        Objects.requireNonNull(itemRepository.findById(13L).orElse(null))));
//
//        assertThat(actualContent).size().isEqualTo(2);
//        assertThat(actualContent.get(0).getItem().getTitle()).isEqualTo("Item 08");
//        assertThat(actualContent.get(0).getCount()).isEqualTo(65);
//        assertThat(actualContent.get(1).getItem().getTitle()).isEqualTo("Item 06");
//        assertThat(actualContent.get(1).getCount()).isEqualTo(85);
//    }
//
//    @Test
//    void findByOrderAndItemIdTest() {
//        assertThat(defaultOrder).isNotNull();
//
//        Optional<OrderItem> actualContent = orderItemRepository.findByOrderAndItemId(defaultOrder, 13L);
//
//        assertThat(actualContent).isPresent();
//        assertThat(actualContent.get().getItem().getTitle()).isEqualTo("Item 06");
//        assertThat(actualContent.get().getCount()).isEqualTo(85);
//    }
//}
//
