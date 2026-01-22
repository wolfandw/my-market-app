package io.github.wolfandw.mymarket.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Класс модели строки корзины.
 */
@Entity
@Table(name = "cart_items")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(nullable = false)
    private Integer count = 1;

    /**
     * Возвращает идентификатор строки корзины.
     *
     * @return идентификатор строки корзины
     */
    public Long getId() {
        return id;
    }

    /**
     * Устанавливает идентификатор строки корзины.
     *
     * @param id идентификатор строки корзины
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Возвращает родительскую корзину.
     *
     * @return родительская корзина
     */
    public Cart getCart() {
        return cart;
    }

    /**
     * Устанавливает родительскую корзину.
     *
     * @param cart родительская корзина
     */
    public void setCart(Cart cart) {
        this.cart = cart;
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
     * Возвращает количество товара в строке корзины.
     *
     * @return количество товара
     */
    public Integer getCount() {
        return count;
    }

    /**
     * Устанавливает количество товара в строке корзины.
     *
     * @param count количество товара
     */
    public void setCount(Integer count) {
        this.count = count;
    }
}
