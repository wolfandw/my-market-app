package io.github.wolfandw.mymarket.model;

import jakarta.persistence.*;

/**
 * Класс строки заказа.
 */
@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(nullable = false)
    private Integer count = 1;

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
    public Order getOrder() {
        return order;
    }

    /**
     * Устанавливает родительский заказ.
     *
     * @param order родительский заказ
     */
    public void setCart(Order order) {
        this.order = order;
    }

    /**
     * Возвращает товар.
     *
     * @return товар
     */
    public Item getItem() {
        return item;
    }

    /**
     * Устанавливает товар.
     *
     * @param item товар
     */
    public void setItem(Item item) {
        this.item = item;
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
