package io.github.wolfandw.mymarket.dto;

import org.springframework.web.bind.annotation.RequestParam;

/**
 * Параметры запроса формы товаров.
 */
public class ItemsPageFormRequest {
    private String search;
    private String sort;
    private Integer pageNumber;
    private Integer pageSize;

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
