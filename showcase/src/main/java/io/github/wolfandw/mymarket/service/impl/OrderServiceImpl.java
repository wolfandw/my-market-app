package io.github.wolfandw.mymarket.service.impl;

import io.github.wolfandw.mymarket.cache.ItemCache;
import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.dto.OrderDto;
import io.github.wolfandw.mymarket.model.Order;
import io.github.wolfandw.mymarket.repository.ItemRepository;
import io.github.wolfandw.mymarket.repository.OrderItemRepository;
import io.github.wolfandw.mymarket.repository.OrderRepository;
import io.github.wolfandw.mymarket.service.OrderService;
import io.github.wolfandw.mymarket.service.UserService;
import io.github.wolfandw.mymarket.service.mapper.ItemToDtoMapper;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final ItemCache itemCache;
    private final UserService userService;

    /**
     * Создает сервис работы с заказами товаров.
     *
     * @param orderRepository     репозиторий заказов
     * @param orderItemRepository репозиторий заказов
     * @param itemRepository      репозиторий товаров
     * @param itemToItemDtoMapper маппер строк заказов
     * @param itemCache           кэш товаров
     * @param userService        сервис пользователей
     */
    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            ItemRepository itemRepository,
                            ItemToDtoMapper itemToItemDtoMapper,
                            ItemCache itemCache,
                            UserService userService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.itemRepository = itemRepository;
        this.itemToItemDtoMapper = itemToItemDtoMapper;
        this.itemCache = itemCache;
        this.userService = userService;
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public Flux<OrderDto> getOrders() {
        return getOrders(orderRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<OrderDto> getOrder(Long orderId, boolean newOrder) {
        Mono<Order> orderMono = orderRepository.findById(orderId);
        return getOrder(orderMono);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public Flux<ItemDto> getOrderItems(Long orderId) {
        Mono<Order> orderMono = orderRepository.findById(orderId);
        return getOrderItems(orderMono);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('USER')")
    public Flux<OrderDto> getUserOrders() {
        Mono<Long> userIdMono = userService.getCurrentUserId();
        Flux<Order> ordersFlux = userIdMono.flatMapMany(orderRepository::findAllByUserId);
        return getOrders(ordersFlux);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('USER')")
    public Mono<OrderDto> getUserOrder(Long orderId, boolean newOrder) {
        Mono<Long> userIdMono = userService.getCurrentUserId();
        Mono<Order> orderMono = userIdMono.flatMap(userId -> orderRepository.findByIdAndUserId(orderId, userId));
        return getOrder(orderMono);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('USER')")
    public Flux<ItemDto> getUserOrderItems(Long orderId) {
        Mono<Long> userIdMono = userService.getCurrentUserId();
        Mono<Order> orderMono = userIdMono.flatMap(userId -> orderRepository.findByIdAndUserId(orderId, userId));
        return getOrderItems(orderMono);
    }

    private Flux<OrderDto> getOrders(Flux<Order> ordersFlux) {
        return ordersFlux.map(order -> getOrderItems(order).collectList().
                        map(orderItems -> new OrderDto(order.getId(), orderItems, order.getTotalSum().longValue()))).
                flatMap(orderDto -> orderDto);
    }

    private Mono<OrderDto> getOrder(Mono<Order> orderMono) {
        return orderMono.map(order ->
                new OrderDto(order.getId(), List.of(), order.getTotalSum().longValue()));
    }

    private Flux<ItemDto> getOrderItems(Mono<Order> orderMono) {
        return orderMono.flatMapMany(this::getOrderItems);
    }

    private Flux<ItemDto> getOrderItems(Order order) {
        return orderItemRepository.findAllByOrderId(order.getId()).map(orderItem ->
                        itemCache.getItem(orderItem.getItemId()).
                                switchIfEmpty(Mono.defer(() -> itemCache.cache(itemRepository.findById(orderItem.getItemId())))).
                                map(item -> itemToItemDtoMapper.mapItem(item, orderItem.getCount())).
                                switchIfEmpty(Mono.defer(() -> Mono.just(new ItemDto(-1L, "", "", 0L, 0))))).
                flatMap(itemDto -> itemDto);
    }
}
