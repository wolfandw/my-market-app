package io.github.wolfandw.mymarket.dto;

/**
 * Параметры запроса изменения количества из формы товара.
 */
public class ItemPageChangeCountFormRequest {
    private Long id;
    private String action;

    /**
     * Возвращает идентификатор товара.
     *
     * @return идентификатор товара
     */
    public Long getId() {
        return id;
    }

    /**
     * Устанавливает идентификатор товара.
     *
     * @param id идентификатор товара
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Возвращает параметр действия с количеством.
     *
     * @return параметр действия с количеством
     */
    public String getAction() {
        return action;
    }

    /**
     * Устанавливает параметр действия с количеством.
     *
     * @param action параметр действия с количеством
     */
    public void setAction(String action) {
        this.action = action;
    }
}
