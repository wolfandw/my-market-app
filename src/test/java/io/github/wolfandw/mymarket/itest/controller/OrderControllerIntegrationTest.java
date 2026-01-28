package io.github.wolfandw.mymarket.itest.controller;

import io.github.wolfandw.mymarket.MyMarketUtils;
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
                .andExpect(model().attributeExists(MyMarketUtils.ATTRIBUTE_ORDERS))
                .andExpect(model().attribute(MyMarketUtils.ATTRIBUTE_ORDERS, IsCollectionWithSize.<List<OrderDto>>hasSize(1)))
                .andExpect(view().name(MyMarketUtils.TEMPLATE_ORDERS));
    }

    @Test
    void getOrderTest() throws Exception {
        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(MyMarketUtils.ATTRIBUTE_ORDER))
                .andExpect(model().attribute(MyMarketUtils.ATTRIBUTE_ORDER, IsNull.notNullValue(OrderDto.class)))
                .andExpect(model().attribute(MyMarketUtils.ATTRIBUTE_ORDER, HasProperty.hasProperty(MyMarketUtils.ATTRIBUTE_ITEMS)))
                .andExpect(model().attribute(MyMarketUtils.ATTRIBUTE_ORDER, HasPropertyWithValue.hasProperty(MyMarketUtils.ATTRIBUTE_ITEMS, IsCollectionWithSize.<List<ItemDto>>hasSize(12))))
                .andExpect(model().attribute(MyMarketUtils.ATTRIBUTE_ORDER, HasProperty.hasProperty(MyMarketUtils.ATTRIBUTE_TOTAL_SUM)))
                .andExpect(model().attribute(MyMarketUtils.ATTRIBUTE_ORDER, HasPropertyWithValue.hasProperty(MyMarketUtils.ATTRIBUTE_TOTAL_SUM, equalTo(8120L))))
                .andExpect(view().name(MyMarketUtils.TEMPLATE_ORDER));
    }
}
