package io.github.wolfandw.mymarket.service.impl;

import io.github.wolfandw.mymarket.dto.CartDto;
import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.model.Cart;
import io.github.wolfandw.mymarket.model.CartItem;
import io.github.wolfandw.mymarket.model.Item;
import io.github.wolfandw.mymarket.repository.CartItemRepository;
import io.github.wolfandw.mymarket.repository.CartRepository;
import io.github.wolfandw.mymarket.repository.ItemRepository;
import io.github.wolfandw.mymarket.service.CartService;
import io.github.wolfandw.mymarket.service.mapper.CartToDtoMapper;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Реализация {@link CartService}.
 */
@Service("сartService")
public class CartServiceImpl implements CartService {
    private static final String ACTION_MINUS = "MINUS";
    private static final String ACTION_PLUS = "PLUS";
    private static final String ACTION_DELETE = "DELETE";

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ItemRepository itemRepository;
    private final CartToDtoMapper cartToCartDtoMapper;

    /**
     * Создает сервис работы с корзинами.
     *
     * @param cartRepository      репозиторий корзин
     * @param cartItemRepository  репозиторий строк корзин
     * @param itemRepository      репозиторий товаров
     * @param cartToCartDtoMapper маппер строк корзин
     */
    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           ItemRepository itemRepository,
                           CartToDtoMapper cartToCartDtoMapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.itemRepository = itemRepository;
        this.cartToCartDtoMapper = cartToCartDtoMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public CartDto getCart(Long cartId) {
        return cartRepository.findById(cartId).map(cart -> {
            List<CartItem> cartItems = cart.getItems();//cartItemRepository.findAllByCart(cart);
            List<ItemDto> items = cartToCartDtoMapper.mapCartItems(cartItems);
            return new CartDto(items, cart.getTotal().longValue());
        }).orElse(new CartDto(List.of(), 0L));
    }

    @Override
    @Transactional
    public void changeItemCount(Long cartId, Long itemId, String action) {
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            return;
        }
        Item item = optionalItem.get();
        BigDecimal price = item.getPrice();

        Cart cart = cartRepository.findById(cartId).orElseGet(() -> cartRepository.save(new Cart(cartId)));
        BigDecimal total = cart.getTotal();

        CartItem cartItem = cartItemRepository.findByCartAndItemId(cart, itemId).orElseGet(() -> createCartItem(cart, item));
        int count = cartItem.getCount();
        if (ACTION_MINUS.equals(action)) {
            count--;
            total = total.subtract(price);
        } else if (ACTION_PLUS.equals(action)) {
            count++;
            total = total.add(price);
        } else if (ACTION_DELETE.equals(action)) {
            total = total.subtract(price.multiply(BigDecimal.valueOf(count)));
            count = 0;
        }

        if (count <= 0) {
            cart.getItems().remove(cartItem);
            cart.setTotal(total);
            cartRepository.save(cart);
        } else if (count != cartItem.getCount()) {
            cartItem.setCount(count);
            cartItemRepository.save(cartItem);
            cart.setTotal(total);
            cartRepository.save(cart);
        }
    }

    private @NonNull CartItem createCartItem(Cart cart, Item item) {
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setItem(item);
        cart.getItems().add(cartItem);
        return cartItem;
    }
}
