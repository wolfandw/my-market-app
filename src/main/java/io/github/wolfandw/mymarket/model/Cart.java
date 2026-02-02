package io.github.wolfandw.mymarket.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс модели корзины.
 */
@Entity
@Table(name = "carts")
public class Cart {
    @Id
    private Long id;

    @Column(nullable = false)
    private BigDecimal total = BigDecimal.ZERO;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    /**
     * Создает корзину.
     */
    public Cart() {
        // By default
    }

    /**
     * Создает корзину с идентификатором.
     *
     * @param id идентификатор корзины
     */
    public Cart(Long id) {
        this.id = id;
    }

    /**
     * Возвращает идентификатор корзины.
     *
     * @return идентификатор корзины
     */
    public Long getId() {
        return id;
    }

    /**
     * Устанавливает идентификатор корзины.
     *
     * @param id идентификатор корзины
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Возвращает сумму Всего корзины.
     *
     * @return Сумма Всего корзины
     */
    public BigDecimal getTotal() {
        return total;
    }

    /**
     * Устанавливает сумму Всего корзины.
     *
     * @param total Сумма Всего корзины
     */
    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    /**
     * Возвращает строки корзины.
     *
     * @return строки корзины
     */
    public List<CartItem> getItems() {
        return items;
    }

    /**
     * Устанавливает строки корзины.
     *
     * @param items строки корзины
     */
    public void setItems(List<CartItem> items) {
        this.items = items;
    }
}
