package io.github.wolfandw.mymarket.test.controller;

import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.dto.OrderDto;
import io.github.wolfandw.mymarket.model.Item;
import io.github.wolfandw.mymarket.model.Order;
import io.github.wolfandw.mymarket.model.OrderItem;
import io.github.wolfandw.mymarket.service.*;
import io.github.wolfandw.mymarket.service.mapper.ItemToDtoMapper;
import io.github.wolfandw.mymarket.test.AbstractTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

/**
 * Абстрактный модельный тест контроллеров.
 */
public class AbstractControllerTest extends AbstractTest {
    @Autowired
    protected MockMvc mockMvc;

    @MockitoBean
    protected ItemToDtoMapper itemToDtoMapper;

    @MockitoBean
    protected ItemService itemService;

    @MockitoBean
    protected CartService cartService;

    @MockitoBean
    protected FileStorageService fileStorageService;

    @MockitoBean
    protected EntityImageService entityImageService;

    @MockitoBean
    protected OrderService orderService;

    /**
     * Маппит модельный товар на его DTO-представление.
     *
     * @param item модельный товар
     * @param count количество в корзине
     * @return DTO-представление товара
     */
    protected ItemDto mapItem(Item item, int count) {
        return new ItemDto(item.getId(), item.getTitle(), item.getDescription(), item.getPrice().longValue(), count);
    }

    /**
     * Маппит модельный заказ на DTO-представление заказа
     * @param order заказ
     * @return DTO-представление заказа
     */
    protected OrderDto mapOrder(Order order) {
        return new OrderDto(order.getId(), mapOrderItems(order.getItems()), order.getTotalSum().longValue());
    }

    private List<ItemDto> mapOrderItems(List<OrderItem> orderItems) {
        return orderItems.stream().map(orderItem -> mapItem(orderItem.getItem(),
                orderItem.getCount())).toList();
    }
}
