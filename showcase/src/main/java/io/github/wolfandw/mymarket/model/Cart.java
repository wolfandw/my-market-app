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
    private Long userId;

    @Column
    private BigDecimal total = BigDecimal.ZERO;

    /**
     * Создает корзину.
     */
    public Cart() {
        // Default
    }

    /**
     * Создает корзину пользователя.
     *
     * @param userId идентификатор пользователя
     */
    public Cart(Long userId) {
        this.userId = userId;
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
     * Возвращает идентификатор пользователя.
     *
     * @return идентификатор пользователя
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * Устанавливает идентификатор пользователя.
     *
     * @param userId идентификатор пользователя
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
