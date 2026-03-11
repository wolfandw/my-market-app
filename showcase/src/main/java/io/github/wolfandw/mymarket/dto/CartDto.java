package io.github.wolfandw.mymarket.dto;

/**
 * DTO-представление корзины.
 *
 * @param id идентификатор корзины
 * @param userId идентификатор пользователя корзины
 * @param total сумма Всего корзины
 */
public record CartDto(Long id, Long userId, Long total) {
}
