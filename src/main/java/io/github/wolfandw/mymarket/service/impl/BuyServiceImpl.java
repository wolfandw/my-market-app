//package io.github.wolfandw.mymarket.service.impl;
//
//import io.github.wolfandw.mymarket.dto.OrderDto;
//import io.github.wolfandw.mymarket.model.Order;
//import io.github.wolfandw.mymarket.model.OrderItem;
//import io.github.wolfandw.mymarket.repository.CartRepository;
//import io.github.wolfandw.mymarket.repository.OrderRepository;
//import io.github.wolfandw.mymarket.service.BuyService;
//import io.github.wolfandw.mymarket.service.mapper.OrderToDtoMapper;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Optional;
//
///**
// * Реализация {@link BuyService}.
// */
//@Service
//public class BuyServiceImpl implements BuyService {
//    private final OrderRepository orderRepository;
//    private final CartRepository cartRepository;
//    private final OrderToDtoMapper orderToDtoMapper;
//
//    /**
//     * Создает сервис покупок.
//     *
//     * @param orderRepository репозиторий заказов.
//     * @param cartRepository репозиторий корзин
//     * @param orderToDtoMapper маппер модельного заказа на его DTO-представление.
//     */
//    public BuyServiceImpl(OrderRepository orderRepository,
//                          CartRepository cartRepository,
//                          OrderToDtoMapper orderToDtoMapper) {
//        this.orderRepository = orderRepository;
//        this.cartRepository = cartRepository;
//        this.orderToDtoMapper = orderToDtoMapper;
//    }
//
//    @Override
//    @Transactional
//    public Optional<OrderDto> buy(Long cartId) {
//        return cartRepository.findById(cartId).
//                filter(cart -> !cart.getItems().isEmpty()).
//                map(cart -> {
//                    Order order = new Order();
//                    order.setTotalSum(cart.getTotal());
//                    List<OrderItem> orderItems = cart.getItems().stream().map(cartItem ->
//                            new OrderItem(order, cartItem.getItem(), cartItem.getCount())).toList();
//                    order.setItems(orderItems);
//
//                    cart.getItems().clear();
//                    cart.setTotal(BigDecimal.ZERO);
//                    cartRepository.save(cart);
//
//                    return orderToDtoMapper.mapOrder(orderRepository.save(order));
//                });
//    }
//}
