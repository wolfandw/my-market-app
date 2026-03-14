package io.github.wolfandw.mymarket.itest.repository;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционные тесты репозитория пользователей.
 */
public class UserRepositoryIntegrationTest extends AbstractRepositoryIntegrationTest {
    @Test
    void findByUsernameTest() {
        StepVerifier.create(userRepository.findByUsername("admin")).
                assertNext(actualUser -> {
                    assertThat(actualUser.getId()).isEqualTo(1L);
                    assertThat(actualUser.getRoles()).isEqualTo("USER,ADMIN");
                }).verifyComplete();
    }
}