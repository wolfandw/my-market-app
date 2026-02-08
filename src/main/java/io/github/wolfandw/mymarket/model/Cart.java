package io.github.wolfandw.mymarket.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

/**
 * Класс модели корзины.
 */
@Table("CARTS")
public class Cart {
    @Id
    private Long id;

    @Column
    private BigDecimal total = BigDecimal.ZERO;

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
}
