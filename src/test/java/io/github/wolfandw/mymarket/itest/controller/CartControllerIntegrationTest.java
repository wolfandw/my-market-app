package io.github.wolfandw.mymarket.itest.controller;

import io.github.wolfandw.mymarket.dto.DtoConstants;
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
    @Test
    @Transactional
    void getCartTest() throws Exception {
        mockMvc.perform(get("/cart/items"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(DtoConstants.ATTRIBUTE_ITEMS))
                .andExpect(model().attribute(DtoConstants.ATTRIBUTE_ITEMS, IsCollectionWithSize.<List<ItemDto>>hasSize(12)))
                .andExpect(model().attributeExists(DtoConstants.ATTRIBUTE_TOTAL))
                .andExpect(model().attribute(DtoConstants.ATTRIBUTE_TOTAL, equalTo(7700L)))
                .andExpect(view().name(DtoConstants.TEMPLATE_CART));
    }

    @Test
    @Transactional
    void changeChartItemCountTest() throws Exception {
        Long itemId = 1L;
        mockMvc.perform(post("/cart/items")
                        .param(DtoConstants.PARAMETER_ID, itemId.toString())
                        .param(DtoConstants.PARAMETER_ACTION, DtoConstants.ACTION_PLUS))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(DtoConstants.ATTRIBUTE_ITEMS))
                .andExpect(model().attribute(DtoConstants.ATTRIBUTE_ITEMS, IsCollectionWithSize.<List<ItemDto>>hasSize(13)))
                .andExpect(model().attributeExists(DtoConstants.ATTRIBUTE_TOTAL))
                .andExpect(model().attribute(DtoConstants.ATTRIBUTE_TOTAL, equalTo(7707L)))
                .andExpect(view().name(DtoConstants.TEMPLATE_CART));
    }
}
