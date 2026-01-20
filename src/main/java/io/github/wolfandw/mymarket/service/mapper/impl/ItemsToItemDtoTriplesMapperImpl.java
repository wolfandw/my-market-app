package io.github.wolfandw.mymarket.service.mapper.impl;

import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.model.Item;

import io.github.wolfandw.mymarket.service.mapper.ItemsToItemDtoTriplesMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Реализация {@link ItemsToItemDtoTriplesMapper}.
 */
@Component
public class ItemsToItemDtoTriplesMapperImpl implements ItemsToItemDtoTriplesMapper {
    @Override
    public List<List<ItemDto>> mapItems(List<Item> items) {
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
}
