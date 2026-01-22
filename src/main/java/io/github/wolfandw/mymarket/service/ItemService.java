package io.github.wolfandw.mymarket.service;

import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.dto.ItemsPageDto;

/**
 * Сервис для работы с товарами.
 */
public interface ItemService {
    /**
     * Возвращает DTO-страницу товаров.
     *
     * @param search строка поиска
     * @param sort направление сортировки
     * @param pageNumber номер страницы
     * @param pageSize размер страницы
     * @return DTO-описание страницы товаров
     */
    ItemsPageDto getItemsPage(String search, String sort, Integer pageNumber, Integer pageSize);

    /**
     * Возвращает DTO-товар.
     *
     * @param id идентификатор товара.
     * @return DTO-описание товара
     */
    ItemDto getItem(Long id);
}
