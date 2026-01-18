package io.github.wolfandw.mymarket.dto;

/**
 * DTO-описание пагинации страницы товаров.
 *
 * @param pageSize размер страницы
 * @param pageNumber номер страницы
 * @param hasPrevious есть предыдущие
 * @param hasNext есть следующие
 */
public record ItemsPagingDto(int pageSize,
                             int pageNumber,
                             boolean hasPrevious,
                             boolean hasNext) {
}
