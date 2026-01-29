package io.github.wolfandw.mymarket.service.impl;

import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.dto.OrderDto;
import io.github.wolfandw.mymarket.model.Order;
import io.github.wolfandw.mymarket.model.OrderItem;
import io.github.wolfandw.mymarket.repository.CartRepository;
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
    private final CartRepository cartRepository;
    private final OrderToDtoMapper orderToDtoMapper;

    /**
     * Создает сервис работы с заказами товаров.
     *
     * @param orderRepository      репозиторий заказов
     * @param cartRepository      репозиторий корзин
     * @param orderToDtoMapper  маппер строк заказов
     */
    public OrderServiceImpl(OrderRepository orderRepository,
                            CartRepository cartRepository,
                            OrderToDtoMapper orderToDtoMapper) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.orderToDtoMapper = orderToDtoMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getOrders() {
        return orderRepository.findAll().stream().map(order -> {
            List<ItemDto> items = orderToDtoMapper.mapOrderItems(order.getItems());
            return new OrderDto(order.getId(), items, order.getTotalSum().longValue());
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderDto> getOrder(Long id, boolean newOrder) {
        return orderRepository.findById(id).map(order -> {
            List<ItemDto> items = orderToDtoMapper.mapOrderItems(order.getItems());
            return new OrderDto(order.getId(), items, order.getTotalSum().longValue());
        });
    }

    @Override
    public Optional<OrderDto> createOrderByCart(Long cartId) {
        return cartRepository.findById(cartId).
                filter(cart -> !cart.getItems().isEmpty()).
                map(cart -> {
            Order order = new Order();
            order.setTotalSum(cart.getTotal());
            List<OrderItem> orderItems = cart.getItems().stream().map(cartItem ->
                    new OrderItem(order, cartItem.getItem(), cartItem.getCount())).toList();
            order.setItems(orderItems);
            return orderToDtoMapper.mapOrder(orderRepository.save(order));
        });
    }
}
