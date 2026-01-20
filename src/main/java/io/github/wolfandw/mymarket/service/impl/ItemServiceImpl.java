package io.github.wolfandw.mymarket.service.impl;

import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.dto.ItemsPageDto;
import io.github.wolfandw.mymarket.dto.ItemsPagingDto;
import io.github.wolfandw.mymarket.model.Item;
import io.github.wolfandw.mymarket.repository.ItemRepository;
import io.github.wolfandw.mymarket.service.ItemService;
import io.github.wolfandw.mymarket.service.mapper.ItemsToItemDtoTriplesMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Реализация {@link ItemService}.
 */
@Service("itemService")
public class ItemServiceImpl implements ItemService {
    private static final int PAGE_NUMBER_DEFAULT = 1;
    private static final int PAGE_SIZE_DEFAULT = 5;

    private static final String SORT_DEFAULT = "NO";
    private static final String SORT_ALPHA = "ALPHA";
    private static final String SORT_PRICE = "PRICE";

    public static final String TITLE_COLUMN = "title";
    public static final String PRICE_COLUMN = "price";

    private static final Map<String, Sort> SORT_BY = Map.of(SORT_ALPHA, Sort.by(TITLE_COLUMN),
            SORT_PRICE, Sort.by(PRICE_COLUMN));

    private final ItemRepository itemRepository;
    private final ItemsToItemDtoTriplesMapper itemsToItemDtoTriplesMapper;

    /**
     * Создает сервис работы с товарами.
     *
     * @param itemRepository репозиторий товаров
     * @param itemsToItemDtoTriplesMapper маппер товаров на DTO-представление товаров.
     */
    public ItemServiceImpl(ItemRepository itemRepository, ItemsToItemDtoTriplesMapper itemsToItemDtoTriplesMapper) {
        this.itemRepository = itemRepository;
        this.itemsToItemDtoTriplesMapper = itemsToItemDtoTriplesMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemsPageDto getItemsPage(String search, String sort, Integer pageNumber, Integer pageSize) {
        pageNumber = pageNumber == null ? PAGE_NUMBER_DEFAULT : pageNumber;
        pageSize = pageSize == null ? PAGE_SIZE_DEFAULT : pageSize;
        sort = sort == null ? SORT_DEFAULT : sort;

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, SORT_BY.getOrDefault(sort, Sort.unsorted()));
        Page<Item> page = getPage(search, pageable);

        List<List<ItemDto>> itemDtoTriples = itemsToItemDtoTriplesMapper.mapItems(page.getContent());
        ItemsPagingDto paging = new ItemsPagingDto(pageSize, pageNumber, page.hasPrevious(), page.hasNext());

        return new ItemsPageDto(itemDtoTriples, search, sort, paging);
    }

    private Page<Item> getPage(String search, Pageable pageable) {
        return isNoSearch(search) ? itemRepository.findAll(pageable) :
                itemRepository.findByTitleContainingOrDescriptionContainingAllIgnoreCase(search, search, pageable);
    }

    private static boolean isNoSearch(String search) {
        return search == null || search.isEmpty();
    }
}
