package io.github.wolfandw.mymarket.service.mapper.impl;

import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.dto.OrderDto;
import io.github.wolfandw.mymarket.model.Order;
import io.github.wolfandw.mymarket.model.OrderItem;
import io.github.wolfandw.mymarket.service.mapper.ItemToDtoMapper;
import io.github.wolfandw.mymarket.service.mapper.OrderToDtoMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Реализация {@link OrderToDtoMapper}.
 */
@Component
public class OrderToDtoMapperImpl implements OrderToDtoMapper {
    private final ItemToDtoMapper itemToDtoMapper;

    /**
     * Создает маппер строк заказа товаров.
     *
     * @param itemToDtoMapper маппер товаров
     */
    public OrderToDtoMapperImpl(ItemToDtoMapper itemToDtoMapper) {
        this.itemToDtoMapper = itemToDtoMapper;
    }

    @Override
    public OrderDto mapOrder(Order order) {
        return new OrderDto(order.getId(), mapOrderItems(order.getItems()), order.getTotalSum().longValue());
    }

    @Override
    public List<ItemDto> mapOrderItems(List<OrderItem> orderItems) {
        return orderItems.stream().map(orderItem -> itemToDtoMapper.mapItem(orderItem.getItem(),
                orderItem.getCount())).toList();
    }
}
