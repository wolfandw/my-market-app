package io.github.wolfandw.mymarket.service.impl;

import io.github.wolfandw.mymarket.dto.OrderDto;
import io.github.wolfandw.mymarket.model.Cart;
import io.github.wolfandw.mymarket.model.CartItem;
import io.github.wolfandw.mymarket.model.Order;
import io.github.wolfandw.mymarket.model.OrderItem;
import io.github.wolfandw.mymarket.repository.CartRepository;
import io.github.wolfandw.mymarket.repository.OrderRepository;
import io.github.wolfandw.mymarket.service.ApplicationService;
import io.github.wolfandw.mymarket.service.mapper.OrderToDtoMapper;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Реализация {@link ApplicationService}.
 */
@Service
public class ApplicationServiceImpl implements ApplicationService {
    private static final String ACTION_MINUS = "MINUS";
    private static final String ACTION_PLUS = "PLUS";
    private static final String ACTION_DELETE = "DELETE";

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final OrderToDtoMapper orderTotDtoMapper;

    /**
     * Создает сервис приложения.
     *
     * @param cartRepository      репозиторий корзин
     * @param orderRepository     репозиторий заказов
     * @param orderTotDtoMapper маппер строк заказов
     */
    public ApplicationServiceImpl(CartRepository cartRepository,
                                  OrderRepository orderRepository,
                                  OrderToDtoMapper orderTotDtoMapper) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.orderTotDtoMapper = orderTotDtoMapper;
    }

    @Override
    @Transactional
    public Optional<OrderDto> buy(Long cartId) {
        Optional<Cart> optionalCart = cartRepository.findById(cartId);
        if (optionalCart.isPresent()) {
            Cart cart = optionalCart.get();
            List<CartItem> cartItems = cart.getItems();
            if (!cartItems.isEmpty()) {
                Order order = new Order(cart.getTotal());
                List<OrderItem> orderItems = cartItems.stream().map(cartItem ->
                        createCartItem(order, cartItem)).toList();
                order.setItems(orderItems);

                cart.getItems().clear();
                cart.setTotal(BigDecimal.ZERO);
                cartRepository.save(cart);
                return Optional.ofNullable(orderTotDtoMapper.mapOrder(orderRepository.save(order)));
            }
        };
        return Optional.empty();
    }

    private @NonNull OrderItem createCartItem(Order order, CartItem cartItem) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setItem(cartItem.getItem());
        orderItem.setCount(cartItem.getCount());
        return orderItem;
    }
}
