package io.github.wolfandw.mymarket.dto;

import java.util.List;

/**
 * DTO-представление корзины.
 *
 * @param items состав корзины
 * @param total сумма Всего корзины
 */
public record CartDto(List<ItemDto> items,
                      long total) {
}
