package io.github.wolfandw.mymarket.test.service;

import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.dto.ItemsPageDto;
import io.github.wolfandw.mymarket.model.Cart;
import io.github.wolfandw.mymarket.model.Item;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Интеграционные тесты сервиса товаров.
 */
public class ItemServiceTest extends AbstractServiceTest {
    @Test
    public void getItemsDefaultTest() {
        Pageable pageable = PageRequest.of(0, 5, Sort.unsorted());
        List<Item> content = ITEMS.values().stream().limit(5).toList();
        Page<Item> page = new PageImpl<>(content, pageable, content.size());
        when(itemRepository.findAll(pageable)).thenReturn(page);

        Cart cart = CARTS.get(DEFAULT_CART_ID);
        when(cartRepository.findById(DEFAULT_CART_ID)).thenReturn(Optional.ofNullable(cart));
        assert cart != null;
        when(cartItemRepository.findAllByCartAndItemIn(cart, page.getContent())).thenReturn(cart.getItems());

        ItemsPageDto itemsPageDto = itemService.getItems(DEFAULT_CART_ID, null, null, null, null);
        assertThat(itemsPageDto.items()).size().isEqualTo(5);
        assertThat(itemsPageDto.items().get(4).title()).isEqualTo("Item 11");
    }

    @Test
    void getItemTest() {
        Long itemId = 2L;
        Item item = ITEMS.get(itemId);
        when(itemRepository.findById(itemId)).thenReturn(Optional.ofNullable(item));

        Cart cart = CARTS.get(DEFAULT_CART_ID);
        when(cartRepository.findById(DEFAULT_CART_ID)).thenReturn(Optional.ofNullable(cart));
        assert cart != null;
        when(cartItemRepository.findByCartAndItemId(cart, itemId)).thenReturn(Optional.ofNullable(cart.getItems().getFirst()));

        Optional<ItemDto> itemDto = itemService.getItem(DEFAULT_CART_ID, itemId);
        assertThat(itemDto).isPresent();
        assertThat(itemDto.get().title()).isEqualTo("Item 08");
    }

    @Test
    void getItemEmptyTest() {
        Long itemId = 14L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        Cart cart = CARTS.get(DEFAULT_CART_ID);
        when(cartRepository.findById(DEFAULT_CART_ID)).thenReturn(Optional.empty());
        assert cart != null;
        when(cartItemRepository.findByCartAndItemId(cart, itemId)).thenReturn(Optional.empty());

        Optional<ItemDto> itemDto = itemService.getItem(DEFAULT_CART_ID, 14L);
        assertThat(itemDto).isEmpty();
    }

    @Test
    void createItem() {
        Long itemId = 14L;
        String title = "Item 14";
        String description = "Item 14 description";
        BigDecimal price = BigDecimal.valueOf(14);

        Item item = new Item(itemId, title, description, null, price);
        when(itemRepository.save(Mockito.any(Item.class))).thenReturn(item);

        ItemDto itemDto = itemService.createItem(title, description, price);
        assertThat(itemDto.title()).isEqualTo(title);
    }
}
