package io.github.wolfandw.mymarket.dto;

/**
 * Информация о пользователе.
 *
 * @param userId идентификатор пользователя
 * @param username имя пользователя
 * @param isUser авторизованный пользователь
 * @param isAdmin административные права
 */
public record UserInfoDto(Long userId, String username, boolean isUser, boolean isAdmin) {
}
