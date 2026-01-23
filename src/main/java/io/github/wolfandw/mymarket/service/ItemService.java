package io.github.wolfandw.mymarket.service;

import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.dto.ItemsPageDto;

import java.util.Optional;

/**
 * Сервис для работы с товарами.
 */
public interface ItemService {
    /**
     * Возвращает DTO-страницу товаров.
     *
     * @param cartId идентификатор корзины
     * @param search строка поиска
     * @param sort направление сортировки
     * @param pageNumber номер страницы
     * @param pageSize размер страницы
     * @return DTO-описание страницы товаров
     */
    ItemsPageDto getItems(Long cartId, String search, String sort, Integer pageNumber, Integer pageSize);

    /**
     * Возвращает DTO-товар.
     *
     * @param cartId идентификатор корзины
     * @param id     идентификатор товара.
     * @return DTO-описание товара
     */
    Optional<ItemDto> getItem(Long cartId, Long id);
}
