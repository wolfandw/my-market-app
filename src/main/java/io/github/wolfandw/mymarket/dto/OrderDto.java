package io.github.wolfandw.mymarket.dto;

import java.util.List;

/**
 * DTO-представление заказа.
 *
 * @param id идентификатор заказа
 * @param items состав заказа
 * @param totalSum сумма Всего заказа
 */
public record OrderDto(Long id,
                       List<ItemDto> items,
                       Long totalSum) {
    /**
     * Возвращает список товаров заказа.
     * Для тестов.
     *
     * @return список товаров заказа
     */
    public List<ItemDto> getItems() {
        return items;
    }

    /**
     * Возвращает итоговую сумму заказа.
     *
     * @return итоговая сумма заказа.
     */
    public Long getTotalSum() {
        return totalSum;
    }
}