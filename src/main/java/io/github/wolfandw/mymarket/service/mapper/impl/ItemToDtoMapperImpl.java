package io.github.wolfandw.mymarket.service.mapper.impl;

import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.model.Item;
import io.github.wolfandw.mymarket.service.mapper.ItemToDtoMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Реализация {@link ItemToDtoMapper}.
 */
@Component
public class ItemToDtoMapperImpl implements ItemToDtoMapper {
    @Override
    public List<List<ItemDto>> mapToTriples(List<Item> items, Map<Long, Integer> itemsCartCount) {
        List<ItemDto> itemsDto = items.stream().map(item -> mapItem(item,
                itemsCartCount.getOrDefault(item.getId(),0))).toList();
        return convertToTriples(itemsDto);
    }

    @Override
    public ItemDto mapItem(Item item, int count) {
        return new ItemDto(item.getId(),
                item.getTitle(),
                item.getDescription(),
                item.getPrice().longValue(),
                count);
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
                    new ItemDto(-1L, "", "", 0L, 0));
        }
        return result;
    }
}
