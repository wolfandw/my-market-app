package io.github.wolfandw.mymarket.service.impl;

import io.github.wolfandw.mymarket.cache.ItemCache;
import io.github.wolfandw.mymarket.dto.CartDto;
import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.model.Cart;
import io.github.wolfandw.mymarket.model.CartItem;
import io.github.wolfandw.mymarket.model.Item;
import io.github.wolfandw.mymarket.repository.CartItemRepository;
import io.github.wolfandw.mymarket.repository.CartRepository;
import io.github.wolfandw.mymarket.repository.ItemRepository;
import io.github.wolfandw.mymarket.service.CartService;
import io.github.wolfandw.mymarket.service.UserService;
import io.github.wolfandw.mymarket.service.mapper.ItemToDtoMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

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
    private final ItemCache itemCache;
    private final UserService userService;

    /**
     * Создает сервис работы с корзинами.
     *
     * @param cartRepository      репозиторий корзин
     * @param cartItemRepository  репозиторий строк корзин
     * @param itemRepository      репозиторий товаров
     * @param itemToItemDtoMapper маппер товаров
     * @param itemCache           кэш товаров
     * @param userService         сервис пользователей
     */
    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           ItemRepository itemRepository,
                           ItemToDtoMapper itemToItemDtoMapper,
                           ItemCache itemCache,
                           UserService userService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.itemRepository = itemRepository;
        this.itemToItemDtoMapper = itemToItemDtoMapper;
        this.itemCache = itemCache;
        this.userService = userService;
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public Flux<ItemDto> getCartItems(Long cartId) {
        Mono<Cart> cartMono = cartRepository.findById(cartId);
        return getUserCartItems(cartMono);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<CartDto> getCart(Long cartId) {
        Mono<Cart> cartMono = cartRepository.findById(cartId);
        return getCartDto(cartMono);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<Void> changeItemCount(Long cartId, Long itemId, String action) {
        Mono<Cart> cartMono = cartRepository.findById(cartId);
        return changeItemCount(cartMono, itemId, action);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('USER')")
    public Flux<ItemDto> getUserCartItems() {
        Mono<Long> userIdMono = userService.getCurrentUserId();
        Mono<Cart> cartMono = userIdMono.flatMap(cartRepository::findFirstByUserId);
        return getUserCartItems(cartMono);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('USER')")
    public Mono<CartDto> getUserCart() {
        Mono<Long> userIdMono = userService.getCurrentUserId();
        Mono<Cart> cartMono = userIdMono.flatMap(cartRepository::findFirstByUserId);
        return getCartDto(cartMono);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('USER')")
    public Mono<Void> changeUserItemCount(Long itemId, String action) {
        Mono<Long> userIdMono = userService.getCurrentUserId().filter(currentUserId -> currentUserId > 0);
        Mono<Cart> cartMono = userIdMono.flatMap(userId ->
                cartRepository.findFirstByUserId(userId).
                        switchIfEmpty(Mono.defer(() -> cartRepository.save(new Cart(userId)))));
        return changeItemCount(cartMono, itemId, action);
    }

    private Flux<ItemDto> getUserCartItems(Mono<Cart> cartMono) {
        return cartMono.flatMapMany(cart -> cartItemRepository.findAllByCartId(cart.getId())).
                flatMap(cartItem -> itemCache.getItem(cartItem.getItemId()).
                        switchIfEmpty(Mono.defer(() -> itemCache.cache(itemRepository.findById(cartItem.getItemId())))).
                        map(item -> itemToItemDtoMapper.mapItem(item, cartItem.getCount())).
                        switchIfEmpty(Mono.defer(() -> Mono.just(new ItemDto(-1L, "", "", 0L, 0)))));
    }

    private Mono<CartDto> getCartDto(Mono<Cart> cartMono) {
        return cartMono.map(cart -> new CartDto(cart.getId(), cart.getUserId(), cart.getTotal().longValue()));
    }

    private Mono<Void> changeItemCount(Mono<Cart> cartMono, Long itemId, String action) {
        Mono<Item> itemMono = itemRepository.findById(itemId);
        Mono<CartItem> cartItemMono = cartMono.flatMap(cart -> cartItemRepository.findByCartIdAndItemId(cart.getId(), itemId).
                switchIfEmpty(Mono.defer(() -> createCartItem(cart.getId(), itemId))));

        return Mono.zip(itemMono, cartMono, cartItemMono).flatMap(tuple -> {
                    Item item = tuple.getT1();
                    Cart cart = tuple.getT2();
                    CartItem cartItem = tuple.getT3();

                    BigDecimal price = item.getPrice().add(BigDecimal.valueOf(0, 0));
                    BigDecimal total = cart.getTotal().add(BigDecimal.valueOf(0, 0));

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

                    Mono<Cart> savedCart = cartRepository.save(cart);
                    if (count > 0) {
                        return savedCart.then(cartItemRepository.save(cartItem)).then();
                    } else if (cartItem.getId() != null) {
                        return savedCart.then(cartItemRepository.delete(cartItem));
                    }
                    return savedCart.then();
                }
        );
    }

    private Mono<CartItem> createCartItem(Long cartId, Long itemId) {
        CartItem cartItem = new CartItem();
        cartItem.setCartId(cartId);
        cartItem.setItemId(itemId);
        return Mono.just(cartItem);
    }
}
