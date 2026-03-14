package io.github.wolfandw.mymarket.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Класс модели заказа.
 */
@Table("ORDERS")
public class Order {
    @Id
    private Long id;

    @Column
    private Long userId;

    @Column
    private BigDecimal totalSum = BigDecimal.ZERO;

    @Column
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Возвращает идентификатор заказа.
     *
     * @return идентификатор заказа
     */
    public Long getId() {
        return id;
    }

    /**
     * Устанавливает идентификатор заказа.
     *
     * @param id идентификатор заказа
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Возвращает сумму Всего заказа.
     *
     * @return Сумма Всего заказа
     */
    public BigDecimal getTotalSum() {
        return totalSum;
    }

    /**
     * Устанавливает сумму Всего заказа.
     *
     * @param totalSum Сумма Всего заказа
     */
    public void setTotalSum(BigDecimal totalSum) {
        this.totalSum = totalSum;
    }

    /**
     * Возвращает дату и время создания заказа.
     *
     * @return дата и время создания заказа
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Устанавливает дату и время создания заказа.
     *
     * @param createdAt дата и время создания заказа
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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
