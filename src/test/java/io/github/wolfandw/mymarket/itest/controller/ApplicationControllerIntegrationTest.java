package io.github.wolfandw.mymarket.itest.controller;

import io.github.wolfandw.mymarket.controller.RedirectUrlFactory;
import io.github.wolfandw.mymarket.itest.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

/**
 * Интеграционные тесты контроллера приложения.
 */
public class ApplicationControllerIntegrationTest extends AbstractIntegrationTest {
    @Test
    void redirectToItemsTest() throws Exception {
        webTestClient.get().uri("/")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals(
                        "Location",
                        RedirectUrlFactory.createUrlToItems()
                );
    }

    @Test
    void buyTest() throws Exception {
        webTestClient.post().uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches(
                        "Location",
                "\\/orders\\/\\d+\\?newOrder\\=true"
                );
    }
}
