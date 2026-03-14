package io.github.wolfandw.mymarket.service.impl;

import io.github.wolfandw.mymarket.dto.OrderDto;
import io.github.wolfandw.mymarket.model.Cart;
import io.github.wolfandw.mymarket.model.CartItem;
import io.github.wolfandw.mymarket.model.Order;
import io.github.wolfandw.mymarket.model.OrderItem;
import io.github.wolfandw.mymarket.repository.CartItemRepository;
import io.github.wolfandw.mymarket.repository.CartRepository;
import io.github.wolfandw.mymarket.repository.OrderItemRepository;
import io.github.wolfandw.mymarket.repository.OrderRepository;
import io.github.wolfandw.mymarket.service.BuyService;
import io.github.wolfandw.mymarket.service.PaymentsService;
import io.github.wolfandw.mymarket.service.UserService;
import io.github.wolfandw.payment.client.domain.PaymentDto;
import org.jspecify.annotations.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

/**
 * Реализация {@link BuyService}.
 */
@Service
public class BuyServiceImpl implements BuyService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final PaymentsService paymentsService;
    private final UserService userService;

    /**
     * Создает сервис покупок.
     *
     * @param orderRepository     репозиторий заказов.
     * @param orderItemRepository репозиторий заказов.
     * @param cartRepository      репозиторий корзин
     * @param paymentsService     сервис платежей
     * @param userService         сервис пользователей
     */
    public BuyServiceImpl(OrderRepository orderRepository,
                          OrderItemRepository orderItemRepository,
                          CartRepository cartRepository,
                          CartItemRepository cartItemRepository,
                          PaymentsService paymentsService,
                          UserService userService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.paymentsService = paymentsService;
        this.userService = userService;
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('USER')")
    public Mono<OrderDto> buy() {
        Mono<Long> userIdMono = userService.getCurrentUserId();
        Mono<Cart> cartMono = userIdMono.flatMap(cartRepository::findFirstByUserId);
        return cartMono.
                filter(cart -> !cart.getTotal().equals(BigDecimal.ZERO)).
                flatMap(cart -> {
                    PaymentDto paymentDto = new PaymentDto();
                    paymentDto.setId(cart.getUserId());
                    paymentDto.setPayment(cart.getTotal());
                    return paymentsService.makeUserPayment(paymentDto).flatMap(balanceDto ->
                            writeToRepositories(cart, cartItemRepository.findAllByCartId(cart.getId())));
                });
    }

    private @NonNull Mono<OrderDto> writeToRepositories(Cart cart, Flux<CartItem> cartItemsFlux) {
        Order order = new Order();
        order.setUserId(cart.getUserId());
        order.setTotalSum(cart.getTotal().add(BigDecimal.valueOf(0, 0)));
        cart.setTotal(BigDecimal.ZERO);
        return cartRepository.save(cart).then(orderRepository.save(order)).flatMap(newOrder ->
                orderItemRepository.saveAll(cartItemsFlux.map(cartItem ->
                                new OrderItem(newOrder.getId(), cartItem.getItemId(), cartItem.getCount()))).
                        then(cartItemRepository.deleteAllByCartId(cart.getId())).
                        thenReturn(new OrderDto(newOrder.getId(), List.of(), newOrder.getTotalSum().longValue())));
    }
}
