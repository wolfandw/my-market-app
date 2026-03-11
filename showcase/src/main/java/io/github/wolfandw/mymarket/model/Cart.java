package io.github.wolfandw.mymarket.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

/**
 * Класс модели корзины.
 */
@Table("CARTS")
public class Cart implements Persistable<Long> {
    @Id
    private Long id;

    @Column
    private Long userId;

    @Column
    private BigDecimal total = BigDecimal.ZERO;

    @Transient
    private boolean isNew = false;

    /**
     * Создает корзину.
     */
    public Cart() {
        // Default
    }

    /**
     * Создает корзину.
     */
    public Cart(Long userId) {
        this.userId = userId;
    }

    /**
     * Создает корзину с идентификатором.
     *
     * @param id идентификатор корзины
     */
    public Cart(Long id, Long userId) {
        this.id = id;
        this.userId = userId;
        this.isNew = true;
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
     * Обход особенности Spring Data.
     *
     * @see <a href="https://github.com/spring-projects/spring-data-r2dbc/issues/49?ysclid=mlk19qb7kw351871225">Spring Projects issue</a>
     * @return true если сохраняется новый объект с указанным явно id
     */
    @Override
    public boolean isNew() {
        if (isNew) {
            isNew = false;
            return true;
        }
        return id == null;
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
