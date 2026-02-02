package io.github.wolfandw.mymarket.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс модели заказа.
 */
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal totalSum = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    /**
     * Создает заказ с идентификатором.
     *
     * @param id идентификатор заказа
     */
    public Order(Long id) {
        this.id = id;
    }

    /**
     * Создает новый заказ.
     */
    public Order() {
        // Default
    }

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
     * Возвращает строки заказа.
     *
     * @return строки заказа
     */
    public List<OrderItem> getItems() {
        return items;
    }

    /**
     * Устанавливает строки заказа.
     *
     * @param items строки заказа
     */
    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}
