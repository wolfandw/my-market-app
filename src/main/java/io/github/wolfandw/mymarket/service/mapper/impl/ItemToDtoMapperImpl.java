package io.github.wolfandw.mymarket.service.mapper.impl;

import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.model.Item;

import io.github.wolfandw.mymarket.service.mapper.ItemToDtoMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Реализация {@link ItemToDtoMapper}.
 */
@Component
public class ItemToDtoMapperImpl implements ItemToDtoMapper {
    @Override
    public List<ItemDto> mapItems(List<Item> items, Map<Long, Integer> itemsCount) {
        return items.stream().map(item -> mapItem(item,
                itemsCount.getOrDefault(item.getId(),0))).collect(Collectors.toList());
    }

    @Override
    public ItemDto mapItem(Item item, int count) {
        return new ItemDto(item.getId(),
                item.getTitle(),
                item.getDescription(),
                item.getImgPath(),
                item.getPrice().longValue(),
                count);
    }
}
