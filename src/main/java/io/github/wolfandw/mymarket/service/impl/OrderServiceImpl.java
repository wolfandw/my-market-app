package io.github.wolfandw.mymarket.service.impl;

import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.dto.OrderDto;
import io.github.wolfandw.mymarket.repository.ItemRepository;
import io.github.wolfandw.mymarket.repository.OrderItemRepository;
import io.github.wolfandw.mymarket.repository.OrderRepository;
import io.github.wolfandw.mymarket.service.OrderService;
import io.github.wolfandw.mymarket.service.mapper.ItemToDtoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Реализация {@link OrderService}.
 */
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;
    private final ItemToDtoMapper itemToItemDtoMapper;

    /**
     * Создает сервис работы с заказами товаров.
     *
     * @param orderRepository     репозиторий заказов
     * @param orderItemRepository репозиторий заказов
     * @param itemRepository      репозиторий товаров
     * @param itemToItemDtoMapper маппер строк заказов
     */
    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            ItemRepository itemRepository,
                            ItemToDtoMapper itemToItemDtoMapper) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.itemRepository = itemRepository;
        this.itemToItemDtoMapper = itemToItemDtoMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<OrderDto> getOrders() {
        return orderRepository.findAll().map(order ->
                        getOrderItems(order.getId()).collectList().
                                map(orderItems -> new OrderDto(order.getId(), orderItems, order.getTotalSum().longValue()))).
                flatMap(orderDto -> orderDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<OrderDto> getOrder(Long id, boolean newOrder) {
        return orderRepository.findById(id).map(order ->
                new OrderDto(order.getId(), List.of(), order.getTotalSum().longValue()));
    }

    @Override
    public Flux<ItemDto> getOrderItems(Long orderId) {
        return orderItemRepository.findAllByOrderId(orderId).map(orderItem ->
                        itemRepository.findById(orderItem.getItemId()).
                                map(item -> itemToItemDtoMapper.mapItem(item, orderItem.getCount())).
                                switchIfEmpty(Mono.just(new ItemDto(-1L, "", "", 0L, 0)))).
                flatMap(itemDto -> itemDto);
    }
}
