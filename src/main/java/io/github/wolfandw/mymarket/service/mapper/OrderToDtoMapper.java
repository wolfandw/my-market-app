package io.github.wolfandw.mymarket.service.mapper;

import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.dto.OrderDto;
import io.github.wolfandw.mymarket.model.Order;
import io.github.wolfandw.mymarket.model.OrderItem;

import java.util.List;

/**
 * Маппер модели заказа товаров в DTO-представление.
 */
public interface OrderToDtoMapper {
    /**
     * Преобразует модельный заказ в заказ DTO.
     *
     * @param order модельный заказ
     * @return заказ DTO
     */
    OrderDto mapOrder(Order order);

    /**
     * Преобразует список строк заказа товаров в DTO-товары с количеством.
     *
     * @param orderItems строки заказа товаров
     * @return DTO-товары с количеством
     */
    List<ItemDto> mapOrderItems(List<OrderItem> orderItems);
}
