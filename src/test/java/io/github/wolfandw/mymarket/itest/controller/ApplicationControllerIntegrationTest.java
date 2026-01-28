package io.github.wolfandw.mymarket.itest.controller;

import io.github.wolfandw.mymarket.MyMarketUtils;
import io.github.wolfandw.mymarket.itest.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Интеграционные тесты контроллера приложения.
 */
public class ApplicationControllerIntegrationTest extends AbstractIntegrationTest {
    @Test
    void redirectToItemsTest() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isFound())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl('/' + MyMarketUtils.TEMPLATE_ITEMS));
    }

    @Test
    @Transactional
    void buyTest() throws Exception {
        mockMvc.perform(post("/buy"))
                .andExpect(status().isFound())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl('/' + MyMarketUtils.TEMPLATE_ORDERS +
                        '/' + 3 +
                        '?' + MyMarketUtils.PARAMETER_NEW_ORDER + '=' + Boolean.TRUE));
    }
}
