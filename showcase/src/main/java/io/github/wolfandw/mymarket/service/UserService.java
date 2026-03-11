package io.github.wolfandw.mymarket.service;

import io.github.wolfandw.mymarket.dto.UserInfoDto;
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import reactor.core.publisher.Mono;

/**
 * Сервис работы с пользователем и паролями.
 */
public interface UserService extends ReactiveUserDetailsService, ReactiveUserDetailsPasswordService {
    /**
     * Возвращает идентификатор текущего пользователя.
     *
     * @return идентификатор текущего пользователя
     */
    Mono<Long> getCurrentUserId();

    /**
     * Возвращает имя текущего пользователя.
     *
     * @return  имя текущего пользователя.
     */
    Mono<String> getCurrentUserName();

    /**
     * Проверяет право пользователя.
     *
     * @return {@code true} если пользователь
     */
    Mono<Boolean> isCurrentUserUser();

    /**
     * Проверяет право администрирования текущего пользователя.
     *
     * @return {@code true} если администратор
     */
    Mono<Boolean> isCurrentUserAdmin();

    /**
     * Возвращает информацию о текущем пользователе.
     *
     * @return информация о текущем пользователе
     */
    Mono<UserInfoDto> getCurrentUserInfo();
}
