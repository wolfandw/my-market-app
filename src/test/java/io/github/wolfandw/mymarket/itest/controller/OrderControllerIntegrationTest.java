package io.github.wolfandw.mymarket.itest.controller;

import io.github.wolfandw.mymarket.dto.DtoConstants;
import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.dto.OrderDto;
import io.github.wolfandw.mymarket.itest.AbstractIntegrationTest;
import org.hamcrest.beans.HasProperty;
import org.hamcrest.beans.HasPropertyWithValue;
import org.hamcrest.collection.IsCollectionWithSize;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Интеграционные тесты контроллера заказов.
 */
public class OrderControllerIntegrationTest extends AbstractIntegrationTest {
    @Test
    void getOrdersTest() throws Exception {
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(DtoConstants.ATTRIBUTE_ORDERS))
                .andExpect(model().attribute(DtoConstants.ATTRIBUTE_ORDERS, IsCollectionWithSize.<List<OrderDto>>hasSize(1)))
                .andExpect(view().name(DtoConstants.TEMPLATE_ORDERS));
    }

    @Test
    void getOrderTest() throws Exception {
        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(DtoConstants.ATTRIBUTE_ORDER))
                .andExpect(model().attribute(DtoConstants.ATTRIBUTE_ORDER, IsNull.notNullValue(OrderDto.class)))
                .andExpect(model().attribute(DtoConstants.ATTRIBUTE_ORDER, HasProperty.hasProperty(DtoConstants.ATTRIBUTE_ITEMS)))
                .andExpect(model().attribute(DtoConstants.ATTRIBUTE_ORDER, HasPropertyWithValue.hasProperty(DtoConstants.ATTRIBUTE_ITEMS, IsCollectionWithSize.<List<ItemDto>>hasSize(12))))
                .andExpect(model().attribute(DtoConstants.ATTRIBUTE_ORDER, HasProperty.hasProperty(DtoConstants.ATTRIBUTE_TOTAL_SUM)))
                .andExpect(model().attribute(DtoConstants.ATTRIBUTE_ORDER, HasPropertyWithValue.hasProperty(DtoConstants.ATTRIBUTE_TOTAL_SUM, equalTo(8120L))))
                .andExpect(view().name(DtoConstants.TEMPLATE_ORDER));
    }
}
