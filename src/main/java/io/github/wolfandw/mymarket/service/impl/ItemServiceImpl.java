package io.github.wolfandw.mymarket.service.impl;

import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.dto.ItemsPageDto;
import io.github.wolfandw.mymarket.dto.ItemsPagingDto;
import io.github.wolfandw.mymarket.model.CartItem;
import io.github.wolfandw.mymarket.model.Item;
import io.github.wolfandw.mymarket.repository.CartItemRepository;
import io.github.wolfandw.mymarket.repository.ItemRepository;
import io.github.wolfandw.mymarket.service.CartService;
import io.github.wolfandw.mymarket.service.ItemService;
import io.github.wolfandw.mymarket.service.mapper.ItemToDtoMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Реализация {@link ItemService}.
 */
@Service("itemService")
public class ItemServiceImpl implements ItemService {
    private static final int PAGE_NUMBER_DEFAULT = 1;
    private static final int PAGE_NUMBER_DELTA = 1;
    private static final int PAGE_SIZE_DEFAULT = 5;

    private static final String SORT_DEFAULT = "NO";
    private static final String SORT_ALPHA = "ALPHA";
    private static final String SORT_PRICE = "PRICE";

    public static final String PRICE_COLUMN = "price";
    public static final String TITLE_COLUMN = "title";

    private static final Map<String, Sort> SORT_BY = Map.of(SORT_ALPHA, Sort.by(TITLE_COLUMN),
            SORT_PRICE, Sort.by(PRICE_COLUMN));

    private final ItemRepository itemRepository;
    private final CartItemRepository cartItemRepository;
    private final ItemToDtoMapper itemToItemDtoMapper;
    private final CartService cartService;

    /**
     * Создает сервис работы с товарами.
     *
     * @param itemRepository      репозиторий товаров
     * @param cartItemRepository  репозиторий строк корзин
     * @param itemToItemDtoMapper маппер товаров на DTO-представление товаров.
     * @param cartService         сервис корзин
     */
    public ItemServiceImpl(ItemRepository itemRepository,
                           CartItemRepository cartItemRepository,
                           ItemToDtoMapper itemToItemDtoMapper,
                           CartService cartService) {
        this.itemRepository = itemRepository;
        this.itemToItemDtoMapper = itemToItemDtoMapper;
        this.cartItemRepository = cartItemRepository;
        this.cartService = cartService;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemsPageDto getItemsPage(String search, String sort, Integer pageNumber, Integer pageSize) {
        pageNumber = pageNumber == null ? PAGE_NUMBER_DEFAULT : pageNumber;
        pageSize = pageSize == null ? PAGE_SIZE_DEFAULT : pageSize;
        sort = sort == null ? SORT_DEFAULT : sort;

        Pageable pageable = PageRequest.of(pageNumber - PAGE_NUMBER_DELTA, pageSize, SORT_BY.getOrDefault(sort, Sort.unsorted()));
        Page<Item> page = getPage(search, pageable);

        List<CartItem> cartItems = cartItemRepository.findAllByCartAndItemIn(cartService.getDefaultCart(), page.getContent());
        Map<Long, Integer> itemsCount = cartItems.stream().collect(Collectors.toMap(cik -> cik.getItem().getId(), CartItem::getCount));

        List<ItemDto> itemsDto = itemToItemDtoMapper.mapItems(page.getContent(), itemsCount);
        List<List<ItemDto>> itemDtoTriples = convertToTriples(itemsDto);

        ItemsPagingDto paging = new ItemsPagingDto(pageSize, pageNumber, page.hasPrevious(), page.hasNext());

        return new ItemsPageDto(itemDtoTriples, search, sort, paging);
    }

    @Override
    public ItemDto getItem(Long id) {
        Optional<Item> item = itemRepository.findById(id);
        Optional<CartItem> cartItem = cartItemRepository.findByCartAndItemId(cartService.getDefaultCart(), id);
        return itemToItemDtoMapper.mapItem(item.orElse(null), cartItem.map(CartItem::getCount).orElse(0));
    }

    private Page<Item> getPage(String search, Pageable pageable) {
        return isNoSearch(search) ? itemRepository.findAll(pageable) :
                itemRepository.findByTitleContainingOrDescriptionContainingAllIgnoreCase(search, search, pageable);
    }

    private List<List<ItemDto>> convertToTriples(List<ItemDto> itemsDto) {
        int itemsSize = itemsDto.size();
        int itemsDtoSize = itemsSize % 3 == 0 ? itemsSize : (itemsSize / 3 * 3 + 3);
        List<List<ItemDto>> result = new ArrayList<>();
        for (int i = 0; i < itemsDtoSize; i++) {
            if (i % 3 == 0) {
                result.add(new ArrayList<>());
            }
            result.getLast().add(i < itemsSize ? itemsDto.get(i) :
                    new ItemDto(-1L, "", "", "", 0L, 0));
        }
        return result;
    }

    private boolean isNoSearch(String search) {
        return search == null || search.isEmpty();
    }


}
