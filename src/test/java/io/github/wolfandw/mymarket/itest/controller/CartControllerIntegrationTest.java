package io.github.wolfandw.mymarket.itest.controller;

import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.itest.AbstractIntegrationTest;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Интеграционные тесты корзины товаров.
 */
public class CartControllerIntegrationTest extends AbstractIntegrationTest {
    private static final String TEMPLATE_CART = "cart";

    private static final String ATTRIBUTE_ITEMS = "items";
    private static final String ATTRIBUTE_TOTAL = "total";

    private static final String PARAMETER_ID = "id";
    private static final String PARAMETER_ACTION = "action";

    private static final String ACTION_PLUS = "PLUS";

    @Test
    @Transactional
    void getCartTest() throws Exception {
        mockMvc.perform(get("/cart/items"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(ATTRIBUTE_ITEMS))
                .andExpect(model().attribute(ATTRIBUTE_ITEMS, IsCollectionWithSize.<List<ItemDto>>hasSize(12)))
                .andExpect(model().attributeExists(ATTRIBUTE_TOTAL))
                .andExpect(model().attribute(ATTRIBUTE_TOTAL, equalTo(7700L)))
                .andExpect(view().name(TEMPLATE_CART));
    }

    @Test
    @Transactional
    void changeChartItemCountTest() throws Exception {
        long itemId = 1L;
        mockMvc.perform(post("/cart/items")
                        .param(PARAMETER_ID, Long.toString(itemId))
                        .param(PARAMETER_ACTION, ACTION_PLUS))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(ATTRIBUTE_ITEMS))
                .andExpect(model().attribute(ATTRIBUTE_ITEMS, IsCollectionWithSize.<List<ItemDto>>hasSize(13)))
                .andExpect(model().attributeExists(ATTRIBUTE_TOTAL))
                .andExpect(model().attribute(ATTRIBUTE_TOTAL, equalTo(7707L)))
                .andExpect(view().name(TEMPLATE_CART));
    }
}
