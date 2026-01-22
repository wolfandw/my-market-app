package io.github.wolfandw.mymarket.repository;

import io.github.wolfandw.mymarket.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с корзинами.
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
}
