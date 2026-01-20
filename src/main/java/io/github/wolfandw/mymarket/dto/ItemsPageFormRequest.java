package io.github.wolfandw.mymarket.dto;

/**
 * Параметры запроса формы товаров.
 */
public class ItemsPageFormRequest {
    private String search;
    private String sort;
    private Integer pageNumber;
    private Integer pageSize;

    /**
     * Возвращает параметр поиска.
     *
     * @return параметр поиска
     */
    public String getSearch() {
        return search;
    }

    /**
     * Устанавливает параметр поиска.
     *
     * @param search параметр поиска
     */
    public void setSearch(String search) {
        this.search = search;
    }

    /**
     * Возвращает параметр сортировки.
     *
     * @return параметр сортировки
     */
    public String getSort() {
        return sort;
    }

    /**
     * Устанавливает параметр сортировки.
     *
     * @param sort параметр сортировки
     */
    public void setSort(String sort) {
        this.sort = sort;
    }

    /**
     * Возвращает параметр номер страницы.
     *
     * @return параметр номер страницы
     */
    public Integer getPageNumber() {
        return pageNumber;
    }

    /**
     * Устанавливает параметр номер страницы.
     *
     * @param pageNumber параметр номер страницы
     */
    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    /**
     * Возвращает параметр размер страницы.
     *
     * @return параметр размер страницы
     */
    public Integer getPageSize() {
        return pageSize;
    }

    /**
     * Устанавливает параметр размер страницы.
     *
     * @param pageSize параметр размер страницы
     */
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
