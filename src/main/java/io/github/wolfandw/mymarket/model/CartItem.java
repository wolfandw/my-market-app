package io.github.wolfandw.mymarket.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Класс модели строки корзины.
 */
@Table("CART_ITEMS")
public class CartItem {
    @Id
    private Long id;

    @Column("CART_ID")
    private Long cartId;

    @Column("ITEM_ID")
    private Long itemId;

    @Column
    private Integer count = 0;

    /**
     * Создает элемент корзины.
     *
     * @param id идентификатор строки корзины
     * @param cartId корзина
     * @param itemId товар
     * @param count количество
     */
    public CartItem(Long id, Long cartId, Long itemId, Integer count ) {
        this.id = id;
        this.cartId = cartId;
        this.itemId = itemId;
        this.count = count;
    }

    /**
     * Создает элемент корзины.
     */
    public CartItem() {
        // By default
    }

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
    public Long getCartId() {
        return cartId;
    }

    /**
     * Устанавливает родительскую корзину.
     *
     * @param cartId родительская корзина
     */
    public void setCartId(Long cartId) {
        this.cartId = cartId;
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
