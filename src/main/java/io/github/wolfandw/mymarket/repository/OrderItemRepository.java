//package io.github.wolfandw.mymarket.repository;
//
//import io.github.wolfandw.mymarket.model.Item;
//import io.github.wolfandw.mymarket.model.Order;
//import io.github.wolfandw.mymarket.model.OrderItem;
//import org.springframework.data.repository.reactive.ReactiveCrudRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.Collection;
//import java.util.List;
//import java.util.Optional;
//
///**
// * Репозиторий для работы со строками заказов.
// */
//@Repository
//public interface OrderItemRepository extends ReactiveCrudRepository<OrderItem, Long> {
//    /**
//     * Возвращает список строк заказа.
//     *
//     * @param order заказ
//     * @return список строк заказ.
//     */
//    List<OrderItem> findAllByOrder(Order order);
//
//    /**
//     * Возвращает список строк заказа с фильтром по товарам.
//     *
//     * @param order заказ
//     * @param items фильтр по товарам
//     * @return список строк заказа с фильтром по товарам.
//     */
//    List<OrderItem> findAllByOrderAndItemIn(Order order, Collection<Item> items);
//
//    /**
//     * Возвращает строку заказа с фильтром по идентификатору товара.
//     *
//     * @param order заказ
//     * @param itemId фильтр по идентификатору товара
//     * @return строка заказа с фильтром по идентификатору товара.
//     */
//    Optional<OrderItem> findByOrderAndItemId(Order order, Long itemId);
//}
