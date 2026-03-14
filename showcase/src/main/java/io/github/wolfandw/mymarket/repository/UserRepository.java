package io.github.wolfandw.mymarket.repository;

import io.github.wolfandw.mymarket.model.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Репозиторий для работы с пользователями.
 */
@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {
    /**
     * Возвращает пользователя по имени.
     *
     * @param username имя пользователя
     * @return пользователь
     */
    Mono<User> findByUsername(String username);
}
