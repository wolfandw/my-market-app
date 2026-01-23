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
}
