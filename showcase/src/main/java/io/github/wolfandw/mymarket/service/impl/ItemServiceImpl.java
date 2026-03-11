package io.github.wolfandw.mymarket.service.impl;

import io.github.wolfandw.mymarket.cache.ItemCache;
import io.github.wolfandw.mymarket.cache.ItemsCache;
import io.github.wolfandw.mymarket.cache.ItemsCountCache;
import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.dto.ItemsPagingDto;
import io.github.wolfandw.mymarket.model.Cart;
import io.github.wolfandw.mymarket.model.Item;
import io.github.wolfandw.mymarket.repository.CartItemRepository;
import io.github.wolfandw.mymarket.repository.CartRepository;
import io.github.wolfandw.mymarket.repository.ItemRepository;
import io.github.wolfandw.mymarket.service.ItemService;
import io.github.wolfandw.mymarket.service.UserService;
import io.github.wolfandw.mymarket.service.mapper.ItemToDtoMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Реализация {@link ItemService}.
 */
@Service
public class ItemServiceImpl implements ItemService {
    private static final int PAGE_NUMBER_DEFAULT = 1;
    private static final int PAGE_NUMBER_DELTA = 1;
    private static final int PAGE_SIZE_DEFAULT = 5;

    private static final String SORT_DEFAULT = "NO";
    private static final String SORT_ALPHA = "ALPHA";
    private static final String SORT_PRICE = "PRICE";

    private static final String PRICE_COLUMN = "price";
    private static final String TITLE_COLUMN = "title";
    private static final Map<String, Sort> SORT_BY = Map.of(SORT_ALPHA, Sort.by(TITLE_COLUMN),
            SORT_PRICE, Sort.by(PRICE_COLUMN));

    private final ItemRepository itemRepository;
    private final CartItemRepository cartItemRepository;
    private final ItemToDtoMapper itemToItemDtoMapper;
    private final ItemsCache itemsCache;
    private final ItemsCountCache itemsCountCache;
    private final ItemCache itemCache;
    private final UserService userService;
    private final CartRepository cartRepository;

    /**
     * Создает сервис работы с товарами.
     *
     * @param itemRepository     репозиторий товаров
     * @param cartItemRepository репозиторий строк корзин
     * @param itemToDtoMapper    маппер товаров на DTO-представление товаров.
     * @param itemsCache         кэш товаров
     * @param itemsCountCache    кэш количества товаров
     * @param itemCache          кэш товаров
     * @param userService        сервис пользователей
     * @param cartRepository     репозиторий корзин
     */
    public ItemServiceImpl(ItemRepository itemRepository,
                           CartItemRepository cartItemRepository,
                           ItemToDtoMapper itemToDtoMapper,
                           ItemsCache itemsCache,
                           ItemsCountCache itemsCountCache,
                           ItemCache itemCache,
                           UserService userService,
                           CartRepository cartRepository) {
        this.itemRepository = itemRepository;
        this.cartItemRepository = cartItemRepository;
        this.itemToItemDtoMapper = itemToDtoMapper;
        this.itemsCache = itemsCache;
        this.itemsCountCache = itemsCountCache;
        this.itemCache = itemCache;
        this.userService = userService;
        this.cartRepository = cartRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ItemDto> getItems(String search, String sort, Integer pageNumber, Integer pageSize) {
        Flux<Item> page = getPage(search, sort, pageNumber, pageSize);
        Mono<Long> userIdMono = userService.getCurrentUserId();
        Mono<Cart> cartMono = userIdMono.flatMap(cartRepository::findFirstByUserId);
        return page.flatMap(item -> cartMono.flatMap(cart ->
                        cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId()).
                        map(ci -> itemToItemDtoMapper.mapItem(item, ci.getCount()))
                ).defaultIfEmpty(itemToItemDtoMapper.mapItem(item)));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ItemsPagingDto> getItemsPaging(String search, Integer pageNumber, Integer pageSize) {
        Mono<Long> cachedItemsCount = itemsCountCache.getItemsCount(getSearch(search),
                getPageNumber(pageNumber), getPageSize(pageSize));
        Mono<Long> databaseItemsCount = getSearch(search).isEmpty() ?
                itemRepository.count() :
                itemRepository.countByTitleContainingOrDescriptionContainingAllIgnoreCase(getSearch(search), getSearch(search));

        return cachedItemsCount.switchIfEmpty(Mono.defer(() -> itemsCountCache.cache(getSearch(search), getPageNumber(pageNumber),
                        getPageSize(pageSize), databaseItemsCount))).
                map(count -> {
                    return new ItemsPagingDto(getPageSize(pageSize),
                            getPageNumber(pageNumber),
                            getPageNumber(pageNumber) > 1,
                            (long) getPageNumber(pageNumber) * getPageSize(pageSize) < count);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ItemDto> getItem(Long id) {
        Mono<Item> cachedItem = itemCache.getItem(id);
        Mono<Item> databaseItem = itemRepository.findById(id);
        Mono<Long> userIdMono = userService.getCurrentUserId();
        Mono<Cart> cartMono = userIdMono.flatMap(cartRepository::findFirstByUserId);
        return cachedItem.switchIfEmpty(Mono.defer(() -> itemCache.cache(databaseItem))).
                flatMap(item -> cartMono.flatMap(cart ->
                        cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId()).
                        map(ci -> itemToItemDtoMapper.mapItem(item, ci.getCount()))
                ).defaultIfEmpty(itemToItemDtoMapper.mapItem(item)));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ItemDto> createItem(String title, String description, BigDecimal price) {
        Item item = new Item();
        item.setTitle(title);
        item.setDescription(description);
        item.setPrice(price);
        return itemCache.cache(itemRepository.save(item)).flatMap(databaseItem ->
                itemsCache.clear().then(itemsCountCache.clear()).thenReturn(itemToItemDtoMapper.mapItem(databaseItem))
        );
    }

    private Flux<Item> getPage(String search, String sort, Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(getPageNumber(pageNumber) - PAGE_NUMBER_DELTA,
                getPageSize(pageSize),
                SORT_BY.getOrDefault(getSort(sort), Sort.unsorted()));

        Flux<Item> cachedItems = itemsCache.getItems(getSearch(search), getSort(sort),
                getPageNumber(pageNumber), getPageSize(pageSize));
        Flux<Item> databaseItems = getSearch(search).isEmpty() ?
                itemRepository.findAllBy(pageable) :
                itemRepository.findByTitleContainingOrDescriptionContainingAllIgnoreCase(search, search, pageable);

        return cachedItems.switchIfEmpty(Flux.defer(() -> itemsCache.cache(getSearch(search), getSort(sort), getPageNumber(pageNumber),
                getPageSize(pageSize), databaseItems)));
    }

    private String getSearch(String search) {
        return search == null ? "" : search;
    }

    private String getSort(String sort) {
        return sort == null ? SORT_DEFAULT : sort;
    }

    private Integer getPageNumber(Integer pageNumber) {
        return pageNumber == null ? PAGE_NUMBER_DEFAULT : pageNumber;
    }

    private Integer getPageSize(Integer pageSize) {
        return pageSize == null ? PAGE_SIZE_DEFAULT : pageSize;
    }
}
