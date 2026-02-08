package io.github.wolfandw.mymarket.service.impl;

import io.github.wolfandw.mymarket.dto.OrderDto;
import io.github.wolfandw.mymarket.model.CartItem;
import io.github.wolfandw.mymarket.model.Order;
import io.github.wolfandw.mymarket.model.OrderItem;
import io.github.wolfandw.mymarket.repository.CartItemRepository;
import io.github.wolfandw.mymarket.repository.CartRepository;
import io.github.wolfandw.mymarket.repository.OrderItemRepository;
import io.github.wolfandw.mymarket.repository.OrderRepository;
import io.github.wolfandw.mymarket.service.BuyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

/**
 * Реализация {@link BuyService}.
 */
@Service
public class BuyServiceImpl implements BuyService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    /**
     * Создает сервис покупок.
     *
     * @param orderRepository репозиторий заказов.
     * @param orderItemRepository репозиторий заказов.
     * @param cartRepository репозиторий корзин
     */
    public BuyServiceImpl(OrderRepository orderRepository,
                          OrderItemRepository orderItemRepository,
                          CartRepository cartRepository,
                          CartItemRepository cartItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    @Transactional
    public Mono<OrderDto> buy(Long cartId) {
        Flux<CartItem> cartItemsFlux = cartItemRepository.findAllByCartId(cartId);
        return cartRepository.findById(cartId).
                filter(cart -> !cart.getTotal().equals(BigDecimal.ZERO)).
                map(cart -> {
                    Order order = new Order();
                    order.setTotalSum(cart.getTotal());
                    cart.setTotal(BigDecimal.ZERO);

                    return cartRepository.save(cart).then(orderRepository.save(order)).map(newOrder ->
                                    orderItemRepository.saveAll(cartItemsFlux.map(cartItem ->
                                    new OrderItem(newOrder.getId(), cartItem.getItemId(), cartItem.getCount()))).
                                    then(cartItemRepository.deleteAllByCartId(cartId)).
                                    thenReturn(new OrderDto(newOrder.getId(), List.of(), newOrder.getTotalSum().longValue())))
                            .flatMap(Function.identity());
                }).flatMap(Function.identity());
    }
}
