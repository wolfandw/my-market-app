package io.github.wolfandw.mymarket.service.impl;

import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.dto.ItemsPageDto;
import io.github.wolfandw.mymarket.dto.ItemsPagingDto;
import io.github.wolfandw.mymarket.model.Item;
import io.github.wolfandw.mymarket.repository.ItemRepository;
import io.github.wolfandw.mymarket.service.ItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Реализация {@link ItemService}.
 */
@Service
public class ItemServiceImpl implements ItemService {
    private static final int PAGE_NUMBER_DEFAULT = 1;
    private static final int PAGE_SIZE_DEFAULT = 5;
    private static final String SORT_DEFAULT = "NO";
    private static final String SORT_ALPHA = "ALPHA";
    private static final String SORT_PRICE = "PRICE";

    //private static final Map<String, Sort> SORT_BY = Map.of(SORT_DEFAULT, Sort.unsorted(),
    private static final Map<String, Sort> SORT_BY = Map.of(SORT_ALPHA, Sort.by("title"),
            SORT_PRICE, Sort.by("price"));

    private final ItemRepository itemRepository;

    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemsPageDto getItemsPage(String search, String sort, Integer pageNumber, Integer pageSize) {
        pageNumber = pageNumber == null ? PAGE_NUMBER_DEFAULT : pageNumber;
        pageSize = pageSize == null ? PAGE_SIZE_DEFAULT : pageSize;
        sort = sort == null ? SORT_DEFAULT : sort;
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, SORT_BY.getOrDefault(sort, Sort.unsorted()));
        Page<Item> page = getPage(search, pageable);
        List<List<ItemDto>> tripleList = mapItemsToDtoTriples(page.getContent());
        return new ItemsPageDto(tripleList, search, sort, new ItemsPagingDto(pageSize, pageNumber, page.hasPrevious(), page.hasNext()));
    }

    private List<List<ItemDto>> mapItemsToDtoTriples(List<Item> items) {
        int itemsSize = items.size();
        int itemsDtoSize = itemsSize % 3 == 0 ? itemsSize : (itemsSize / 3 * 3 + 3);
        List<List<ItemDto>> result = new ArrayList<>();
        for (int i = 0; i < itemsDtoSize; i++) {
            if (i % 3 == 0) {
                result.add(new ArrayList<>());
            }
            ItemDto itemDto = new ItemDto(-1L, "", "", "", 0L, 0);
            if (i < itemsSize) {
                Item item = items.get(i);
                itemDto = new ItemDto(item.getId(),
                        item.getTitle(),
                        item.getDescription(),
                        item.getImgPath(),
                        item.getPrice().longValue(),
                        0);
            }
            result.getLast().add(itemDto);
        }
        return result;
    }

    private Page<Item> getPage(String search, Pageable pageable) {
        if (search == null || search.isEmpty()) {
            return itemRepository.findAll(pageable);
        }
        return itemRepository.findByTitleContainingOrDescriptionContainingAllIgnoreCase(search, search, pageable);
    }
}
