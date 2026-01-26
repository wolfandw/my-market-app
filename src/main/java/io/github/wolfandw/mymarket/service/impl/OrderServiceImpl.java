package io.github.wolfandw.mymarket.service.impl;

import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.dto.OrderDto;
import io.github.wolfandw.mymarket.model.OrderItem;
import io.github.wolfandw.mymarket.repository.OrderItemRepository;
import io.github.wolfandw.mymarket.repository.OrderRepository;
import io.github.wolfandw.mymarket.service.OrderService;
import io.github.wolfandw.mymarket.service.mapper.OrderToDtoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Реализация {@link OrderService}.
 */
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderToDtoMapper orderToOrderDtoMapper;

    /**
     * Создает сервис работы с заказами товаров.
     *
     * @param orderRepository      репозиторий заказов
     * @param orderItemRepository  репозиторий строк заказов
     * @param orderToOrderDtoMapper  маппер строк заказов
     */
    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            OrderToDtoMapper orderToOrderDtoMapper) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderToOrderDtoMapper = orderToOrderDtoMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getOrders() {
        return orderRepository.findAll().stream().map(order -> {
            List<OrderItem> orderItems = orderItemRepository.findAllByOrder(order);
            List<ItemDto> items = orderToOrderDtoMapper.mapOrderItems(orderItems);
            return new OrderDto(order.getId(), items, order.getTotalSum().longValue());
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderDto> getOrder(Long id, boolean newOrder) {
        return orderRepository.findById(id).map(order -> {
            List<OrderItem> orderItems = orderItemRepository.findAllByOrder(order);
            List<ItemDto> items = orderToOrderDtoMapper.mapOrderItems(orderItems);
            return new OrderDto(order.getId(), items, order.getTotalSum().longValue());
        });
    }
}
