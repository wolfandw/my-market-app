package io.github.wolfandw.mymarket.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

/**
 * Класс строки заказа.
 */
@Table(name = "ORDER_ITEMS")
public class OrderItem {
    @Id
    private Long id;

    @Column("ORDER_ID")
    private Long orderId;

    @Column("ITEM_ID")
    private Long itemId;

    @Column
    private Integer count = 1;

    /**
     * Создает элемент заказа.
     *
     * @param id идентификатор строки заказа
     * @param orderId заказ
     * @param itemId товар
     * @param count количество
     */
    public OrderItem(Long id, Long orderId, Long itemId, Integer count ) {
        this.id = id;
        this.orderId = orderId;
        this.itemId = itemId;
        this.count = count;
    }

    /**
     * Создает элемент заказа.
     *
     * @param orderId заказ
     * @param itemId товар
     * @param count количество
     */
    public OrderItem(Long orderId, Long itemId, Integer count ) {
        this.orderId = orderId;
        this.itemId = itemId;
        this.count = count;
    }

    /**
     * Создает элемент корзины.
     */
    public OrderItem() {
        // By default
    }
    /**
     * Возвращает идентификатор строки заказа.
     *
     * @return идентификатор строки заказа
     */
    public Long getId() {
        return id;
    }

    /**
     * Устанавливает идентификатор строки заказа.
     *
     * @param id идентификатор строки заказа
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Возвращает родительский заказ.
     *
     * @return родительский заказ
     */
    public Long getOrderId() {
        return orderId;
    }

    /**
     * Устанавливает родительский заказ.
     *
     * @param orderId родительский заказ
     */
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    /**
     * Возвращает товар.
     *
     * @return товар
     */
    public Long getItemId() {
        return itemId;
    }

    /**
     * Устанавливает товар.
     *
     * @param itemId товар
     */
    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    /**
     * Возвращает количество товара в строке заказа.
     *
     * @return количество товара
     */
    public Integer getCount() {
        return count;
    }

    /**
     * Устанавливает количество товара в строке заказа.
     *
     * @param count количество товара
     */
    public void setCount(Integer count) {
        this.count = count;
    }
}
