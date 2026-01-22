package io.github.wolfandw.mymarket.service.impl;

import io.github.wolfandw.mymarket.dto.CartDto;
import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.model.Cart;
import io.github.wolfandw.mymarket.model.CartItem;
import io.github.wolfandw.mymarket.repository.CartItemRepository;
import io.github.wolfandw.mymarket.repository.CartRepository;
import io.github.wolfandw.mymarket.service.CartService;
import io.github.wolfandw.mymarket.service.ItemService;
import io.github.wolfandw.mymarket.service.mapper.CartItemToDtoMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Реализация {@link CartService}.
 */
@Service("сartService")
public class CartServiceImpl implements CartService {
    /*
     * Идентификатор корзины по-умолчанию.
     */
    private static final Long DEFAULT_CART_ID = 1L;

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartItemToDtoMapper cartToCartDtoMapper;

    private Cart defaultCart = null;

    /**
     * Создает сервис работы с товарами.
     *
     * @param cartRepository      репозиторий корзин
     * @param cartItemRepository  репозиторий строк корзин
     * @param cartToCartDtoMapper  маппер строк корзин
     */
    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository, CartItemToDtoMapper cartToCartDtoMapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.cartToCartDtoMapper = cartToCartDtoMapper;
    }

    @Override
    public CartDto getCart() {
        Cart cart = getDefaultCart();
        List<CartItem> cartItems = cartItemRepository.findAllByCart(cart);
        List<ItemDto> items = cartToCartDtoMapper.mapCartItems(cartItems);
        return new CartDto(items, cart.getTotal().longValue());
    }

    @Override
    public Cart getDefaultCart() {
        if (defaultCart == null) {
            Optional<Cart> cart = cartRepository.findById(DEFAULT_CART_ID);
            defaultCart = cart.orElse(cartRepository.save(new Cart()));
        }
        return defaultCart;
    }
}
