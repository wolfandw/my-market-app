package io.github.wolfandw.mymarket.service.impl;

import io.github.wolfandw.mymarket.model.Cart;
import io.github.wolfandw.mymarket.model.CartItem;
import io.github.wolfandw.mymarket.model.Item;
import io.github.wolfandw.mymarket.repository.*;
import io.github.wolfandw.mymarket.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Реализация {@link CartService}.
 */
@Service
public class CartServiceImpl implements CartService {
    private static final String ACTION_MINUS = "MINUS";
    private static final String ACTION_PLUS = "PLUS";
    private static final String ACTION_DELETE = "DELETE";

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ItemRepository itemRepository;
    //private final CartToDtoMapper cartToDtoMapper;

    /**
     * Создает сервис работы с корзинами.
     *
     * @param cartRepository     репозиторий корзин
     * @param cartItemRepository репозиторий строк корзин
     * @param itemRepository     репозиторий товаров
     *                           // * @param cartToDtoMapper    маппер строк корзин
     */
    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           ItemRepository itemRepository//,
//                           CartToDtoMapper cartToDtoMapper
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.itemRepository = itemRepository;
        //this.cartToDtoMapper = cartToDtoMapper;
    }

    @Transactional(readOnly = true)
    public Mono<Map<Long, Integer>> getCartItemsCount(Long cartId) {
        return cartItemRepository.findAllByCartId(cartId).map(cartItem ->
                Map.entry(cartItem.getItemId(), cartItem.getCount())).collectMap(Map.Entry::getKey, Map.Entry::getValue);
    }

    @Transactional(readOnly = true)
    public Mono<Integer> getCartItemCount(Long cartId, Long itemId) {
        return cartItemRepository.findByCartIdAndItemId(cartId, itemId).map(CartItem::getCount).defaultIfEmpty(0);
    }

//    @Override
//    @Transactional(readOnly = true)
//    public CartDto getCart(Long cartId) {
//        return cartRepository.findById(cartId).map(cart -> {
//            List<ItemDto> items = cartToDtoMapper.mapCartItems(cart.getItems());
//            return new CartDto(items, cart.getTotal().longValue());
//        }).orElse(new CartDto(List.of(), 0L));
//    }

    @Override
    @Transactional
    public Mono<Void> changeItemCount(Long cartId, Long itemId, String action) {
        Mono<Item> itemMono = itemRepository.findById(itemId);
        Mono<Cart> cartMono = cartRepository.findById(cartId).switchIfEmpty(cartRepository.save(new Cart(cartId)));
        Mono<CartItem> cartItemMono = cartItemRepository.findByCartIdAndItemId(cartId, itemId).
                defaultIfEmpty(createCartItem(cartId, itemId));

        return Mono.zip(itemMono, cartMono, cartItemMono).flatMap(tuple -> {
                    Item item = tuple.getT1();
                    Cart cart = tuple.getT2();
                    CartItem cartItem = tuple.getT3();

                    BigDecimal price = item.getPrice();
                    BigDecimal total = cart.getTotal();

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

                    cart.setTotal(total);
                    if (count > 0) {
                        cartItem.setCount(count);
                        return cartItemRepository.save(cartItem).then(cartRepository.save(cart)).then();
                    }
                    return cartItemRepository.delete(cartItem).then(cartRepository.save(cart)).then();
                }
        );
    }

    private CartItem createCartItem(Long cartId, Long itemId) {
        CartItem cartItem = new CartItem();
        cartItem.setCartId(cartId);
        cartItem.setItemId(itemId);
        return cartItem;
    }
}
