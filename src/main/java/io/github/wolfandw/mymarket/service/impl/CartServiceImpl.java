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
import io.github.wolfandw.mymarket.service.mapper.ItemToDtoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.function.Function;

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
    private final ItemToDtoMapper itemToItemDtoMapper;

    /**
     * Создает сервис работы с корзинами.
     *
     * @param cartRepository     репозиторий корзин
     * @param cartItemRepository репозиторий строк корзин
     * @param itemRepository     репозиторий товаров
     * @param itemToItemDtoMapper    маппер товаров
     */
    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           ItemRepository itemRepository,
                           ItemToDtoMapper itemToItemDtoMapper
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.itemRepository = itemRepository;
        this.itemToItemDtoMapper = itemToItemDtoMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ItemDto> getCartItems(Long cartId) {
        return cartItemRepository.findAllByCartId(cartId).map(cartItem ->
                itemRepository.findById(cartItem.getItemId()).
                        map(item -> itemToItemDtoMapper.mapItem(item, cartItem.getCount())).
                        switchIfEmpty(Mono.just(new ItemDto(-1L, "", "", 0L, 0)))).
                        flatMap(Function.identity());
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<CartDto> getCart(Long cartId) {
        return cartRepository.findById(cartId).map(cart -> new CartDto(cart.getId(), cart.getTotal().longValue()));
    }

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
                    cartItem.setCount(count);

                    if (count > 0) {
                        return cartItemRepository.save(cartItem).then(cartRepository.save(cart)).then();
                    } else if (cartItem.getId() != null) {
                        return cartItemRepository.delete(cartItem).then(cartRepository.save(cart)).then();
                    }
                    return cartRepository.save(cart).then();
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
