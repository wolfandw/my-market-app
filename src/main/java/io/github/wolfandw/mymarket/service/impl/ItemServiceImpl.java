package io.github.wolfandw.mymarket.service.impl;

import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.model.Item;
import io.github.wolfandw.mymarket.repository.ItemRepository;
import io.github.wolfandw.mymarket.service.ItemService;
import io.github.wolfandw.mymarket.service.mapper.ItemToDtoMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    private final ItemToDtoMapper itemToItemDtoMapper;

    /**
     * Создает сервис работы с товарами.
     *
     * @param itemRepository     репозиторий товаров
     * @param itemToDtoMapper    маппер товаров на DTO-представление товаров.
     */
    public ItemServiceImpl(ItemRepository itemRepository,
                           ItemToDtoMapper itemToDtoMapper) {
        this.itemRepository = itemRepository;
        this.itemToItemDtoMapper = itemToDtoMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ItemDto> getItems(Long cartId, String search, String sort, Integer pageNumber, Integer pageSize) {
        int finalPageNumber = pageNumber == null ? PAGE_NUMBER_DEFAULT : pageNumber;
        int finalPageSize = pageSize == null ? PAGE_SIZE_DEFAULT : pageSize;
        Sort sortBy = SORT_BY.getOrDefault(sort == null ? SORT_DEFAULT : sort, Sort.unsorted());

        Flux<Item> itemsAll = search == null || search.isEmpty() ? itemRepository.findAll(sortBy) :
                itemRepository.
                        findByTitleContainingOrDescriptionContainingAllIgnoreCase(search, search, sortBy);

        return itemsAll.skip((long) (finalPageNumber - PAGE_NUMBER_DELTA) * finalPageSize).take(finalPageSize).
                map(itemToItemDtoMapper::mapItem);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> getItemsCount(String search) {
        return search == null || search.isEmpty() ? itemRepository.count() :
                itemRepository.countByTitleContainingOrDescriptionContainingAllIgnoreCase(search, search);
    }

//    @Override
//    @Transactional(readOnly = true)
//    public Optional<ItemDto> getItem(Long cartId, Long id) {
//        return itemRepository.findById(id).map(item -> {
//            Integer count = cartRepository.findById(cartId).
//                    map(c -> cartItemRepository.findByCartAndItemId(c, id).
//                            map(CartItem::getCount).orElse(0)).orElse(0);
//            return itemToItemDtoMapper.mapItem(item, count);
//        });
//    }
//
//    @Override
//    @Transactional
//    public ItemDto createItem(String title, String description, BigDecimal price) {
//        Item item = new Item();
//        item.setTitle(title);
//        item.setDescription(description);
//        item.setPrice(price);
//        return itemToItemDtoMapper.mapItem(itemRepository.save(item), 0);
//    }
}
