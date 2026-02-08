package io.github.wolfandw.mymarket.service.impl;

import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.dto.ItemsPagingDto;
import io.github.wolfandw.mymarket.model.Item;
import io.github.wolfandw.mymarket.repository.CartItemRepository;
import io.github.wolfandw.mymarket.repository.ItemRepository;
import io.github.wolfandw.mymarket.service.ItemService;
import io.github.wolfandw.mymarket.service.mapper.ItemToDtoMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
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

    /**
     * Создает сервис работы с товарами.
     *
     * @param itemRepository  репозиторий товаров
     * @param cartItemRepository репозиторий строк корзин
     * @param itemToDtoMapper маппер товаров на DTO-представление товаров.
     */
    public ItemServiceImpl(ItemRepository itemRepository,
                           CartItemRepository cartItemRepository,
                           ItemToDtoMapper itemToDtoMapper) {
        this.itemRepository = itemRepository;
        this.cartItemRepository = cartItemRepository;
        this.itemToItemDtoMapper = itemToDtoMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ItemDto> getItems(Long cartId, String search, String sort, Integer pageNumber, Integer pageSize) {
        Flux<Item> page = getPage(search, sort, pageNumber, pageSize);
        return page.map(item -> cartItemRepository.findByCartIdAndItemId(cartId, item.getId()).
                map(ci -> itemToItemDtoMapper.mapItem(item, ci.getCount())).
                switchIfEmpty(Mono.just(itemToItemDtoMapper.mapItem(item, 0)))).
                flatMap(itemDto -> itemDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ItemsPagingDto> getItemsPaging(String search, Integer pageNumber, Integer pageSize) {
        Mono<Long> itemsCount = search == null || search.isEmpty() ? itemRepository.count() :
                itemRepository.countByTitleContainingOrDescriptionContainingAllIgnoreCase(search, search);
        return itemsCount.map(count -> {
            int pagingPageNumber = pageNumber == null ? PAGE_NUMBER_DEFAULT : pageNumber;
            int pagingPageSize = pageSize == null ? PAGE_SIZE_DEFAULT : pageSize;
            return new ItemsPagingDto(pagingPageSize, pagingPageNumber, pagingPageNumber > 1,
                    (long) pagingPageNumber * pagingPageSize < count);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ItemDto> getItem(Long cartId, Long id) {
        return itemRepository.findById(id).map(item -> cartItemRepository.findByCartIdAndItemId(cartId, item.getId()).
                map(ci -> itemToItemDtoMapper.mapItem(item, ci.getCount())).
                switchIfEmpty(Mono.just(itemToItemDtoMapper.mapItem(item, 0)))).flatMap(itemDto -> itemDto);
    }

    @Override
    @Transactional
    public Mono<ItemDto> createItem(String title, String description, BigDecimal price, MultipartFile imageFile) {
        //entityImageService.updateEntityImage(newItemDto.id(), imageFile);
        Item item = new Item();
        item.setTitle(title);
        item.setDescription(description);
        item.setPrice(price);
        return itemRepository.save(item).map(newItem -> itemToItemDtoMapper.mapItem(newItem, 0));
    }

    private Flux<Item> getPage(String search, String sort, Integer pageNumber, Integer pageSize) {
        pageNumber = pageNumber == null ? PAGE_NUMBER_DEFAULT : pageNumber;
        pageSize = pageSize == null ? PAGE_SIZE_DEFAULT : pageSize;
        sort = sort == null ? SORT_DEFAULT : sort;
        Pageable pageable = PageRequest.of(pageNumber - PAGE_NUMBER_DELTA, pageSize, SORT_BY.getOrDefault(sort, Sort.unsorted()));
        return getPage(search, pageable);
    }

    private Flux<Item> getPage(String search, Pageable pageable) {
        return search == null || search.isEmpty() ? itemRepository.findAllBy(pageable) :
                itemRepository.findByTitleContainingOrDescriptionContainingAllIgnoreCase(search, search, pageable);
    }
}
