package io.github.wolfandw.mymarket.service.impl;

import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.dto.ItemsPageDto;
import io.github.wolfandw.mymarket.dto.ItemsPagingDto;
import io.github.wolfandw.mymarket.model.CartItem;
import io.github.wolfandw.mymarket.model.Item;
import io.github.wolfandw.mymarket.repository.CartItemRepository;
import io.github.wolfandw.mymarket.repository.CartRepository;
import io.github.wolfandw.mymarket.repository.ItemRepository;
import io.github.wolfandw.mymarket.service.EntityImageService;
import io.github.wolfandw.mymarket.service.ItemService;
import io.github.wolfandw.mymarket.service.mapper.ItemToDtoMapper;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

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

    private static final String JPG = "jpg";
    private static final Logger log = LoggerFactory.getLogger(ItemServiceImpl.class);

    private final ItemRepository itemRepository;
    private final CartItemRepository cartItemRepository;
    private final ItemToDtoMapper itemToItemDtoMapper;
    private final CartRepository cartRepository;

    /**
     * Создает сервис работы с товарами.
     *
     * @param itemRepository      репозиторий товаров
     * @param cartItemRepository  репозиторий строк корзин
     * @param itemToItemDtoMapper маппер товаров на DTO-представление товаров.
     * @param cartRepository      репозиторий корзин
     */
    public ItemServiceImpl(ItemRepository itemRepository,
                           CartItemRepository cartItemRepository,
                           ItemToDtoMapper itemToItemDtoMapper,
                           CartRepository cartRepository,
                           EntityImageService entityImageService) {
        this.itemRepository = itemRepository;
        this.itemToItemDtoMapper = itemToItemDtoMapper;
        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemsPageDto getItems(Long cartId, String search, String sort, Integer pageNumber, Integer pageSize) {
        pageNumber = pageNumber == null ? PAGE_NUMBER_DEFAULT : pageNumber;
        pageSize = pageSize == null ? PAGE_SIZE_DEFAULT : pageSize;
        sort = sort == null ? SORT_DEFAULT : sort;

        Page<Item> page = getPage(search, sort, pageNumber, pageSize);
        Map<Long, Integer> itemsCount = getItemsCount(cartId, page);
        List<List<ItemDto>> itemDtoTriples = getTriples(page, itemsCount);
        ItemsPagingDto paging = new ItemsPagingDto(pageSize, pageNumber, page.hasPrevious(), page.hasNext());

        return new ItemsPageDto(itemDtoTriples, search, sort, paging);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ItemDto> getItem(Long cartId, Long id) {
        return itemRepository.findById(id).map(item -> {
            Integer count = cartRepository.findById(cartId).
                    map(c -> cartItemRepository.findByCartAndItemId(c, id).
                            map(CartItem::getCount).orElse(0)).orElse(0);
            return itemToItemDtoMapper.mapItem(item, count);
        });
    }

    @Override
    @Transactional
    public ItemDto createItem(String title, String description, BigDecimal price) {
        Item item = new Item();
        item.setTitle(title);
        item.setDescription(description);
        item.setPrice(price);
        return itemToItemDtoMapper.mapItem(itemRepository.save(item), 0);
    }

    private String getImageFileName(MultipartFile imageFile, Long itemId){
        if (imageFile == null) {
            return null;
        }
        String originName = imageFile.getOriginalFilename();
        String extension = getImageExtension(originName);
        return itemId.toString() + "." + extension;
    }

    private String getImageExtension(String originName) {
        if (originName != null && originName.lastIndexOf('.') != -1) {
            String extension = originName.substring(originName.lastIndexOf('.') + 1).toLowerCase();
            if (!extension.isEmpty()) {
                return extension;
            }
        }
        return JPG;
    }

    private @NonNull List<List<ItemDto>> getTriples(Page<Item> page, Map<Long, Integer> itemsCount) {
        List<ItemDto> itemsDto = itemToItemDtoMapper.mapItems(page.getContent(), itemsCount);
        return convertToTriples(itemsDto);
    }

    private @NonNull Map<Long, Integer> getItemsCount(Long cartId, Page<Item> page) {
        return cartRepository.findById(cartId).map(cart -> {
            List<CartItem> cartItems = cartItemRepository.findAllByCartAndItemIn(cart, page.getContent());
            return cartItems.stream().collect(Collectors.toMap(cartItemKey -> cartItemKey.getItem().getId(), CartItem::getCount));
        }).orElse(Collections.emptyMap());
    }

    private Page<Item> getPage(String search, String sort, Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - PAGE_NUMBER_DELTA, pageSize, SORT_BY.getOrDefault(sort, Sort.unsorted()));
        return getPage(search, pageable);
    }

    private Page<Item> getPage(String search, Pageable pageable) {
        if (isNoSearch(search)) {
            return itemRepository.findAll(pageable);
        }
        return itemRepository.findByTitleContainingOrDescriptionContainingAllIgnoreCase(search, search, pageable);
    }

    private List<List<ItemDto>> convertToTriples(List<ItemDto> itemsDto) {
        int itemsSize = itemsDto.size();
        int itemsDtoSize = itemsSize % 3 == 0 ? itemsSize : (itemsSize / 3 * 3 + 3);
        List<List<ItemDto>> result = new ArrayList<>();
        for (int i = 0; i < itemsDtoSize; i++) {
            if (i % 3 == 0) {
                result.add(new ArrayList<>());
            }
            result.getLast().add(i < itemsSize ? itemsDto.get(i) : createItemDtoStub());
        }
        return result;
    }

    private @NonNull ItemDto createItemDtoStub() {
        return new ItemDto(-1L, "", "", 0L, 0);
    }

    private boolean isNoSearch(String search) {
        return search == null || search.isEmpty();
    }
}
